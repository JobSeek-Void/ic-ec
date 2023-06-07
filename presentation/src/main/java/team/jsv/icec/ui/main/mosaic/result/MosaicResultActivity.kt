package team.jsv.icec.ui.main.mosaic.result

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import team.jsv.icec.base.BaseActivity
import team.jsv.icec.util.gone
import team.jsv.icec.util.loadImage
import team.jsv.icec.util.showSnackBarAction
import team.jsv.icec.util.visible
import team.jsv.presentation.R
import team.jsv.presentation.databinding.ActivityMosaicResultBinding

class MosaicResultActivity :
    BaseActivity<ActivityMosaicResultBinding>(R.layout.activity_mosaic_result) {

    private val viewModel: MosaicResultViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initTopBar()
        collectResultImage()
        collectEventState()
    }

    override fun onResume() {
        super.onResume()

        binding.topBar.ivShare.isEnabled = true

        initClickListeners()
    }

    override fun onPause() {
        super.onPause()

        binding.topBar.ivShare.isEnabled = false
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
                    is MosaicResultEvent.FinishActivity -> { finish() }

                    is MosaicResultEvent.SendMosaicImage -> {
                        val shareIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_STREAM,event.mosaicImage.toUri())
                            type = SHARE_TYPE
                        }

                        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_text)))
                    }
                }
            }
        }
    }

    private fun initClickListeners() {
        binding.topBar.ivShare.setOnClickListener {
            viewModel.handleMosaicResultEvent(MosaicResultEvent.Event.Share)
        }

        binding.topBar.btClose.setOnClickListener {
            viewModel.handleMosaicResultEvent(MosaicResultEvent.Event.ActivityFinish)
        }
    }

    companion object {
        const val SHARE_TYPE = "image/jpg"
    }

}
