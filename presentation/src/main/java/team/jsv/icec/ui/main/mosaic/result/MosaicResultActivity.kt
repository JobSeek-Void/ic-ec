package team.jsv.icec.ui.main.mosaic.result

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import team.jsv.icec.base.BaseActivity
import team.jsv.icec.util.loadImage
import team.jsv.icec.util.setICECThemeBottomNavigationColor
import team.jsv.icec.util.shareImage
import team.jsv.icec.util.showSnackBarAction
import team.jsv.icec.util.visible
import team.jsv.presentation.R
import team.jsv.presentation.databinding.ActivityMosaicResultBinding

class MosaicResultActivity :
    BaseActivity<ActivityMosaicResultBinding>(R.layout.activity_mosaic_result) {

    private val viewModel: MosaicResultViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setICECThemeBottomNavigationColor()

        initTopBar()
        collectResultImage()
        collectEventState()
    }

    override fun onResume() {
        super.onResume()

        initClickListeners()
    }

    private fun initTopBar() {
        binding.topBar.btClose.visible()
        binding.topBar.ivShare.visible()
        binding.topBar.tvSaveDone.visible()
    }

    private fun collectResultImage() {
        lifecycleScope.launch {
            viewModel.image.collect { uri ->
                binding.ivMosaicResult.loadImage(uri)
                binding.root.showSnackBarAction(R.string.snackbar_text, R.color.white, R.color.SubColor)
            }
        }
    }

    private fun collectEventState() {
        lifecycleScope.launch {
            viewModel.mosaicResultEvent.collect { event ->
                when(event) {
                    MosaicResultEvent.OnClickFinish -> { finish() }

                    is MosaicResultEvent.OnClickShare -> { shareImage(event.mosaicImage) }
                }
            }
        }
    }

    private fun initClickListeners() {
        binding.topBar.ivShare.setOnClickListener {
            viewModel.setOnClickShare()
        }

        binding.topBar.btClose.setOnClickListener {
            viewModel.setOnClickClose()
        }
    }

    companion object {
        const val SHARE_TYPE = "image/jpg"
    }

}
