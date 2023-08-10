package team.jsv.icec.ui.main.start

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import team.jsv.icec.base.BaseActivity
import team.jsv.icec.base.startActivityWithAnimation
import team.jsv.icec.ui.camera.CameraActivity
import team.jsv.icec.ui.main.MainActivity
import team.jsv.icec.ui.main.start.adapter.RecentImageAdapter
import team.jsv.icec.util.Extras
import team.jsv.icec.util.PermissionUtil
import team.jsv.icec.util.getPathFromUri
import team.jsv.icec.util.requestPermissions
import team.jsv.icec.util.setICECThemeBottomNavigationColor
import team.jsv.icec.util.showToast
import team.jsv.icec.util.toPx
import team.jsv.presentation.R
import team.jsv.presentation.databinding.ActivityStartBinding

class StartActivity : BaseActivity<ActivityStartBinding>(R.layout.activity_start) {
    private val viewModel: StartViewModel by viewModels()
    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>
    private val recentImageAdapter by lazy {
        RecentImageAdapter(
            onClick = { imagePath ->
                startActivityWithAnimation<MainActivity>(intentBuilder = {
                    putExtra(Extras.ImagePath, imagePath)
                })
            }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestPermissions(
            permission = PermissionUtil.getPermissions(),
            onPermissionAccepted = { showToast(getString(R.string.accept_permission)) },
            onPermissionDenied = {
                showToast(getString(R.string.reject_permission))
                finish()
            }
        )
        setICECThemeBottomNavigationColor()

        setImagePickerLauncher()
        initRecentImageRecyclerView()
    }

    override fun onResume() {
        super.onResume()

        initClickEvent()
        if (checkReadStoragePermission()) {
            updateImageList(loadRecentImages())
        }
    }

    private fun checkReadStoragePermission(): Boolean {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
        val acceptPermission = ContextCompat.checkSelfPermission(this, permission)
        return acceptPermission == PackageManager.PERMISSION_GRANTED
    }

    private fun initRecentImageRecyclerView() {
        binding.rvRecentImage.apply {
            layoutManager = GridLayoutManager(context, 3)
            adapter = recentImageAdapter
            itemAnimator = null

            addItemDecoration(
                GridSpacingItemDecoration(
                    spanCount = SPAN_COUNT,
                    spacing = SPACING.toPx.toInt()
                )
            )
        }
    }

    private fun loadRecentImages(): List<String> {
        val imagePaths: MutableList<String> = mutableListOf()

        val projection =
            arrayOf(MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_MODIFIED)
        val cursor = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            "${MediaStore.Images.Media.DATE_MODIFIED} DESC"
        )

        cursor?.use {
            val dataIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            imagePaths.clear()

            for (count in 0 until 30) {
                if (!it.moveToNext()) break

                val imagePath = it.getString(dataIndex)
                imagePaths.add(imagePath)
            }
        }

        cursor?.close()

        return imagePaths
    }

    private fun updateImageList(imagePaths: List<String>) {
        viewModel.setImagePaths(imagePaths)
        recentImageAdapter.submitList(viewModel.imagePaths.value)
    }

    private fun setImagePickerLauncher() {
        imagePickerLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data = result.data
                    val imageUri = data?.data
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

    companion object {
        const val SPAN_COUNT = 3
        const val SPACING = 16f
    }
}