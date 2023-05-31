package team.jsv.icec.ui.main

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
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
import java.io.ByteArrayOutputStream
import java.io.File
import java.lang.Byte.decode


class MosaicResultActivity :
    BaseActivity<ActivityMosaicResultBinding>(R.layout.activity_mosaic_result) {

    private val viewModel: MosaicResultViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(INTENT_KEY, Uri::class.java)
        } else {
            intent.getParcelableExtra(INTENT_KEY) as? Uri
        })?.let { image ->
            viewModel.setImage(
                image
            )
            observeImage()
        }

        showSnackBarAction()
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

    private fun observeImage() {
        viewModel.image.observe(this) { image ->
            binding.ivMosaicResult.setImageURI(image)
        }
    }

    private fun initClickListeners() {
        binding.topBar.ivShare.setOnClickListener {
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, viewModel.image.value)
                type = SHARE_TYPE
            }

            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_text)))
        }

        binding.topBar.btClose.setOnClickListener {
            finish()
        }
    }

    private fun showSnackBarAction() {
        val snackBar = Snackbar.make(binding.root, R.string.snackbar_text, Snackbar.LENGTH_LONG)
        setSnackBarOption(snackBar)
        snackBar.show()
    }

    private fun setSnackBarOption(snackBar: Snackbar) {
        snackBar.setTextColor(Color.WHITE)
        snackBar.setBackgroundTint(ContextCompat.getColor(this, R.color.SubColor))

        val snackbarView = snackBar.view
        val textView =
            snackbarView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)

        textView.textAlignment = View.TEXT_ALIGNMENT_CENTER

        val backgroundDrawable = snackbarView.background as GradientDrawable

        backgroundDrawable.cornerRadius =
            resources.getDimensionPixelOffset(R.dimen.snackbar_corner_radius).toFloat()

        val params = snackbarView.layoutParams as ViewGroup.MarginLayoutParams

        params.setMargins(
            params.leftMargin + resources.getDimensionPixelOffset(R.dimen.snackbar_margin_start),
            params.topMargin,
            params.rightMargin + resources.getDimensionPixelOffset(R.dimen.snackbar_margin_end),
            params.bottomMargin + resources.getDimensionPixelOffset(R.dimen.snackbar_margin_bottom)
        )

        snackbarView.layoutParams = params
    companion object {
        const val SHARE_TYPE = "image/jpg"
    }
}