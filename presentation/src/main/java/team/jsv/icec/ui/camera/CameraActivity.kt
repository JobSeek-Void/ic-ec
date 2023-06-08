package team.jsv.icec.ui.camera

import team.jsv.icec.base.BaseActivity
import team.jsv.icec.util.hideSystemUI
import team.jsv.presentation.R
import team.jsv.presentation.databinding.ActivityCameraBinding

class CameraActivity : BaseActivity<ActivityCameraBinding>(R.layout.activity_camera) {

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        hideSystemUI(binding.cameraNavHostFragment)
    }

    companion object {
        const val RESOURCE_STATUS_NAME = "status_bar_height"
        const val RESOURCE_NAVIGATION_NAME = "navigation_bar_height"
        const val RESOURCE_DEF_TYPE = "dimen"
        const val RESOURCE_DEF_PACKAGE = "android"
    }
}
