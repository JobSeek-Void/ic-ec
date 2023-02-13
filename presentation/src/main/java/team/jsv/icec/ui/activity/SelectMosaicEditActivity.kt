package team.jsv.icec.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import team.jsv.presentation.databinding.ActivitySelectMosaicEditBinding

class SelectMosaicEditActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySelectMosaicEditBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectMosaicEditBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}