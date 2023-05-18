package team.jsv.icec.ui.main.mosaic.detect

import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.BindingAdapter
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import team.jsv.icec.util.dp
import team.jsv.icec.util.gone
import team.jsv.icec.util.setStokeColor
import team.jsv.icec.util.visible
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

@BindingAdapter("checkingVisible")
fun ImageView.setCheckingVisible(isSelect: Boolean) {
    if (isSelect) {
        visible()
    } else {
        gone()
    }
}

@BindingAdapter("strokeFor")
fun ShapeableImageView.strokeFor(isSelect: Boolean) {
    if (isSelect) {
        strokeWidth = 4.dp
        setStokeColor(R.color.SubColor)
    } else {
        setStokeColor(R.color.transparent)
    }
}
