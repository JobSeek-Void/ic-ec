package team.jsv.icec.ui.main

import android.content.res.Configuration
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import team.jsv.icec.base.BaseFragment
import team.jsv.icec.ui.main.mosaic.MosaicViewModel
import team.jsv.icec.ui.main.mosaic.ScreenStep
import team.jsv.presentation.R
import team.jsv.presentation.databinding.FragmentSelectMosaicEditBinding

@AndroidEntryPoint
class SelectMosaicEditFragment :
    BaseFragment<FragmentSelectMosaicEditBinding>(R.layout.fragment_select_mosaic_edit) {

    private val viewModel: MosaicViewModel by activityViewModels()

    override fun initView() {
        setButtonStroke()
    }

    override fun onResume() {
        super.onResume()

        binding.buttonMosaic.setOnClickListener {
            navController.navigate(R.id.action_selectMosaicEditFragment_to_faceSelectFragment)
            viewModel.setScreen(ScreenStep.SelectFace)
            viewModel.originalImage.value?.let { originalImage -> viewModel.getFaceList(originalImage) }
        }

        binding.buttonEdit.setOnClickListener {
            navController.navigate(R.id.action_selectMosaicEditFragment_to_photoEditFragment)
        }
    }

    private fun setButtonStroke() {
        if (currentTheme == Configuration.UI_MODE_NIGHT_NO) {
            binding.buttonEdit.strokeWidth = 0
            binding.buttonMosaic.strokeWidth = 0
        }
    }

}