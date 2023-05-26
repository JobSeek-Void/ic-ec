package team.jsv.icec.ui.main

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import team.jsv.icec.base.BaseActivity
import team.jsv.icec.util.gone
import team.jsv.icec.util.visible
import team.jsv.presentation.R
import team.jsv.presentation.databinding.ActivityMosaicResultBinding

class MosaicResultActivity :
    BaseActivity<ActivityMosaicResultBinding>(R.layout.activity_mosaic_result) {

    private val viewModel: MosaicResultViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("image", Uri::class.java)
        } else {
            intent.getParcelableExtra("image") as? Uri
        })?.let { image ->
            viewModel.setImage(
                image
            )
            initView()
        }
    }

    override fun onResume() {
        super.onResume()
        initTopBar()
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

    private fun initView() {
        binding.ivMosaicResult.setImageURI(viewModel.image.value)
    }

    private fun initClickListeners() {
        binding.topBar.ivShare.setOnClickListener {
            val shareIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, viewModel.image.value)
                type = "image/jpg"
            }

            startActivity(Intent.createChooser(shareIntent, R.string.share_text.toString()))
        }

        binding.topBar.btClose.setOnClickListener {
            finish()
        }
    }
}