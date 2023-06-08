package team.jsv.icec.ui.main.mosaic.result

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import team.jsv.icec.base.BaseActivity
import team.jsv.icec.util.loadImage
import team.jsv.icec.util.showSnackBarAction
import team.jsv.icec.util.visible
import team.jsv.presentation.R
import team.jsv.presentation.databinding.ActivityMosaicResultBinding
import java.io.File
import java.net.URI

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

    private fun shareImage(mosaicImagePath: String) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = SHARE_TYPE

            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P &&
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
            ) {
                val contentUri = FileProvider.getUriForFile(
                    this@MosaicResultActivity,
                    "${packageName}.provider",
                    File(URI(mosaicImagePath).path)
                )
                putExtra(Intent.EXTRA_STREAM, contentUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } else {
                putExtra(Intent.EXTRA_STREAM, mosaicImagePath.toUri())
            }
        }

        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_text)))
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
