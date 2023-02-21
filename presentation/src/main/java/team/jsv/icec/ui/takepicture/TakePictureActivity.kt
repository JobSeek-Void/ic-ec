@file:SuppressLint("ResourceType")

package team.jsv.icec.ui.takepicture

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import androidx.annotation.RequiresApi
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import team.jsv.icec.base.BaseActivity
import team.jsv.icec.util.PermissionUtil
import team.jsv.presentation.R
import team.jsv.presentation.databinding.ActivityTakePictureBinding
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

enum class RatioId(val id: Int) {
    OneOnOne(1),
    FourOnThree(2),
    NineOnSixteen(3)
}

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class TakePictureActivity :
    BaseActivity<ActivityTakePictureBinding>(R.layout.activity_take_picture) {
    private lateinit var cameraExecutor: ExecutorService
    private var imageCapture: ImageCapture? = null
    private var cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startCameraWithPermission()
        getLastImageFromGallery()
    }

    override fun onResume() {
        super.onResume()
        initClickListener()
        resizeCameraView(RatioId.FourOnThree.id)
    }

    private fun initClickListener() {
        binding.imageviewOneOnOne.setOnClickListener {
            resizeCameraView(RatioId.OneOnOne.id)
        }

        binding.imageviewThreeOnFour.setOnClickListener {
            resizeCameraView(RatioId.FourOnThree.id)
        }

        binding.imageviewNineOnSixteen.setOnClickListener {
            resizeCameraView(RatioId.NineOnSixteen.id)
        }

        binding.buttonCapture.setOnClickListener {
            takePhoto()
        }

        binding.buttonReverse.setOnClickListener {
            if (cameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA) {
                cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                startCamera()
            } else {
                cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
                startCamera()
            }
        }

        binding.imageviewGallery.setOnClickListener {
            openGallery()
        }
    }

    private fun resizeCameraView(id: Int) {
        val display = binding.root.resources?.displayMetrics
        val deviceWidth = display?.widthPixels

        val layoutParams = binding.cameraPreview.layoutParams

        when (id) {
            RatioId.OneOnOne.id -> {
                layoutParams.height = deviceWidth!!
            }

            RatioId.FourOnThree.id -> {
                layoutParams.height = deviceWidth!! / 3 * 4
            }

            RatioId.NineOnSixteen.id -> {
                layoutParams.height = deviceWidth!! / 9 * 16
            }
        }

        binding.cameraPreview.layoutParams = layoutParams
    }

    private fun getLastImageFromGallery() {
        val uriExternal: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(
            MediaStore.Images.ImageColumns._ID,
            MediaStore.Images.Media._ID,
            MediaStore.Images.ImageColumns.DATE_ADDED,
            MediaStore.Images.ImageColumns.MIME_TYPE
        )
        val cursor: Cursor = this.contentResolver.query(
            uriExternal, projection, null,
            null, MediaStore.Images.ImageColumns.DATE_ADDED + " DESC"
        )!!

        if (cursor.moveToFirst()) {
            val columnIndexID = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val imageId: Long = cursor.getLong(columnIndexID)
            val imageURI = Uri.withAppendedPath(uriExternal, "" + imageId)

            Glide.with(this)
                .load(imageURI)
                .into(binding.imageviewGallery)
        }
        cursor.close()
    }

    private fun allPermissionsGranted() = PermissionUtil.getPermissions().all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.cameraPreview.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder().apply {
                setTargetResolution(Size(1080, 1080))
            }.build()

            try {
                cameraProvider.unbindAll()

                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )
            } catch (e: Exception) {
                Log.e("cameraError", "Use case binding failed ", e)
            }
        }, ContextCompat.getMainExecutor(this))
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.KOREA)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
            }
        }

        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(
                contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
            .build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e("captureError", "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    getLastImageFromGallery()
                }
            }
        )
    }

    private fun startCameraWithPermission() {
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, PermissionUtil.getPermissions().toTypedArray(), REQUEST_CODE_PERMISSIONS
            )
        }
    }

    private fun openGallery() {
        val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(gallery, PICK_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        imageUri = data?.data ?: return
        //      고른 이미지 처리
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private const val PICK_IMAGE = 100

        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    }
}
