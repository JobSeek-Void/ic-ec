package team.jsv.icec.ui.main.mosaic.mosaicFace

import androidx.fragment.app.activityViewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.google.android.material.slider.Slider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import team.jsv.domain.model.MosaicType
import team.jsv.icec.base.BaseFragment
import team.jsv.icec.base.EventObserver
import team.jsv.icec.ui.main.mosaic.MosaicViewModel
import team.jsv.presentation.R
import team.jsv.presentation.databinding.FragmentMosaicFaceBinding

@AndroidEntryPoint
class MosaicFaceFragment
    : BaseFragment<FragmentMosaicFaceBinding>(R.layout.fragment_mosaic_face) {

    private val viewModel: MosaicViewModel by activityViewModels()

    override fun initView() {
        initClickListener()
        observeState()
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
        binding.sliderMosaicFigure.apply {
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

    private fun observeState() {
        lifecycleScope.launch {
            viewModel.backPress.observe(this@MosaicFaceFragment, EventObserver {
                popBackStack()
            })

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
                binding.sliderMosaicFigure.value = state.pixelSize
            }
        }
    }
}
