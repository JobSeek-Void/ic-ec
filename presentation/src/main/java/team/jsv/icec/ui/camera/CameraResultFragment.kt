package team.jsv.icec.ui.camera

import androidx.fragment.app.activityViewModels
import team.jsv.icec.base.BaseFragment
import team.jsv.presentation.R
import team.jsv.presentation.databinding.FragmentCameraResultBinding

class CameraResultFragment :
    BaseFragment<FragmentCameraResultBinding>(R.layout.fragment_camera_result) {

    private val viewModel: CameraViewModel by activityViewModels()

    override fun initView() {
        binding.photo.setImageBitmap(viewModel.bitmapImage.value)
    }
}