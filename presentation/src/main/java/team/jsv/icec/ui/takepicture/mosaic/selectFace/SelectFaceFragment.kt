package team.jsv.icec.ui.main.mosaic.selectFace

import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import team.jsv.icec.base.BaseFragment
import team.jsv.icec.base.EventObserver
import team.jsv.icec.ui.main.mosaic.MosaicViewModel
import team.jsv.icec.ui.main.mosaic.ScreenStep
import team.jsv.presentation.R
import team.jsv.presentation.databinding.FragmentSelectFaceBinding

@AndroidEntryPoint
class SelectFaceFragment :
    BaseFragment<FragmentSelectFaceBinding>(R.layout.fragment_select_face) {

    private val viewModel: MosaicViewModel by activityViewModels()

    override fun initView() {
        viewModel.backPress.observe(this, EventObserver {
            popBackStack()
        })

        binding.buttonNext.setOnClickListener {
            navController.navigate(R.id.action_faceSelectFragment_to_faceMosaicFragment)
            viewModel.run {
                setScreen(ScreenStep.MosaicFace)
                getMosaicImage()
            }
        }
    }
}