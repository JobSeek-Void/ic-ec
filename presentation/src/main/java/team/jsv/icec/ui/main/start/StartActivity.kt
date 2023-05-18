package team.jsv.icec.ui.main.start

import android.app.Activity
import android.content.Intent
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import team.jsv.icec.base.BaseActivity
import team.jsv.icec.ui.camera.CameraActivity
import team.jsv.icec.ui.main.start.adapter.GalleryImageAdapter
import team.jsv.icec.ui.main.start.model.GalleryImageItem
import team.jsv.icec.util.PermissionUtil
import team.jsv.icec.util.requestPermissions
import team.jsv.presentation.R
import team.jsv.presentation.databinding.ActivityStartBinding

class StartActivity : BaseActivity<ActivityStartBinding>(R.layout.activity_start) {
    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>
    private lateinit var galleryImageAdapter: GalleryImageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermissions(PermissionUtil.getPermissions())

        imagePickerLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data

                    if (data != null) {
                        val imageUri: Uri? = data.data
                        //Todo(jiiiiiyoon): imageUri 모자이크 화면으로 넘기기
                    }
                }
            }


        galleryImageAdapter = GalleryImageAdapter()

        binding.rvRecentPhoto.apply {
            adapter = galleryImageAdapter
            itemAnimator = null
            addItemDecoration(RecyclerViewSpacing(space = 14, topSpace = 14))
            layoutManager = GridLayoutManager(this@StartActivity, 3)
        }

    }

    override fun onResume() {
        super.onResume()
        initClickEvent()
    }

    private fun initClickEvent() {
        binding.btPhoto.setOnClickListener {
            openGallery()
        }

        binding.btCamera.setOnClickListener {
            startActivity(Intent(this@StartActivity, CameraActivity::class.java))
        }
    }

    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        imagePickerLauncher.launch(galleryIntent)
    }
}