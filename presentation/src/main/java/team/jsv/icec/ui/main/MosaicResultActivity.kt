package team.jsv.icec.ui.main

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import team.jsv.icec.base.BaseActivity
import team.jsv.icec.util.gone
import team.jsv.icec.util.visible
import team.jsv.presentation.R
import team.jsv.presentation.databinding.ActivityMosaicResultBinding

class MosaicResultActivity :
    BaseActivity<ActivityMosaicResultBinding>(R.layout.activity_mosaic_result) {

    companion object {
        const val INTENT_KEY = "image"
        const val SHARE_TYPE = "image/jpg"
    }

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
            val shareIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, viewModel.image.value)
                type = SHARE_TYPE
            }

            startActivity(Intent.createChooser(shareIntent, R.string.share_text.toString()))
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
    }
}