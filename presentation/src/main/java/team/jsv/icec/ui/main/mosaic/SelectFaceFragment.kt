package team.jsv.icec.ui.main.mosaic

import android.util.Log
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import team.jsv.icec.base.BaseFragment
import team.jsv.icec.base.showToast
import team.jsv.presentation.R
import team.jsv.presentation.databinding.FragmentSelectFaceBinding

@AndroidEntryPoint
class SelectFaceFragment :
    BaseFragment<FragmentSelectFaceBinding>(R.layout.fragment_select_face) {

    private val viewModel: MosaicViewModel by activityViewModels()

    override fun initView() {
        viewModel.detectFaces.observe(this) {
            Log.d("얼굴 리스트", it.faceList.toString())
        }
        viewModel.event.observe(this) {
            when (it) {
                is MosaicEvent.SendToast -> binding.root.context.showToast(it.message)
            }
        }
    }
}