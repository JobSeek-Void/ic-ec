package team.jsv.icec.util

import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import team.jsv.icec.ui.camera.ConnenctState
import team.jsv.icec.ui.camera.SettingRatio
import kotlin.math.roundToInt

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
        targetViewGroup: ViewGroup,
        topView: View,
        topViewSet: Int,
        bottomView: View,
        bottomViewSet: Int,
        connectStateValue: Int
    ) {

        val constraintSet = ConstraintSet()
        constraintSet.clone(constraintLayout)

        constraintSet.connect(
            targetViewGroup.id,
            ConstraintSet.LEFT,
            ConstraintSet.PARENT_ID,
            ConstraintSet.LEFT
        )
        constraintSet.connect(
            targetViewGroup.id,
            ConstraintSet.RIGHT,
            ConstraintSet.PARENT_ID,
            ConstraintSet.RIGHT
        )

        when (connectStateValue) {
            ConnenctState.TOP.id -> {
                constraintSet.connect(
                    targetViewGroup.id,
                    ConstraintSet.TOP,
                    topView.id,
                    topViewSet
                )
            }
            ConnenctState.BOTTOM.id -> {
                constraintSet.connect(
                    targetViewGroup.id,
                    ConstraintSet.BOTTOM,
                    bottomView.id,
                    bottomViewSet
                )
            }
            ConnenctState.TOPBOTTOM.id -> {
                constraintSet.connect(
                    targetViewGroup.id,
                    ConstraintSet.TOP,
                    topView.id,
                    topViewSet
                )
                constraintSet.connect(
                    targetViewGroup.id,
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