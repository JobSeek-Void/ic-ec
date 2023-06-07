package team.jsv.icec.ui.camera

import android.os.Build
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import team.jsv.icec.base.BaseActivity
import team.jsv.icec.util.PermissionUtil
import team.jsv.icec.util.SettingViewUtil
import team.jsv.icec.util.requestPermissions
import team.jsv.presentation.R
import team.jsv.presentation.databinding.ActivityCameraBinding

class CameraActivity : BaseActivity<ActivityCameraBinding>(R.layout.activity_camera) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermissions(PermissionUtil.getPermissions())
    }


    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        hideSystemUI()
    }

    private fun hideSystemUI() {
        if (Build.VERSION.SDK_INT >= VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
            window.insetsController?.hide(WindowInsets.Type.statusBars())
            setBottomMargin()
        } else {
            window.decorView.systemUiVisibility =
                (View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
        }
    }

    private fun setBottomMargin() {
        val layoutParams =
            binding.cameraNavHostFragment.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.setMargins(
            0, 0, 0,
            SettingViewUtil.getNavigationBarHeightDP(this)
        )
        binding.cameraNavHostFragment.layoutParams = layoutParams
    }

    companion object {
        const val RESOURCE_STATUS_NAME = "status_bar_height"
        const val RESOURCE_NAVIGATION_NAME = "navigation_bar_height"
        const val RESOURCE_DEF_TYPE = "dimen"
        const val RESOURCE_DEF_PACKAGE = "android"
    }
}
