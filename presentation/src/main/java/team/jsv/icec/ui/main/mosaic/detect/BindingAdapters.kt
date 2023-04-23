package team.jsv.icec.ui.main.mosaic.detect

import androidx.core.content.res.ResourcesCompat
import androidx.databinding.BindingAdapter
import com.google.android.material.button.MaterialButton
import team.jsv.presentation.R

@BindingAdapter("allSelectBackground")
fun MaterialButton.changeBackground(isAllSelect: Boolean) {
    if (isAllSelect) {
        setBackgroundColor(ResourcesCompat.getColor(resources, R.color.SubColor, null))
    } else {
        setBackgroundColor(ResourcesCompat.getColor(resources, R.color.gray2, null))
    }
}
