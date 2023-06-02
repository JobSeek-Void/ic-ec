package team.jsv.icec.ui.main

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import team.jsv.icec.base.BaseActivity
import team.jsv.icec.base.startActivityWithAnimation
import team.jsv.icec.ui.main.mosaic.result.MosaicResultActivity
import team.jsv.icec.util.Extras.ImagePath
import team.jsv.icec.util.gone
import team.jsv.icec.util.loadImage
import team.jsv.icec.util.saveImage
import team.jsv.icec.util.showToast
import team.jsv.icec.util.toBitmap
import team.jsv.icec.util.visible
import team.jsv.presentation.R
import team.jsv.presentation.databinding.ActivityMainBinding

@AndroidEntryPoint
class MainActivity :
    BaseActivity<ActivityMainBinding>(R.layout.activity_main) {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var onBackPressedCallback: OnBackPressedCallback
    private val navController by lazy {
        (supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment).findNavController()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        handleState()
        handleEvent()
        initView()
    }

    override fun onResume() {
        super.onResume()

        initClickListeners()
    }

    private fun initView() {
        setOnBackPressedCallback()
    }

    private fun initClickListeners() {
        binding.topBar.btBack.setOnClickListener {
            viewModel.handleBackPress()
        }

        binding.topBar.btNext.setOnClickListener {
            viewModel.nextScreenStep()
        }

        binding.topBar.btDownload.setOnClickListener {
            viewModel.nextScreenStep()
        }

        binding.topBar.btClose.setOnClickListener {
            finish()
        }
    }

    private fun handleState() {
        lifecycleScope.launch {
            viewModel.mainState.flowWithLifecycle(lifecycle).collectLatest { state ->
                with(state.pictureState) {
                    when (viewType) {
                        PictureState.ViewType.Original -> {
                            binding.ivImage.loadImage(originalImage)
                        }

                        PictureState.ViewType.Mosaic -> {
                            binding.ivImage.loadImage(mosaicImage)
                        }
                    }
                }
                when (state.screenStep) {
                    ScreenStep.SelectFace -> {
                        binding.topBar.btClose.visible()
                        binding.topBar.btDownload.gone()
                        binding.topBar.btBack.gone()
                        binding.topBar.btNext.visible()
                        binding.topBar.tvTitle.gone()
                        binding.topBar.tvTitle.text = getString(R.string.mosaic_text)
                    }

                    ScreenStep.MosaicFace -> {
                        binding.topBar.btClose.gone()
                        binding.topBar.btDownload.visible()
                        binding.topBar.btBack.visible()
                        binding.topBar.btNext.gone()
                        binding.topBar.tvTitle.gone()
                        binding.topBar.tvTitle.text = getString(R.string.mosaic_text)
                    }
                }
            }
        }
    }

    private fun handleEvent() {
        lifecycleScope.launch{
            viewModel.mainEvent.collect{ mainEvent ->
                when (mainEvent) {
                    MainEvent.Finish -> {
                        finish()
                    }

                    MainEvent.NavigateToMosaicFace -> {
                        navController.navigate(R.id.action_faceSelectFragment_to_faceMosaicFragment)
                    }

                    MainEvent.NavigateToMosaicResult -> {
                        saveImage(bitmap = binding.ivImage.toBitmap())
                        // TODO(ham2174) : 이미지 경로 넘겨주기. (이미지 경로 saveImage 함수에서 )
                        startActivityWithAnimation<MosaicResultActivity>()
                        finish()
                    }

                    is MainEvent.SendToast -> {
                        binding.root.context.showToast(mainEvent.message)
                    }
                }
            }
        }
    }

    private fun setOnBackPressedCallback() {
        onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                viewModel.handleBackPress()
            }
        }
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

}