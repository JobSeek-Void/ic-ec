package team.jsv.icec.util

import android.app.Activity
import android.os.Build
import android.util.DisplayMetrics
import androidx.fragment.app.Fragment

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

val Activity.deviceHeight
    get() = run {
        val displayMetrics = DisplayMetrics()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            windowManager.currentWindowMetrics.bounds.height()
        } else {
            @Suppress("DEPRECATION") val display = windowManager.defaultDisplay
            @Suppress("DEPRECATION") display.getRealMetrics(displayMetrics)
            displayMetrics.heightPixels
        }
    }

val Fragment.deviceWidth
    get() = requireActivity().deviceWidth

val Fragment.deviceHeight
    get() = requireActivity().deviceHeight