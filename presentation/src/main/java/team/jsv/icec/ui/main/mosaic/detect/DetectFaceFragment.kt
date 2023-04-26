package team.jsv.icec.ui.main.mosaic.detect

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
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
    private val detectedFaceAdapter by lazy {
        DetectedFaceAdapter(object : DetectedFaceListener {
            override fun onItemClick(position: Int) {
                viewModel.setOnClickItem(position)
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.vm = viewModel
        initRecyclerView()
    }

    override fun initView() {
        lifecycleScope.launch {
            launch {
                viewModel.backPress.observe(this@DetectFaceFragment, EventObserver {
                    popBackStack()
                })
            }

            launch {
                viewModel.selectedItemIndex.collect { selectedIndexList ->
                    detectedFaceAdapter.updateSelection(selectedIndexList)
                    if (selectedIndexList.isEmpty()) {
                        binding.btGroupSelect.changeBackground(false)
                    } else {
                        binding.btGroupSelect.changeBackground(true)
                    }
                }
            }
        }
    }

    private fun initRecyclerView() {
        binding.rvDetectedFace.apply {
            adapter = detectedFaceAdapter
            itemAnimator = null
            addItemDecoration(HorizontalSpaceItemDecoration(space = 12))
        }

        lifecycleScope.launch {
            viewModel.detectFaces.collect { faceViewItems ->
                detectedFaceAdapter.submitList(faceViewItems.faceList)
            }
        }
    }

}
