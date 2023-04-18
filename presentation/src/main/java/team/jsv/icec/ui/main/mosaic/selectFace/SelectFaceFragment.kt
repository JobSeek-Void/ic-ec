package team.jsv.icec.ui.main.mosaic.selectFace

import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import team.jsv.icec.base.BaseFragment
import team.jsv.icec.base.EventObserver
import team.jsv.icec.ui.main.mosaic.MosaicViewModel
import team.jsv.icec.util.HorizontalSpaceItemDecoration
import team.jsv.presentation.R
import team.jsv.presentation.databinding.FragmentSelectFaceBinding

@AndroidEntryPoint
class SelectFaceFragment :
    BaseFragment<FragmentSelectFaceBinding>(R.layout.fragment_select_face) {

    private val viewModel: MosaicViewModel by activityViewModels()
    private val detectedFaceAdapter by lazy { DetectedFaceAdapter() }

    override fun initView() {
        binding.rvDetectedFace.apply {
            adapter = detectedFaceAdapter
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