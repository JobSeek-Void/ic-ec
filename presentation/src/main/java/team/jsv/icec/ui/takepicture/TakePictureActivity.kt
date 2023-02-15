package team.jsv.icec.ui.takepicture

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import team.jsv.presentation.databinding.ActivityTakePictureBinding

class TakePictureActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityTakePictureBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityTakePictureBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.ratioButton.setOnClickListener {
            // 비율 옵션 버튼
        }

        viewBinding.galleryButton.setOnClickListener {
            // 갤러리 버튼
        }

        viewBinding.captureButton.setOnClickListener {
            // 사진 찍기 버튼
        }

        viewBinding.reverseButton.setOnClickListener {
            // 좌우 반전 버튼
        }
    }
}