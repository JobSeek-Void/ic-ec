package team.jsv.icec.util

import android.content.res.Resources

val Float.dp: Float
    get() = this * Resources.getSystem().displayMetrics.density

val Int.dp: Float
    get() = this.toFloat() * Resources.getSystem().displayMetrics.density