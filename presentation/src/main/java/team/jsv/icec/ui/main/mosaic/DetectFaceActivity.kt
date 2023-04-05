package team.jsv.icec.ui.main.mosaic

import android.os.Bundle
import team.jsv.icec.base.BaseActivity
import team.jsv.presentation.R
import team.jsv.presentation.databinding.ActivityDetectFaceBinding

class DetectFaceActivity : BaseActivity<ActivityDetectFaceBinding>(R.layout.activity_detect_face) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onResume() {
        super.onResume()
        initClickListener()
    }

    private fun initClickListener() {
        binding.imageviewBack.setOnClickListener {
            finish()
        }

        binding.buttonNext.setOnClickListener {
//            startActivity(Intent(this, 모자이크 2단계 page))
        }
    }
}