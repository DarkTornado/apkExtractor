package com.darktornado.apkextractor

import android.graphics.drawable.Drawable

data class AppInfo(val name: String, val packageName: String, val icon: Drawable, val path: String) : Comparable<AppInfo> {

    override fun compareTo(other: AppInfo): Int {
        val result = this.name.compareTo(other.name)
        if (result == 0) return this.packageName.compareTo(other.packageName)
        return result;
    }

}