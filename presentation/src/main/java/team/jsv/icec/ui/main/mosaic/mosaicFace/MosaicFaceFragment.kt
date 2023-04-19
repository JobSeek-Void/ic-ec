package team.jsv.icec.ui.main.mosaic.mosaicFace

import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.slider.Slider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import team.jsv.icec.base.BaseFragment
import team.jsv.icec.base.EventObserver
import team.jsv.icec.ui.main.mosaic.MosaicViewModel
import team.jsv.presentation.R
import team.jsv.presentation.databinding.FragmentMosaicFaceBinding

@AndroidEntryPoint
class MosaicFaceFragment :
    BaseFragment<FragmentMosaicFaceBinding>(R.layout.fragment_mosaic_face) {

    private val viewModel: MosaicViewModel by activityViewModels()

    override fun initView() {
        observeState()
        binding.ivMosaicFigure.setOnClickListener {
            viewModel.setMosaicType(MosaicType.Mosaic)
        }
        binding.ivBlurFigure.setOnClickListener {
            viewModel.setMosaicType(MosaicType.Blur)
        }
        binding.sliderMosaicFigure.apply {
            haloRadius = 0

            addOnChangeListener(Slider.OnChangeListener { _, value, _ ->
                viewModel.setPixelSize(value)
            })

            addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
                override fun onStartTrackingTouch(slider: Slider) {}
                override fun onStopTrackingTouch(slider: Slider) {
                    viewModel.emitMosaicEvent()
                }
            })
        }
    }

    private fun observeState() = lifecycleScope.launch {
        viewModel.backPress.observe(this@MosaicFaceFragment, EventObserver {
            popBackStack()
        })

        viewModel.pixelSize.observe(this@MosaicFaceFragment) {
            binding.sliderMosaicFigure.value = it
        }

        viewModel.mosaicFaceState.flowWithLifecycle(lifecycle).collect { state ->
            when (state.mosaicType) {
                MosaicType.Mosaic -> {
                    binding.ivMosaicFigure.setImageResource(R.drawable.ic_mosaic_active_50)
                    binding.ivBlurFigure.setImageResource(R.drawable.ic_blur_inactive_50)
                }
                MosaicType.Blur -> {
                    binding.ivMosaicFigure.setImageResource(R.drawable.ic_mosaic_inactive_50)
                    binding.ivBlurFigure.setImageResource(R.drawable.ic_blur_active_50)
                }
                MosaicType.None -> {
                    binding.ivBlurFigure.setImageResource(R.drawable.ic_blur_inactive_50)
                    binding.ivMosaicFigure.setImageResource(R.drawable.ic_mosaic_inactive_50)
                }
            }
        }
    }
}
