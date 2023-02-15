package team.jsv.icec.ui.main.mosaic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import team.jsv.icec.base.BaseFragment
import team.jsv.presentation.R
import team.jsv.presentation.databinding.FragmentSelectFaceBinding

class SelectFaceFragment :
    BaseFragment<FragmentSelectFaceBinding>(R.layout.fragment_select_face) {

    private val viewModel: MosaicViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel.getImage().observe(viewLifecycleOwner) { image ->
            binding.image.setImageResource(image)
        }

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun initView() {

    }
}