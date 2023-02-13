package team.jsv.icec.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import team.jsv.presentation.R
import team.jsv.presentation.databinding.FragmentSelectMosaicEditBinding

class SelectMosaicEditFragment : Fragment() {

    private var _binding: FragmentSelectMosaicEditBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSelectMosaicEditBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        binding.mosaicButton.setOnClickListener {
            it.findNavController()
                .navigate(R.id.action_selectMosaicEditFragment_to_faceSelectFragment)
        }

        binding.editButton.setOnClickListener {
            it.findNavController()
                .navigate(R.id.action_selectMosaicEditFragment_to_photoEditFragment)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}