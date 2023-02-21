package team.jsv.icec.ui.main.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import team.jsv.icec.base.BaseFragment
import team.jsv.presentation.R
import team.jsv.presentation.databinding.FragmentPhotoEditBinding

class PhotoEditFragment :
    BaseFragment<FragmentPhotoEditBinding>(R.layout.fragment_photo_edit) {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun initView() {

    }
}