package team.jsv.icec.ui.main.mosaic.detect

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.google.android.material.slider.Slider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import team.jsv.icec.base.BaseFragment
import team.jsv.icec.base.EventObserver
import team.jsv.icec.ui.main.MainViewModel
import team.jsv.icec.ui.main.mosaic.detect.adapter.DetectedFaceAdapter
import team.jsv.icec.util.HorizontalSpaceItemDecoration
import team.jsv.presentation.R
import team.jsv.presentation.databinding.FragmentDetectFaceBinding

@AndroidEntryPoint
class DetectFaceFragment : BaseFragment<FragmentDetectFaceBinding>(R.layout.fragment_detect_face) {

    private val viewModel: MainViewModel by activityViewModels()
    private val detectedFaceAdapter by lazy {
        DetectedFaceAdapter { position -> viewModel.setOnClickItem(position) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bind()
    }

    override fun initView() {
        initDetectSlider()
        initRecyclerView()
    }

    override fun onResume() {
        super.onResume()

        initClickListener()
        initTouchListener()
    }

    private fun bind() {
        binding.vm = viewModel

        observeBackPress()
        collectDetectFaceState()
        collectSelectedItemUpdates()
    }

    private fun initDetectSlider() {
        binding.sdDetectFace.apply {
            value = sliderValue
            valueFrom = sliderFrom
            valueTo = sliderValueTo
            stepSize = sliderStepSize
            haloRadius = sliderHaloRadius
        }
    }

    private fun initRecyclerView() {
        binding.rvDetectedFace.apply {
            adapter = detectedFaceAdapter
            itemAnimator = null
            addItemDecoration(HorizontalSpaceItemDecoration(space = horizontalSpace))
        }
    }

    private fun observeBackPress() {
        viewModel.backPress.observe(viewLifecycleOwner, EventObserver {
            popBackStack()
        })
    }

    private fun collectSelectedItemUpdates() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.detectedFaceIndexes.flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .collectLatest { selectedIndexList ->
                    detectedFaceAdapter.updateSelection(selectedIndexList)
                    binding.btGroupSelect.changeBackground(selectedIndexList.isNotEmpty())
                }
        }
    }

    private fun collectDetectFaceState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.detectFaceState.flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .collectLatest { state ->
                    when (state.isLoading) {
                        true -> dialog.show()
                        false -> {
                            dialog.dismiss()
                            detectedFaceAdapter.submitList(state.faceViewItem.faceList)
                        }
                    }
                }
        }
    }

    private fun initClickListener() {
        binding.ivRefresh.setOnClickListener {
            initDetectSlider()
            viewModel.setDetectStrength(sliderValue)
        }
    }

    private fun initTouchListener() {
        binding.sdDetectFace.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: Slider) {}
            override fun onStopTrackingTouch(slider: Slider) {
                viewModel.setDetectStrength(slider.value)
            }
        })
    }

    companion object {
        private const val sliderValue = 90f
        private const val sliderFrom = 1f
        private const val sliderValueTo = 99f
        private const val sliderStepSize = 1f
        private const val sliderHaloRadius = 0
        private const val horizontalSpace = 12
    }

}
