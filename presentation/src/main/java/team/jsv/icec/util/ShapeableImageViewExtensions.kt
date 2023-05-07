package team.jsv.icec.util

import android.content.res.ColorStateList
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.imageview.ShapeableImageView

fun ShapeableImageView.setStokeColor(color: Int) {
    strokeColor = ColorStateList.valueOf(ResourcesCompat.getColor(resources, color, null))
}
