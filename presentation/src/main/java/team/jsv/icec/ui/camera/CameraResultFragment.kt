package team.jsv.icec.ui.camera

import android.content.Intent
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import team.jsv.icec.base.BaseFragment
import team.jsv.icec.ui.main.MainActivity
import team.jsv.icec.util.ConnenctState
import team.jsv.icec.util.SettingViewUtil
import team.jsv.icec.util.deviceHeight
import team.jsv.icec.util.deviceWidth
import team.jsv.icec.util.saveImage
import team.jsv.presentation.R
import team.jsv.presentation.databinding.FragmentCameraResultBinding

class CameraResultFragment :
    BaseFragment<FragmentCameraResultBinding>(R.layout.fragment_camera_result) {

    private val viewModel: CameraViewModel by activityViewModels()

    override fun onResume() {
        super.onResume()

        initClickEvent()
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
            viewModel.bitmapImage.value?.let { bitmapImage ->
                lifecycleScope.launch {
                    requireActivity().run {
                        saveImage(bitmapImage)
                        startActivity(Intent(requireActivity(), MainActivity::class.java))
                        finish()
                    }
                }
            }
        }
    }

    private fun observeRatio() {
        viewModel.ratioState.observe(this) { ratioState ->
            val layoutParams = binding.ivPreviewImage.layoutParams

            binding.ivPreviewImage.layoutParams = SettingViewUtil.resizeView(
                layoutParams,
                ratioState,
                this.deviceWidth,
                this.deviceHeight
            )

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

    companion object {
        const val JPEG_MIME_TYPE = "image/jpeg"
        const val MEDIA_DIRECTORY_PATH = "/ICEC IMAGE"
        const val EXTERNAL_STORAGE_DIRECTORY_PATH = "/Pictures/ICEC IMAGE"
        const val IMAGE_DATE_FORMAT = "yyyy-MM-dd-HHmmss"
        const val STRING_ICEC = "ICEC"
    }

}