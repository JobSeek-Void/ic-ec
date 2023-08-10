package team.jsv.icec.util

import android.content.res.Resources

val Float.toPx: Float
    get() = this * Resources.getSystem().displayMetrics.density

val Int.toPx: Float
    get() = this.toFloat() * Resources.getSystem().displayMetrics.density

val Float.toDp: Float
    get() = this / Resources.getSystem().displayMetrics.density

val Int.toDp: Float
    get() = this.toFloat() / Resources.getSystem().displayMetrics.density