package team.jsv.icec.base

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.fragment.NavHostFragment
import team.jsv.icec.util.LoadingDialog

abstract class BaseFragment<T : ViewDataBinding>(
    @LayoutRes val layoutRes: Int,
) : Fragment() {
    private var _binding: T? = null
    protected val binding: T get() = _binding!!

    protected val currentTheme: Int get() = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
    protected val navController: NavController get() = NavHostFragment.findNavController(this)
    protected val dialog by lazy { LoadingDialog(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, layoutRes, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this@BaseFragment
        initView()
    }

    fun navigate(direction: NavDirections) {
        navController.navigate(direction)
    }

    fun popBackStack() {
        navController.popBackStack()
    }
    
    abstract fun initView()

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
