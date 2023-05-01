package team.jsv.icec.ui.main.mosaic.detect

import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.BindingAdapter
import com.google.android.material.button.MaterialButton
import team.jsv.presentation.R

@BindingAdapter("allSelectBackground")
fun MaterialButton.changeBackground(isAllSelect: Boolean) {
    text = if (isAllSelect) {
        setBackgroundColor(ResourcesCompat.getColor(resources, R.color.SubColor, null))
        context.getString(R.string.deselect_all_text)
    } else {
        setBackgroundColor(ResourcesCompat.getColor(resources, R.color.gray1, null))
        context.getString(R.string.select_all_text)
    }
}

@BindingAdapter("detectedFaceCount")
fun TextView.applyFaceCount(count: Int) {
    text = context.getString(R.string.detected_face_count, count)
}