package com.darktornado.apkextractor

import android.app.Activity
import android.os.Bundle
import android.widget.*
import com.darktornado.library.LicenseView

class LicenseActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layout = LinearLayout(this)
        layout.orientation = 1

        val lv1 = LicenseView(this)
        lv1.setTitle("License View")
        lv1.setSubtitle("  by Dark Tornado, BSD 3-Clause License")
        lv1.setLicense("BSD 3-Clause License", "LicenseView.txt")
        layout.addView(lv1)
        val lv2 = LicenseView(this)
        lv2.setTitle("Material Design")
        lv2.setSubtitle("  by Google, Apache License 2.0")
        lv2.setLicense("Apache License 2.0", "MaterialDesign.txt")
        layout.addView(lv2)

        val pad = dip2px(16);
        layout.setPadding(pad, pad, pad, pad)
        val scroll = ScrollView(this)
        scroll.addView(layout)
        setContentView(scroll)
    }


    private fun toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

    private fun dip2px(dips: Int) = Math.ceil(dips * resources.displayMetrics.density.toDouble()).toInt()

}