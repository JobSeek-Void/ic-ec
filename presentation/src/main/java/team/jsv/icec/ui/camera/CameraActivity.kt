package team.jsv.icec.ui.camera

import android.os.Bundle
import team.jsv.icec.base.BaseActivity
import team.jsv.icec.util.PermissionUtil
import team.jsv.icec.util.requestPermissions
import team.jsv.presentation.R
import team.jsv.presentation.databinding.ActivityCameraBinding

class CameraActivity :
    BaseActivity<ActivityCameraBinding>(R.layout.activity_camera) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermissions(PermissionUtil.getPermissions())
    }
}
