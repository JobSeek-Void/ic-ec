package team.jsv.icec.ui.main.mosaic.mosaicFace

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.google.android.material.slider.Slider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import team.jsv.domain.model.MosaicType
import team.jsv.icec.base.BaseFragment
import team.jsv.icec.base.EventObserver
import team.jsv.icec.ui.main.MainViewModel
import team.jsv.icec.ui.main.mosaic.detect.strokeFor
import team.jsv.presentation.R
import team.jsv.presentation.databinding.FragmentMosaicFaceBinding

@AndroidEntryPoint
class MosaicFaceFragment : BaseFragment<FragmentMosaicFaceBinding>(R.layout.fragment_mosaic_face) {

    private val viewModel: MainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bind()
    }

    override fun initView() {
        binding.ivMosaicFigure.clipToOutline = true
        binding.ivBlurFigure.clipToOutline = true

        initSliderMosaicFigure()
    }

    override fun onResume() {
        super.onResume()

        initClickListener()
        initTouchListener()
    }

    private fun bind() {
        observeState()
        collectMosaicFaceState()
    }

    private fun observeState() {
        viewModel.backPress.observe(viewLifecycleOwner, EventObserver {
            popBackStack()
        })
    }

    private fun collectMosaicFaceState() {
        lifecycleScope.launch {
            viewModel.mosaicFaceState.flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .collectLatest { state ->
                    binding.sliderMosaicFigure.value = state.pixelSize

                    when (state.mosaicType) {
                        MosaicType.Mosaic -> {
                            binding.ivMosaicFigure.strokeFor(true)
                            binding.ivBlurFigure.strokeFor(false)
                        }

                        MosaicType.Blur -> {
                            binding.ivMosaicFigure.strokeFor(false)
                            binding.ivBlurFigure.strokeFor(true)
                        }

                        MosaicType.None -> {
                            binding.ivMosaicFigure.strokeFor(false)
                            binding.ivBlurFigure.strokeFor(false)
                        }
                    }

                    when (state.isLoading) {
                        true -> dialog.show()
                        false -> dialog.dismiss()
                    }
                }
        }
    }

    private fun initSliderMosaicFigure() {
        binding.sliderMosaicFigure.apply {
            value = SLIDER_VALUE
            valueFrom = SLIDER_FROM
            valueTo = SLIDER_VALUE_TO
            stepSize = SLIDER_STEP_SIZE
            haloRadius = SLIDER_HALO_RADIUS
        }
    }

    private fun initClickListener() {
        binding.ivMosaicFigure.setOnClickListener {
            viewModel.setMosaicType(MosaicType.Mosaic)
        }

        binding.ivBlurFigure.setOnClickListener {
            viewModel.setMosaicType(MosaicType.Blur)
        }

        binding.ivRefresh.setOnClickListener {
            viewModel.mosaicFaceRefresh()
        }
    }

    private fun initTouchListener() {
        binding.sliderMosaicFigure.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: Slider) {}
            override fun onStopTrackingTouch(slider: Slider) {
                viewModel.setPixelSize(slider.value)
                viewModel.getMosaicImage()
            }
        })
    }

    companion object {
        private const val SLIDER_VALUE = 20f
        private const val SLIDER_FROM = 10f
        private const val SLIDER_VALUE_TO = 90f
        private const val SLIDER_STEP_SIZE = 10f
        private const val SLIDER_HALO_RADIUS = 0
    }

}
