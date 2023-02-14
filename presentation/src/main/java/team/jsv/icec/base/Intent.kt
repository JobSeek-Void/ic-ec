@file:SuppressLint("NewApi")

package team.jsv.icec.base

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent


inline fun <reified T : Activity> Activity.changeActivityWithAnimation(
    intentBuilder: Intent.() -> Intent = { this },
) {
    startActivityWithAnimation<T>(
        intentBuilder = intentBuilder,
        withFinish = true,
    )
}

inline fun <reified T : Activity> Activity.startActivityWithAnimation(
    intentBuilder: Intent.() -> Intent = { this },
    withFinish: Boolean = false,
) {
    startActivity(intentBuilder(Intent(this, T::class.java)))
    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    if (withFinish) finish()
}

fun Activity.finishWithAnimation() {
    finish()
    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
}