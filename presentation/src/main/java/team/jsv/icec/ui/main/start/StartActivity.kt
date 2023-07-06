package team.jsv.icec.ui.main.start

import android.app.Activity
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.GridLayoutManager
import team.jsv.icec.base.BaseActivity
import team.jsv.icec.base.startActivityWithAnimation
import team.jsv.icec.ui.camera.CameraActivity
import team.jsv.icec.ui.main.MainActivity
import team.jsv.icec.util.Extras
import team.jsv.icec.util.PermissionUtil
import team.jsv.icec.util.getPathFromUri
import team.jsv.icec.util.requestPermissions
import team.jsv.presentation.R
import team.jsv.presentation.databinding.ActivityStartBinding

class StartActivity : BaseActivity<ActivityStartBinding>(R.layout.activity_start) {

    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestPermissions(PermissionUtil.getPermissions())
        setImagePickerLauncher()

        val projection =
            arrayOf(MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_MODIFIED)
        val cursor = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            "${MediaStore.Images.Media.DATE_MODIFIED} DESC"
        )

        val imagePaths = mutableListOf<String>()
        cursor?.use {
            val dataIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            var count = 0

            while (it.moveToNext() && count < 30) {
                val imagePath = it.getString(dataIndex)
                imagePaths.add(imagePath)
                count++
            }
        }

        cursor?.close()

        binding.rvRecentImage.layoutManager = GridLayoutManager(this, 3)
        val adapter = ImageAdapter(this, imagePaths)
        binding.rvRecentImage.adapter = adapter
        binding.rvRecentImage.addItemDecoration(
            GridSpacingItemDecoration(spanCount = 3, spacing = 16f.fromDpToPx())
        )
    }

    private fun Float.fromDpToPx(): Int =
        (this * Resources.getSystem().displayMetrics.density).toInt()

    override fun onResume() {
        super.onResume()

        initClickEvent()
    }

    private fun setImagePickerLauncher() {
        imagePickerLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data
                    val imageUri: Uri? = data?.data
                    val imagePath = imageUri?.let { getPathFromUri(it) }

                    startActivityWithAnimation<MainActivity>(intentBuilder = {
                        putExtra(Extras.ImagePath, imagePath)
                    })
                }
            }
    }

    private fun initClickEvent() {
        binding.btGallery.setOnClickListener {
            openGallery()
        }
        binding.btCamera.setOnClickListener {
            startActivityWithAnimation<CameraActivity>()
        }
    }

    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        imagePickerLauncher.launch(galleryIntent)
    }

}