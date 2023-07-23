package team.jsv.icec.util

import android.app.Activity
import android.os.Build
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import team.jsv.presentation.R

fun Activity.setFullScreen() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
    } else {
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or
            (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    }
}

fun Activity.setSystemUITransparent() {
    window.apply {
        setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
    }
    if(Build.VERSION.SDK_INT == Build.VERSION_CODES.R) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
    }
}

fun Activity.showSystemUI() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        window.insetsController?.show(
            WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
    } else {
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    }
}

fun Activity.hideSystemUI() {
    hideStatusBar()
    hideNavigationBar()
}

fun Activity.hideStatusBar() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        window.insetsController?.hide(WindowInsets.Type.statusBars())
    } else {
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or
            (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }
}

fun Activity.hideNavigationBar() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        window.insetsController?.hide(WindowInsets.Type.navigationBars())
    } else {
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or
            (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
    }
}

fun Activity.setBehaviorSystemUI() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        window.insetsController?.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    } else {
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
    }
}

fun Activity.setICECThemeBottomNavigationColor() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
        window.navigationBarColor = ContextCompat.getColor(this, R.color.SystemColor)
    } else {
        window.navigationBarColor = ContextCompat.getColor(this, R.color.black)
    }
}