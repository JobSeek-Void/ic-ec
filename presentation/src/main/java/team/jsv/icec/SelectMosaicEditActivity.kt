package team.jsv.icec

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import team.jsv.presentation.databinding.ActivitySelectMosaicEditBinding

class SelectMosaicEditActivity: AppCompatActivity() {

    private lateinit var viewBinding: ActivitySelectMosaicEditBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivitySelectMosaicEditBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
    }
}