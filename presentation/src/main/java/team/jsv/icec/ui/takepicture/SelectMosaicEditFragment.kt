package team.jsv.icec.ui.main

import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import team.jsv.icec.base.BaseFragment
import team.jsv.icec.ui.main.mosaic.MosaicViewModel
import team.jsv.presentation.R
import team.jsv.presentation.databinding.FragmentSelectMosaicEditBinding

@AndroidEntryPoint
class SelectMosaicEditFragment :
    BaseFragment<FragmentSelectMosaicEditBinding>(R.layout.fragment_select_mosaic_edit) {

    private val viewModel: MosaicViewModel by activityViewModels()

    override fun onResume() {
        super.onResume()

        initView()
    }

    override fun initView() {
        binding.buttonMosaic.setOnClickListener {
            navController.navigate(R.id.action_selectMosaicEditFragment_to_faceSelectFragment)
            viewModel.originalImage.value?.let { it1 -> viewModel.getFaceList(it1) }
        }

        binding.buttonEdit.setOnClickListener {
            navController.navigate(R.id.action_selectMosaicEditFragment_to_photoEditFragment)
        }
    }
}