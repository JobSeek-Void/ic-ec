package team.jsv.icec.ui.main.start

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import team.jsv.icec.base.BaseActivity
import team.jsv.icec.base.startActivityWithAnimation
import team.jsv.icec.ui.camera.CameraActivity
import team.jsv.icec.ui.main.MainActivity
import team.jsv.icec.util.Extras
import team.jsv.icec.util.PermissionUtil
import team.jsv.icec.util.requestPermissions
import team.jsv.presentation.R
import team.jsv.presentation.databinding.ActivityStartBinding

class StartActivity : BaseActivity<ActivityStartBinding>(R.layout.activity_start) {

    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestPermissions(PermissionUtil.getPermissions())
        setImagePickerLauncher()
    }

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
                    val imagePath: String? =
                        imageUri?.let { uri -> getPathFromUri(this@StartActivity, uri) }

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

    private fun getPathFromUri(context: Context, uri: Uri): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.contentResolver.query(uri, projection, null, null, null)
        return cursor?.use {
            val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            if (cursor.moveToFirst()) cursor.getString(columnIndex) else null
        }
    }

}