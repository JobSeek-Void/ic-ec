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
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.camera.core.AspectRatio.Ratio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.constraintlayout.widget.ConstraintSet.Constraint
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.marginTop
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
    ThreeOnFour(2),
    NineOnSixteen(3),
    Full(4)
}

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class TakePictureActivity :
    BaseActivity<ActivityTakePictureBinding>(R.layout.activity_take_picture) {
    private lateinit var cameraExecutor: ExecutorService
    private var imageCapture: ImageCapture? = null
    private var cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
    private var imageUri: Uri? = null
    private var ratioState = RatioId.OneOnOne.id

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startCameraWithPermission()
    }

    override fun onResume() {
        super.onResume()
        initClickListener()
        resizeCameraView(RatioId.OneOnOne.id)
        ratioState = RatioId.OneOnOne.id
    }

    private fun initClickListener() {
        binding.imageviewReverse.setOnClickListener {
            cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA) {
                CameraSelector.DEFAULT_BACK_CAMERA
            } else {
                CameraSelector.DEFAULT_FRONT_CAMERA
            }

            startCamera()
        }

        binding.imageviewClose.setOnClickListener {
            finish()
        }

        binding.imageviewRatio.setOnClickListener {
            when (ratioState) {
                RatioId.OneOnOne.id -> {
                    ratioState = RatioId.ThreeOnFour.id
                    binding.imageviewRatio.setImageResource(R.drawable.bt_three_on_four_22_33)
                    resizeCameraView(RatioId.ThreeOnFour.id)
                    reconnectView(RatioId.ThreeOnFour.id)
                }

                RatioId.ThreeOnFour.id -> {
                    ratioState = RatioId.NineOnSixteen.id
                    binding.imageviewRatio.setImageResource(R.drawable.bt_nine_on_sixteen_22_33)
                    resizeCameraView(RatioId.NineOnSixteen.id)
                    reconnectView(RatioId.NineOnSixteen.id)
                }

                RatioId.NineOnSixteen.id -> {
                    ratioState = RatioId.Full.id
                    binding.imageviewRatio.setImageResource(R.drawable.bt_full_22_33)
                    resizeCameraView(RatioId.Full.id)
                    reconnectView(RatioId.Full.id)
                }

                RatioId.Full.id -> {
                    ratioState = RatioId.OneOnOne.id
                    binding.imageviewRatio.setImageResource(R.drawable.bt_one_on_one_22_33)
                    resizeCameraView(RatioId.OneOnOne.id)
                    reconnectView(RatioId.OneOnOne.id)
                }
            }
        }

        binding.buttonCapture.setOnClickListener {
            takePhoto()
        }
    }

    private fun reconnectView(id: Int) {
        val constraintSet = ConstraintSet()
        constraintSet.clone(binding.constraintLayout)

        when (id) {
            RatioId.OneOnOne.id -> {
                constraintSet.connect(binding.cameraPreview.id, ConstraintSet.TOP, binding.imageviewReverse.id, ConstraintSet.BOTTOM)
                constraintSet.connect(binding.cameraPreview.id, ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT)
                constraintSet.connect(binding.cameraPreview.id, ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT)
                constraintSet.connect(binding.cameraPreview.id, ConstraintSet.BOTTOM, binding.buttonCapture.id, ConstraintSet.TOP)
            }

            RatioId.NineOnSixteen.id -> {
                constraintSet.connect(binding.cameraPreview.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
                constraintSet.connect(binding.cameraPreview.id, ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT)
                constraintSet.connect(binding.cameraPreview.id, ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT)
            }

            RatioId.Full.id -> {
                constraintSet.connect(binding.cameraPreview.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
                constraintSet.connect(binding.cameraPreview.id, ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT)
                constraintSet.connect(binding.cameraPreview.id, ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT)
                constraintSet.connect(binding.cameraPreview.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
            }
        }

        constraintSet.applyTo(binding.constraintLayout)
    }

    private fun resizeCameraView(id: Int) {
        val display = binding.root.resources?.displayMetrics
        val deviceWidth = display?.widthPixels

        val layoutParams = binding.cameraPreview.layoutParams

        when (id) {
            RatioId.OneOnOne.id -> {
                layoutParams.height = deviceWidth!!
            }

            RatioId.ThreeOnFour.id -> {
                layoutParams.height = deviceWidth!! / 3 * 4
            }

            RatioId.NineOnSixteen.id -> {
                layoutParams.height = deviceWidth!! / 9 * 16
            }

            RatioId.Full.id -> {
                layoutParams.height = display?.heightPixels!!
            }
        }

        binding.cameraPreview.layoutParams = layoutParams
        startCamera()
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

            val display = binding.root.resources?.displayMetrics
            val deviceWidth = display?.widthPixels

            imageCapture = ImageCapture.Builder().apply {
                when (ratioState) {
                    RatioId.OneOnOne.id -> {
                        setTargetResolution(Size(deviceWidth!!, deviceWidth))
                    }

                    RatioId.ThreeOnFour.id -> {
                        setTargetResolution(Size(deviceWidth!!, deviceWidth / 3 * 4))
                    }

                    RatioId.NineOnSixteen.id -> {
                        setTargetResolution(Size(deviceWidth!!, deviceWidth / 9 * 16))
                    }

                    RatioId.Full.id -> {
                        setTargetResolution(Size(deviceWidth!!, display.heightPixels))
                    }
                }
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
                    //TODO(jiiiiiyoon) : Photo화면으로 전환
                }
            }
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        imageUri = data?.data ?: return
        //      고른 이미지 처리
    }

    companion object {
        private const val PICK_IMAGE = 100

        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    }
}
