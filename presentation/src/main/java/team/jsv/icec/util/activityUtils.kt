package team.jsv.icec.util

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import team.jsv.presentation.R

fun Context.showToast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

fun View.showSnackBarAction(msg: String, textColor: Int, backgroundColor: Int) {
    val snackBar = Snackbar.make(this, msg, Snackbar.LENGTH_LONG)
    this.initSnackBarSetting(snackBar)
    setSnackBarOption(snackBar, textColor, backgroundColor)
    snackBar.show()
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