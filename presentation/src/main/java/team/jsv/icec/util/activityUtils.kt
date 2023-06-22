package team.jsv.icec.util

import android.app.Activity
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.WindowCompat
import com.google.android.material.snackbar.Snackbar
import team.jsv.presentation.R

fun Context.showToast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

fun View.showSnackBarAction(msg: Int, textColor: Int, backgroundColor: Int) {
    val snackBar = Snackbar.make(this, context.getString(msg), Snackbar.LENGTH_LONG)
    this.initSnackBarSetting(snackBar)
    setSnackBarOption(snackBar, context.getColor(textColor), context.getColor(backgroundColor))
    snackBar.show()
}

/* root View 를 제공해야합니다 */
fun Activity.hideSystemUI(view : View) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        window.setDecorFitsSystemWindows(false)
        window.insetsController?.hide(WindowInsets.Type.statusBars())
        setBottomMargin(view, this)
    } else {
        @Suppress("DEPRECATION")
        // API 30 이상에서는 deprecated 됨 @jiiiiiyoon
        window.decorView.systemUiVisibility =
            (View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
    }
}

private fun setBottomMargin(view : View, context: Context) {
    val layoutParams =
        view.layoutParams as ViewGroup.MarginLayoutParams
    layoutParams.setMargins(
        0, 0, 0,
        SettingViewUtil.getNavigationBarHeightDP(context)
    )

    view.layoutParams = layoutParams
}

private fun View.initSnackBarSetting(snackBar: Snackbar) {
    val snackBarView = snackBar.view
    val textView =
        snackBarView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)

    textView.textAlignment = View.TEXT_ALIGNMENT_CENTER

    val backgroundDrawable = snackBarView.background as GradientDrawable

    backgroundDrawable.cornerRadius =
        resources.getDimensionPixelOffset(R.dimen.snackbar_corner_radius).toFloat()

    val params = snackBarView.layoutParams as ViewGroup.MarginLayoutParams

    params.setMargins(
        params.leftMargin + resources.getDimensionPixelOffset(R.dimen.snackbar_margin_start),
        params.topMargin,
        params.rightMargin + resources.getDimensionPixelOffset(R.dimen.snackbar_margin_end),
        params.bottomMargin + resources.getDimensionPixelOffset(R.dimen.snackbar_margin_bottom)
    )

    snackBarView.layoutParams = params
}

private fun setSnackBarOption(snackBar: Snackbar, textColor: Int, backgroundColor: Int) {
    snackBar.setTextColor(textColor)
    snackBar.setBackgroundTint(backgroundColor)
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
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        window.insetsController?.let {
            it.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
            it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    } else {
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    }
}