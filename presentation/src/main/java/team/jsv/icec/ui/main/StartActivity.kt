package team.jsv.icec.ui.main

import android.content.Intent
import android.os.Bundle
import team.jsv.icec.base.BaseActivity
import team.jsv.icec.ui.camera.CameraActivity
import team.jsv.icec.util.PermissionUtil
import team.jsv.icec.util.requestPermissions
import team.jsv.presentation.R
import team.jsv.presentation.databinding.ActivityStartBinding

class StartActivity : BaseActivity<ActivityStartBinding>(R.layout.activity_start) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermissions(PermissionUtil.getPermissions())
    }

    override fun onResume() {
        super.onResume()
        initClickEvent()
    }

    private fun initClickEvent() {
        binding.btPhoto.setOnClickListener {
            //Todo(jiiiiyoon): 갤러리 연동하기
        }

        binding.btCamera.setOnClickListener {
            startActivity(Intent(this@StartActivity, CameraActivity::class.java))
        }
    }
}