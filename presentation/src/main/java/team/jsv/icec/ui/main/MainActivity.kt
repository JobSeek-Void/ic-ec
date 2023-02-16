package team.jsv.icec.ui.main

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import dagger.hilt.android.AndroidEntryPoint
import team.jsv.icec.base.BaseActivity
import team.jsv.icec.ui.main.mosaic.MosaicViewModel
import team.jsv.presentation.R
import team.jsv.presentation.databinding.ActivityMainBinding
import java.io.File

@AndroidEntryPoint
class MainActivity :
    BaseActivity<ActivityMainBinding>(R.layout.activity_main) {

    private val viewModel: MosaicViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO(ham2174) : 갤러리 혹은 카메라로 찍은 사진 데이터를 가져와야함. TakePictureActivity -> MainActivity 이미지 데이터 전달 필요
        // 권한을 허용하도록 요청
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->
            results.forEach {
                if (!it.value) {
                    Toast.makeText(applicationContext, "권한 허용 필요", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE))

        viewModel.setImage(File("/storage/emulated/0/Pictures/test_3.jpg"))
    }

}