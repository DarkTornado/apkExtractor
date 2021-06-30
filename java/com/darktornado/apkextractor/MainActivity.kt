package com.darktornado.apkextractor

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.Toast
import com.darktornado.listview.Item
import com.darktornado.listview.ListAdapter

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionCheck()

        val apps = loadAppList()
        val items = ArrayList<Item>()
        for (app in apps) {
            items.add(Item(app!!.name, app.packageName, app.icon))
        }

        val layout = LinearLayout(this)
        layout.orientation = 1
        val list = ListView(this)
        val adapter = ListAdapter()
        adapter.setItems(items)
        list.adapter = adapter
        list.isFastScrollEnabled = true
        layout.addView(list)
        val pad = dip2px(16)
        list.setPadding(pad, pad, pad, pad)
        setContentView(layout)
    }

    private fun loadAppList(): Array<AppInfo?> {
        val appList = packageManager.getInstalledPackages(PackageManager.GET_META_DATA)
        val apps = arrayOfNulls<AppInfo>(appList.size)
        for (n in appList.indices) {
            val app = appList.get(n)
            val name = app.applicationInfo.loadLabel(packageManager).toString()
            val packageName = app.packageName
            val icon = app.applicationInfo.loadIcon(packageManager)
            apps[n] = AppInfo(name, packageName, icon)
        }
        return apps
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
