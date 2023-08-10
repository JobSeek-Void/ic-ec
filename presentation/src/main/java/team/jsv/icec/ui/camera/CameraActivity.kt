package team.jsv.icec.ui.camera

import android.os.Build
import android.os.Bundle
import androidx.core.content.ContextCompat
import team.jsv.icec.base.BaseActivity
import team.jsv.icec.util.getNavigationHeightDIP
import team.jsv.icec.util.setBottomMargin
import team.jsv.icec.util.setFullScreen
import team.jsv.presentation.R
import team.jsv.presentation.databinding.ActivityCameraBinding

class CameraActivity : BaseActivity<ActivityCameraBinding>(R.layout.activity_camera) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFullScreen()

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O_MR1) {
            window.navigationBarColor = ContextCompat.getColor(this, R.color.black)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            binding.cameraNavHostFragment.setBottomMargin(this.getNavigationHeightDIP().toInt())
        }
    }

}