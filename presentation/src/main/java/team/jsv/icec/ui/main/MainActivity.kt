package team.jsv.icec.ui.main

import android.os.Bundle
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import team.jsv.icec.base.BaseActivity
import team.jsv.icec.base.EventObserver
import team.jsv.icec.base.showToast
import team.jsv.icec.ui.main.mosaic.MosaicEvent
import team.jsv.icec.ui.main.mosaic.MosaicViewModel
import team.jsv.icec.ui.main.mosaic.PictureState
import team.jsv.icec.ui.main.mosaic.ScreenStep
import team.jsv.icec.util.loadImage
import team.jsv.presentation.R
import team.jsv.presentation.databinding.ActivityMainBinding
import java.io.File

@AndroidEntryPoint
class MainActivity :
    BaseActivity<ActivityMainBinding>(R.layout.activity_main) {

    private val viewModel: MosaicViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

        binding.topBar.buttonBack.setOnClickListener {
            when (viewModel.screenStep.value) {
                ScreenStep.SelectMosaicEdit ->
                    finish()
                else -> {
                    viewModel.run {
                        backPress()
                        setImageAboutScreenStep()
                    }
                }
            }
        }

    }

}