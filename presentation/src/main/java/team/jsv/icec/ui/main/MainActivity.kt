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
    private val imagePath: String? by lazy { intent.getStringExtra("imagePath") }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initTopBar()
        initView()
        setOnBackPressedCallback()
    }

    override fun onResume() {
        super.onResume()

        initClickListeners()
    }

    private fun initTopBar() {
        viewModel.screenStep.observe(this) {
            when (it) {
                ScreenStep.SelectMosaicEdit -> {
                    binding.topBar.btClose.visible()
                    binding.topBar.btBack.gone()
                    binding.topBar.tvTitle.gone()
                    binding.topBar.btNext.gone()
                    binding.topBar.btDownload.gone()
                }
                ScreenStep.SelectFace -> {
                    binding.topBar.btClose.gone()
                    binding.topBar.btDownload.gone()
                    binding.topBar.btBack.visible()
                    binding.topBar.btNext.visible()
                    binding.topBar.tvTitle.visible()
                    binding.topBar.tvTitle.text = getString(R.string.mosaic_text)
                }
                ScreenStep.MosaicFace -> {
                    binding.topBar.btClose.gone()
                    binding.topBar.btDownload.visible()
                    binding.topBar.btNext.gone()
                    binding.topBar.tvTitle.visible()
                    binding.topBar.tvTitle.text = getString(R.string.mosaic_text)
                }
                else -> {}
            }
        }
    }

    private fun initView() {
        imagePath?.let { path -> viewModel.setImage(File(path)) }

        viewModel.mosaicEvent.observe(this, EventObserver {
            when (it) {
                is MosaicEvent.SendToast -> {
                    binding.root.context.showToast(it.message)
                }
            }
        })

        viewModel.state.observe(this) {
            when (it) {
                is PictureState.File
                -> {
                    viewModel.originalImage.observe(this) { file ->
                        binding.ivImage.loadImage(file)
                    }
                }
                is PictureState.Url
                -> {
                    viewModel.mosaicImage.observe(this, EventObserver { url ->
                        binding.ivImage.loadImage(url)
                    })
                }
            }
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
            ScreenStep.SelectMosaicEdit -> {
                finish()
            }
            ScreenStep.SelectFace -> {
                viewModel.run {
                    backPress()
                    setScreen(ScreenStep.SelectMosaicEdit)
                }
            }
            ScreenStep.MosaicFace -> {
                viewModel.run {
                    backPress()
                    setImageAboutScreenStep()
                    setScreen(ScreenStep.SelectFace)
                }
            }
            else -> {}
        }
    }

    private fun initClickListeners() {
        binding.topBar.btBack.setOnClickListener {
            handleScreenStepChange()
        }

        binding.topBar.btNext.setOnClickListener {
            when (viewModel.screenStep.value) {
                ScreenStep.SelectFace -> {
                    navController.navigate(R.id.action_faceSelectFragment_to_faceMosaicFragment)
                    viewModel.run {
                        setScreen(ScreenStep.MosaicFace)
                        setImageAboutScreenStep()
                        getMosaicImage()
                    }
                }
                else -> {}
            }
        }
    }
}