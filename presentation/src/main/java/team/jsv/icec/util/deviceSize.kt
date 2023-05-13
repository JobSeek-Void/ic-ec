package team.jsv.icec.util

import android.app.Activity
import android.os.Build
import android.util.DisplayMetrics

val Activity.deviceHeight
    get() = run {
        val displayMetrics = DisplayMetrics()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            windowManager.currentWindowMetrics.bounds.height()
        } else {
            @Suppress("DEPRECATION") val display = windowManager.defaultDisplay
            @Suppress("DEPRECATION") display.getMetrics(displayMetrics)
            displayMetrics.heightPixels
        }
    }

val Activity.deviceWidth
    get() = run {
        val displayMetrics = DisplayMetrics()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            windowManager.currentWindowMetrics.bounds.width()
        } else {
            @Suppress("DEPRECATION") val display = windowManager.defaultDisplay
            @Suppress("DEPRECATION") display.getMetrics(displayMetrics)
            displayMetrics.widthPixels
        }
    }