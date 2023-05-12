package team.jsv.icec.util

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import team.jsv.icec.ui.camera.CameraFragment
import team.jsv.icec.ui.camera.SettingRatio
import kotlin.math.roundToInt

enum class ConnenctState(val id: Int) {
    TOP(0),
    BOTTOM(1),
    TOPBOTTOM(2),
}

object SettingViewUtil {
    fun resizeView(
        layoutParams: ViewGroup.LayoutParams,
        ratioValue: Int,
        deviceWidth: Int,
        deviceHeight: Int
    ): ViewGroup.LayoutParams {
        when (ratioValue) {
            SettingRatio.RATIO_1_1.id -> {
                layoutParams.height = (deviceWidth * SettingRatio.AMOUNT_1_1).roundToInt()
            }

            SettingRatio.RATIO_3_4.id -> {
                layoutParams.height = (deviceWidth * SettingRatio.AMOUNT_3_4).roundToInt()
            }

            SettingRatio.RATIO_9_16.id -> {
                layoutParams.height = (deviceWidth * SettingRatio.AMOUNT_9_16).roundToInt()
            }

            SettingRatio.RATIO_FULL.id -> {
                layoutParams.height = deviceHeight
            }
        }

        return layoutParams
    }

    fun reconnectView(
        constraintLayout: ConstraintLayout,
        targetView: View,
        topView: View,
        topViewSet: Int,
        bottomView: View,
        bottomViewSet: Int,
        connectStateValue: Int
    ) {
        val constraintSet = ConstraintSet()
        constraintSet.clone(constraintLayout)

        constraintSet.connect(
            targetView.id,
            ConstraintSet.LEFT,
            ConstraintSet.PARENT_ID,
            ConstraintSet.LEFT
        )
        constraintSet.connect(
            targetView.id,
            ConstraintSet.RIGHT,
            ConstraintSet.PARENT_ID,
            ConstraintSet.RIGHT
        )

        when (connectStateValue) {
            ConnenctState.TOP.id -> {
                constraintSet.clear(targetView.id, ConstraintSet.BOTTOM)

                constraintSet.connect(
                    targetView.id,
                    ConstraintSet.TOP,
                    topView.id,
                    topViewSet
                )
            }
            ConnenctState.BOTTOM.id -> {
                constraintSet.clear(targetView.id, ConstraintSet.TOP)

                constraintSet.connect(
                    targetView.id,
                    ConstraintSet.BOTTOM,
                    bottomView.id,
                    bottomViewSet
                )
            }
            ConnenctState.TOPBOTTOM.id -> {
                constraintSet.connect(
                    targetView.id,
                    ConstraintSet.TOP,
                    topView.id,
                    topViewSet
                )
                constraintSet.connect(
                    targetView.id,
                    ConstraintSet.BOTTOM,
                    bottomView.id,
                    bottomViewSet
                )
            }
            else -> {}
        }.apply {
            constraintSet.applyTo(constraintLayout)
        }
    }

}