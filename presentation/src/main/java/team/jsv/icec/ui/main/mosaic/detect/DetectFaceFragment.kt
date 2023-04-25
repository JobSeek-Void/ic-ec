package team.jsv.icec.ui.main.mosaic.detect

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import team.jsv.icec.base.BaseFragment
import team.jsv.icec.base.EventObserver
import team.jsv.icec.ui.main.mosaic.MosaicViewModel
import team.jsv.icec.ui.main.mosaic.detect.adapter.DetectedFaceAdapter
import team.jsv.icec.ui.main.mosaic.detect.adapter.DetectedFaceListener
import team.jsv.icec.util.HorizontalSpaceItemDecoration
import team.jsv.presentation.R
import team.jsv.presentation.databinding.FragmentDetectFaceBinding

@AndroidEntryPoint
class DetectFaceFragment :
    BaseFragment<FragmentDetectFaceBinding>(R.layout.fragment_detect_face) {

    private val viewModel: MosaicViewModel by activityViewModels()
    private val detectFaceViewModel: DetectFaceViewModel by viewModels()
    private val detectedFaceAdapter by lazy {
        DetectedFaceAdapter(object : DetectedFaceListener {
            override fun onItemClick(position: Int) {
                viewModel.updateSelectedItemList(position)
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.detectFaceViewModel = detectFaceViewModel
        initRecyclerView()
    }

    override fun initView() {
        viewModel.backPress.observe(this, EventObserver {
            popBackStack()
        })

        lifecycleScope.launch {
            viewModel.detectFaces.flowWithLifecycle(lifecycle).collect {
                val detectedFaceCount = it.faceList.size
                binding.tvDetectedFaceCount.text =
                    getString(R.string.detected_face_count, detectedFaceCount)
            }
        }
    }

    private fun initRecyclerView() {
        binding.rvDetectedFace.apply {
            adapter = detectedFaceAdapter
            addItemDecoration(HorizontalSpaceItemDecoration(space = 12))
        }

        lifecycleScope.launch {
            viewModel.detectFaces.collect { faceViewItems ->
                detectedFaceAdapter.submitList(faceViewItems.faceList)
            }
        }
    }

}
