package team.jsv.icec.util

import com.google.android.material.slider.Slider

fun Slider.setSliderValues(
    value: Float,
    valueFrom: Float,
    valueTo: Float,
    stepSize: Float,
    haloRadius:Int = 0,
) {
    this.value = value
    this.valueFrom = valueFrom
    this.valueTo = valueTo
    this.stepSize = stepSize
    this.haloRadius = haloRadius
}