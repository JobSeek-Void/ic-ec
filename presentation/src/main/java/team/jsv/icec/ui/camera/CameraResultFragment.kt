package team.jsv.icec.ui.camera

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.activityViewModels
import team.jsv.icec.base.BaseFragment
import team.jsv.icec.ui.main.MainActivity
import team.jsv.icec.util.ConnenctState
import team.jsv.icec.util.SettingViewUtil
import team.jsv.presentation.R
import team.jsv.presentation.databinding.FragmentCameraResultBinding
import java.text.SimpleDateFormat
import java.util.Locale

class CameraResultFragment :
    BaseFragment<FragmentCameraResultBinding>(R.layout.fragment_camera_result) {

    companion object {
        const val JPEG_MIME_TYPE = "image/jpeg"
        const val MEDIA_DIRECTORY_PATH = "/ICEC IMAGE"
        const val EXTERNAL_STORAGE_DIRECTORY_PATH = "/Pictures/ICEC IMAGE"
    }

    private val viewModel: CameraViewModel by activityViewModels()

    private val deviceHeight get() = screenHeight(requireActivity())
    private val deviceWidth get() = screenWidth(requireActivity())

    override fun onResume() {
        super.onResume()
        initClickEvent()
    }

    private fun screenHeight(activity: Activity): Int {
        val displayMetrics = DisplayMetrics()

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            activity.windowManager.currentWindowMetrics.bounds.height()
        } else {
            @Suppress("DEPRECATION") val display = activity.windowManager.defaultDisplay
            @Suppress("DEPRECATION") display.getMetrics(displayMetrics)
            displayMetrics.heightPixels
        }
    }

    private fun screenWidth(activity: Activity): Int {
        val displayMetrics = DisplayMetrics()

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            activity.windowManager.currentWindowMetrics.bounds.width()
        } else {
            @Suppress("DEPRECATION") val display = activity.windowManager.defaultDisplay
            @Suppress("DEPRECATION") display.getMetrics(displayMetrics)
            displayMetrics.widthPixels
        }
    }

    override fun initView() {
        binding.ivPreviewImage.setImageBitmap(viewModel.bitmapImage.value)
        setBackBtMargin()
        observeRatio()
    }

    private fun setBackBtMargin() {
        val layoutParams = binding.ivBack.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.setMargins(0, SettingViewUtil.getStatusBarHeightDP(requireContext()), 0, 0)
        binding.ivBack.layoutParams = layoutParams
    }

    private fun initClickEvent() {
        binding.ivBack.setOnClickListener {
            popBackStack()
        }

        binding.ivUseImage.setOnClickListener {
            viewModel.bitmapImage.value?.let { bitmapImage -> saveImage(bitmapImage) }
        }
    }

    private fun observeRatio() {
        viewModel.ratioState.observe(this) { ratioState ->
            val layoutParams = binding.ivPreviewImage.layoutParams

            binding.ivPreviewImage.layoutParams =
                SettingViewUtil.resizeView(layoutParams, ratioState, deviceWidth, deviceHeight)


            when (ratioState) {
                SettingRatio.RATIO_1_1.id -> {
                    SettingViewUtil.reconnectView(
                        constraintLayout = binding.constraintLayout,
                        targetView = binding.ivPreviewImage,
                        topView = binding.ivUseImage,
                        topViewSet = ConstraintSet.BOTTOM,
                        bottomView = binding.viewEmptyBox,
                        bottomViewSet = ConstraintSet.TOP,
                        connectStateValue = ConnenctState.TOPBOTTOM.id
                    )
                }

                SettingRatio.RATIO_3_4.id -> {
                    SettingViewUtil.reconnectView(
                        constraintLayout = binding.constraintLayout,
                        targetView = binding.ivPreviewImage,
                        topView = binding.ivUseImage,
                        topViewSet = ConstraintSet.BOTTOM,
                        bottomView = binding.viewEmptyBox,
                        bottomViewSet = ConstraintSet.TOP,
                        connectStateValue = ConnenctState.TOPBOTTOM.id
                    )
                }

                SettingRatio.RATIO_9_16.id -> {
                    SettingViewUtil.reconnectView(
                        constraintLayout = binding.constraintLayout,
                        targetView = binding.ivPreviewImage,
                        topView = binding.constraintLayout,
                        topViewSet = ConstraintSet.TOP,
                        bottomView = binding.constraintLayout,
                        bottomViewSet = ConstraintSet.BOTTOM,
                        connectStateValue = ConnenctState.BOTTOM.id
                    )
                }

                SettingRatio.RATIO_FULL.id -> {
                    SettingViewUtil.reconnectView(
                        constraintLayout = binding.constraintLayout,
                        targetView = binding.ivPreviewImage,
                        topView = binding.constraintLayout,
                        topViewSet = ConstraintSet.TOP,
                        bottomView = binding.constraintLayout,
                        bottomViewSet = ConstraintSet.BOTTOM,
                        connectStateValue = ConnenctState.TOPBOTTOM.id
                    )
                }
            }
        }
    }

    }
}