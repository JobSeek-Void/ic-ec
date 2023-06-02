package team.jsv.icec.ui.main.mosaic.result

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION_CODES
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.content.FileProvider
import team.jsv.icec.base.BaseActivity
import team.jsv.icec.util.Extras.ImagePath
import team.jsv.icec.util.gone
import team.jsv.icec.util.loadImage
import team.jsv.icec.util.showSnackBarAction
import team.jsv.icec.util.visible
import team.jsv.presentation.R
import team.jsv.presentation.databinding.ActivityMosaicResultBinding
import java.io.File

class MosaicResultActivity :
    BaseActivity<ActivityMosaicResultBinding>(R.layout.activity_mosaic_result) {

    private val viewModel: MosaicResultViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        intent.getStringExtra(ImagePath)?.let { image ->
            viewModel.setImage(image)
        }

        initTopBar()
        observeImage()
    }

    override fun onResume() {
        super.onResume()

        initClickListeners()
    }

    private fun initTopBar() {
        binding.topBar.btClose.visible()
        binding.topBar.ivShare.visible()
        binding.topBar.tvSaveDone.visible()
        binding.topBar.btBack.gone()
        binding.topBar.tvTitle.gone()
        binding.topBar.btNext.gone()
        binding.topBar.btDownload.gone()
    }

    private fun observeImage() {
        viewModel.image.observe(this) { image ->
            binding.ivMosaicResult.loadImage(image)
            binding.root.showSnackBarAction(R.string.snackbar_text, R.color.white, R.color.SubColor)
        }
    }

    private fun initClickListeners() {
        binding.topBar.ivShare.setOnClickListener {
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, convertUri(viewModel.image.value ?: ""))
                type = SHARE_TYPE
            }

            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_text)))
        }

        binding.topBar.btClose.setOnClickListener {
            finish()
        }
    }

    private fun convertUri(imagePath : String) : Uri {
        return if (Build.VERSION.SDK_INT >= VERSION_CODES.N) {
            FileProvider.getUriForFile(this, "com.example.fileprovider", File(imagePath))
        } else {
            Uri.fromFile(File(imagePath))
        }
    }

    companion object {
        const val SHARE_TYPE = "image/jpg"
    }
}