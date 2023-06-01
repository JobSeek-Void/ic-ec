package team.jsv.icec.base

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import team.jsv.icec.util.LoadingDialog

abstract class BaseActivity<T : ViewDataBinding>(
    @LayoutRes private val layoutId: Int
) : AppCompatActivity() {

    protected lateinit var binding: T
    protected val dialog by lazy { LoadingDialog(context = binding.root.context) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, layoutId)
        binding.lifecycleOwner = this
    }

    override fun onDestroy() {
        binding.unbind()
        super.onDestroy()
    }
}