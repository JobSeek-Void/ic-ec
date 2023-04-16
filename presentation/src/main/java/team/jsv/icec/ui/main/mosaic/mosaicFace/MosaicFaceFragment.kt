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

    private var debounceJobGetMosaicImage: Job? = null
    private val debounceDelay: Long = 300L

    override fun initView() {
        viewModel.backPress.observe(this, EventObserver {
            popBackStack()
        })

        viewModel.pixelSize.observe(this) {
            binding.sliderMosaicFigure.value = it
        }

        binding.sliderMosaicFigure.apply {
            haloRadius = 0
        }.run {
            addOnChangeListener(Slider.OnChangeListener { _, value, _ ->
                viewModel.setPixelSize(value)
            })

            addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
                override fun onStartTrackingTouch(slider: Slider) {}
                override fun onStopTrackingTouch(slider: Slider) {
                    debounceJobGetMosaicImage?.cancel()
                    debounceJobGetMosaicImage =
                        lifecycleScope.launch(Dispatchers.Main) {
                            delay(debounceDelay)
                            viewModel.getMosaicImage()
                        }
                }
            })
        }
    }

}