package team.jsv.icec.ui.camera

import team.jsv.icec.base.BaseActivity
import team.jsv.icec.util.setFullScreen
import team.jsv.presentation.R
import team.jsv.presentation.databinding.ActivityCameraBinding

class CameraActivity : BaseActivity<ActivityCameraBinding>(R.layout.activity_camera) {

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)

        setFullScreen()
    }
}
