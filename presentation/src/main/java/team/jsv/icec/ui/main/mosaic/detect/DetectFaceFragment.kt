package team.jsv.icec.ui.main.mosaic.detect

import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import team.jsv.icec.base.BaseFragment
import team.jsv.icec.base.EventObserver
import team.jsv.icec.ui.main.mosaic.MosaicViewModel
import team.jsv.icec.util.HorizontalSpaceItemDecoration
import team.jsv.presentation.R
import team.jsv.presentation.databinding.FragmentDetectFaceBinding

@AndroidEntryPoint
class DetectFaceFragment :
    BaseFragment<FragmentDetectFaceBinding>(R.layout.fragment_detect_face) {

    private val viewModel: MosaicViewModel by activityViewModels()
    private val detectedFaceAdapter by lazy { DetectedFaceAdapter() }
    private var isSelect: Boolean = false

    override fun initView() {
        binding.rvDetectedFace.apply {
            adapter = detectedFaceAdapter
            addItemDecoration(HorizontalSpaceItemDecoration(space = 12))
        }

        viewModel.backPress.observe(this, EventObserver {
            popBackStack()
        })

        viewModel.detectFaces.observe(this) {
            detectedFaceAdapter.submitList(it.faceList)

            val detectedFaceCount = it.faceList.size
            binding.tvDetectedFaceCount.text =
                getString(R.string.detected_face_count, detectedFaceCount)
        }

    }

}