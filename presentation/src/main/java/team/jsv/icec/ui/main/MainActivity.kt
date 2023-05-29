package team.jsv.icec.ui.main

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import team.jsv.icec.base.BaseActivity
import team.jsv.icec.base.EventObserver
import team.jsv.icec.base.showToast
import team.jsv.icec.ui.main.mosaic.MosaicEvent
import team.jsv.icec.ui.main.mosaic.MosaicViewModel
import team.jsv.icec.ui.main.mosaic.PictureState
import team.jsv.icec.ui.main.mosaic.ScreenStep
import team.jsv.icec.util.gone
import team.jsv.icec.util.loadImage
import team.jsv.icec.util.visible
import team.jsv.presentation.R
import team.jsv.presentation.databinding.ActivityMainBinding
import java.io.File

@AndroidEntryPoint
class MainActivity :
    BaseActivity<ActivityMainBinding>(R.layout.activity_main) {

    private val viewModel: MosaicViewModel by viewModels()
    private lateinit var onBackPressedCallback: OnBackPressedCallback
    private val navController by lazy {
        (supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment).findNavController()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bind()
        initView()
    }

    override fun onResume() {
        super.onResume()

        initClickListeners()
    }

    private fun bind() {
        viewModel.screenStep.observe(this) {
            when (it) {
                ScreenStep.SelectFace -> {
                    binding.topBar.btClose.visible()
                    binding.topBar.btDownload.gone()
                    binding.topBar.btBack.gone()
                    binding.topBar.btNext.visible()
                    binding.topBar.tvTitle.gone()
                    binding.topBar.tvTitle.text = getString(R.string.mosaic_text)
                    viewModel.setOriginalImage()
                }
                ScreenStep.MosaicFace -> {
                    binding.topBar.btClose.gone()
                    binding.topBar.btDownload.visible()
                    binding.topBar.btBack.visible()
                    binding.topBar.btNext.gone()
                    binding.topBar.tvTitle.gone()
                    binding.topBar.tvTitle.text = getString(R.string.mosaic_text)
                    viewModel.setMosaicImage()
                }
                else -> {}
            }
        }

        viewModel.mosaicEvent.observe(this, EventObserver {
            when (it) {
                is MosaicEvent.SendToast -> {
                    binding.root.context.showToast(it.message)
                }
            }
        })

        viewModel.state.observe(this) { pictureState ->
            when (pictureState) {
                is PictureState.File ->
                    binding.ivImage.loadImage(viewModel.originalImage)
                is PictureState.Url ->
                    binding.ivImage.loadImage(viewModel.mosaicImage)
            }
        }
    }

    private fun initView() {
        setOnBackPressedCallback()
    }

    private fun initClickListeners() {
        binding.topBar.btBack.setOnClickListener {
            handleScreenStepChange()
        }

        binding.topBar.btNext.setOnClickListener {
            viewModel.nextScreenStep()
            setChangeFragment()
        }

        binding.topBar.btClose.setOnClickListener {
            finish()
        }
    }

    private fun setOnBackPressedCallback() {
        onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                handleScreenStepChange()
            }
        }
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    private fun handleScreenStepChange() {
        when (viewModel.screenStep.value) {
            ScreenStep.SelectFace -> {
                finish()
            }
            ScreenStep.MosaicFace -> {
                viewModel.backPress()
                viewModel.setScreen(ScreenStep.SelectFace)
            }
            else -> {}
        }
    }

    private fun setChangeFragment() {
        when (viewModel.screenStep.value) {
            ScreenStep.MosaicFace -> {
                navController.navigate(R.id.action_faceSelectFragment_to_faceMosaicFragment)
            }
            else -> {}
        }
    }

}