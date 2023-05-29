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
import team.jsv.icec.ui.main.MainViewModel
import team.jsv.icec.ui.main.mosaic.detect.strokeFor
import team.jsv.presentation.R
import team.jsv.presentation.databinding.FragmentMosaicFaceBinding

@AndroidEntryPoint
class MosaicFaceFragment
    : BaseFragment<FragmentMosaicFaceBinding>(R.layout.fragment_mosaic_face) {

    private val viewModel: MainViewModel by activityViewModels()

    override fun initView() {
        initClickListener()
        observeState()
    }

    private fun initClickListener() {
        binding.ivMosaicFigure.apply {
            clipToOutline = true
            setOnClickListener {
                viewModel.setMosaicType(MosaicType.Mosaic)
            }
        }
        binding.ivBlurFigure.apply {
            clipToOutline = true
            setOnClickListener {
                viewModel.setMosaicType(MosaicType.Blur)
            }
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
                    viewModel.getMosaicImage()
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
                binding.sliderMosaicFigure.value = state.pixelSize
                when (state.isLoading) {
                    true -> dialog.show()
                    false -> dialog.dismiss()
                }
            }
        }
    }
}
