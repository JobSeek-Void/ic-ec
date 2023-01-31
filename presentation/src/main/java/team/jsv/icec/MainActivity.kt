package team.jsv.icec

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import team.jsv.presentation.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
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