package team.jsv.icec.ui.main.mosaic

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import team.jsv.icec.base.BaseActivity
import team.jsv.icec.ui.main.MainActivity
import team.jsv.presentation.R
import team.jsv.presentation.databinding.ActivityMainMosaicStep1Binding

class MainMosaicStep1 : BaseActivity<ActivityMainMosaicStep1Binding>(R.layout.activity_main_mosaic_step1) {
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