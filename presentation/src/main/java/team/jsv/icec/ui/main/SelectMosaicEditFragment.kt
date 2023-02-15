package team.jsv.icec.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import team.jsv.icec.base.BaseFragment
import team.jsv.icec.ui.main.mosaic.MosaicViewModel
import team.jsv.presentation.R
import team.jsv.presentation.databinding.FragmentSelectMosaicEditBinding

class SelectMosaicEditFragment :
    BaseFragment<FragmentSelectMosaicEditBinding>(R.layout.fragment_select_mosaic_edit) {

    private val viewModel: MosaicViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()

        binding.mosaicButton.setOnClickListener {
            navController.navigate(R.id.action_selectMosaicEditFragment_to_faceSelectFragment)
            viewModel.getImage().observe(viewLifecycleOwner) { image ->
                viewModel.getFaceList(image)
            }
        }

        binding.editButton.setOnClickListener {
            it.findNavController()
                .navigate(R.id.action_selectMosaicEditFragment_to_photoEditFragment)
        }
    }


    override fun initView() {

    }
}