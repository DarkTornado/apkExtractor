package com.darktornado.apkextractor

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.Gravity
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import com.darktornado.listview.Item
import com.darktornado.listview.ListAdapter
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class MainActivity : Activity() {

    private val sdcard = Environment.getExternalStorageDirectory().absolutePath

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layout = LinearLayout(this)
        layout.orientation = 1
        permissionCheck()

        val bar = ProgressBar(this)
        bar.setPadding(0, 0, 0, dip2px(10))
        layout.addView(bar)
        val txt = TextView(this)
        txt.text = "Loading Apps..."
        txt.textSize = 14f
        txt.setTextColor(Color.BLACK)
        txt.gravity = Gravity.CENTER
        layout.addView(txt)
        layout.gravity = Gravity.CENTER or Gravity.CENTER_VERTICAL

        Thread({ loadApps(layout, txt) }).start()
        setContentView(layout)
    }

    private fun loadApps(layout: LinearLayout, txt: TextView) {
        val apps = loadAppList(txt)
        val items = ArrayList<Item>()
        for (app in apps) {
            items.add(Item(app!!.name, app.packageName, app.icon))
        }
        runOnUiThread {
            layout.removeAllViews()
            layout.gravity = Gravity.TOP
            applyAppList(apps, items, layout)
        }
    }

    private fun applyAppList(apps: Array<AppInfo?>, items: ArrayList<Item>, layout: LinearLayout) {
        val list = ListView(this)
        val adapter = ListAdapter()
        adapter.setItems(items)
        list.adapter = adapter
        list.isFastScrollEnabled = true
        list.onItemClickListener = OnItemClickListener { parent: AdapterView<*>?, view: View?, pos: Int, id: Long ->
            File("$sdcard/ApkExtract/").mkdirs()
            extractApkFile(apps.get(pos)!!)
        }
        layout.addView(list)
        val pad = dip2px(16)
        list.setPadding(pad, pad, pad, pad)
    }

    private fun loadAppList(txt: TextView): Array<AppInfo?> {
        val appList = packageManager.getInstalledPackages(PackageManager.GET_META_DATA)
        val apps = arrayOfNulls<AppInfo>(appList.size)
        runOnUiThread { txt.text = "Loading Apps...(0/${apps.size})" }
        for (n in appList.indices) {
            val app = appList.get(n)
            val name = app.applicationInfo.loadLabel(packageManager).toString()
            val packageName = app.packageName
            val icon = resizeDrawable(app.applicationInfo.loadIcon(packageManager))
            val path = app.applicationInfo.sourceDir
            apps[n] = AppInfo(name, packageName, icon, path)
            runOnUiThread { txt.text = "Loading Apps...($n/${apps.size})" }
        }
        apps.sort()
        return apps
    }

    private fun resizeDrawable(drawable: Drawable): Drawable {
        val bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        val canvas = Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return BitmapDrawable(Bitmap.createScaledBitmap(bitmap, dip2px(96), dip2px(96), false))
    }

    private fun extractApkFile(app: AppInfo) {
        try {
            val file1 = File(app.path)
            val file2 = File("$sdcard/ApkExtract/${app.name}.apk")
            val ch1 = FileInputStream(file1).channel
            val ch2 = FileOutputStream(file2).channel
            ch1.transferTo(0, ch1.size(), ch2)
            toast(".apk file is extracted.")
        } catch (e: Exception) {
            toast("Extraction is Failed.\n$e")
        }
    }

    private fun permissionCheck() {
        if (Build.VERSION.SDK_INT < 23) return
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
            toast("Please allow permissions.")
        }
    }

    private fun toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

    private fun dip2px(dips: Int) = Math.ceil(dips * resources.displayMetrics.density.toDouble()).toInt()

}
