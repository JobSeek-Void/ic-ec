package team.jsv.icec.ui.main

import android.os.Bundle
import android.view.View
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
import team.jsv.icec.util.PermissionUtil
import team.jsv.icec.util.gone
import team.jsv.icec.util.loadImage
import team.jsv.icec.util.requestPermissions
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

        requestPermissions(PermissionUtil.getPermissions())
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
                    binding.topBar.buttonBack.visible()
                    binding.topBar.textviewTitle.gone()
                    binding.topBar.buttonNext.gone()
                }
                ScreenStep.SelectFace -> {
                    binding.topBar.buttonBack.visible()
                    binding.topBar.buttonNext.visible()
                    binding.topBar.textviewTitle.visible()
                    binding.topBar.textviewTitle.text = getString(R.string.mosaic_text)
                }
                ScreenStep.MosaicFace -> {
                    binding.topBar.buttonBack.visible()
                    binding.topBar.buttonNext.visible()
                    binding.topBar.textviewTitle.visible()
                    binding.topBar.textviewTitle.text = getString(R.string.mosaic_text)
                }
                else -> {}
            }
        }
    }

    private fun initView() {

        // TODO(ham2174) : 갤러리 혹은 카메라로 찍은 사진 데이터를 가져와야함. TakePictureActivity -> MainActivity 이미지 데이터 전달 필요
        viewModel.setImage(File("/storage/emulated/0/Pictures/dicdic6.jpg"))

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
                        binding.imageviewImage.loadImage(file)
                    }
                }
                is PictureState.Url
                -> {
                    viewModel.mosaicImage.observe(this, EventObserver { url ->
                        binding.imageviewImage.loadImage(url)
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
        binding.topBar.buttonBack.setOnClickListener {
            handleScreenStepChange()
        }

        binding.topBar.buttonNext.setOnClickListener {
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