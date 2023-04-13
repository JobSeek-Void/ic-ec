@file:SuppressLint("ResourceType")

package team.jsv.icec.ui.takepicture

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import team.jsv.icec.base.BaseActivity
import team.jsv.presentation.R
import team.jsv.presentation.databinding.ActivityTakePictureBinding
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

enum class SettingRatio(val id: Int) {
    RATIO_1_1(1),
    RATIO_3_4(2),
    RATIO_9_16(3),
    RATIO_FULL(4);

    companion object {
        const val AMOUNT_1_1 = 1
        const val AMOUNT_3_4 = 4 / 3
        const val AMOUNT_9_16 = 16 / 9
    }
}

class TakePictureActivity :
    BaseActivity<ActivityTakePictureBinding>(R.layout.activity_take_picture) {

    companion object {
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    }

    private lateinit var cameraExecutor: ExecutorService
    private lateinit var cameraSelector: CameraSelector
    private var imageCapture: ImageCapture? = null
    private var imageUri: Uri? = null
    private var ratioState = SettingRatio.RATIO_1_1.id

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
        resizeCameraView(SettingRatio.RATIO_1_1.id)
        ratioState = SettingRatio.RATIO_1_1.id
    }

    override fun onResume() {
        super.onResume()
        initClickListener()
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
                SettingRatio.RATIO_1_1.id -> {
                    ratioState = SettingRatio.RATIO_3_4.id
                    binding.imageviewRatio.setImageResource(R.drawable.ic_3_4_ratio_22_33)
                    resizeCameraView(SettingRatio.RATIO_3_4.id)
                    reconnectView(SettingRatio.RATIO_3_4.id)
                }

                SettingRatio.RATIO_3_4.id -> {
                    ratioState = SettingRatio.RATIO_9_16.id
                    binding.imageviewRatio.setImageResource(R.drawable.ic_9_16_ratio_22_33)
                    resizeCameraView(SettingRatio.RATIO_9_16.id)
                    reconnectView(SettingRatio.RATIO_9_16.id)
                }

                SettingRatio.RATIO_9_16.id -> {
                    ratioState = SettingRatio.RATIO_FULL.id
                    binding.imageviewRatio.setImageResource(R.drawable.ic_full_ratio_22_33)
                    resizeCameraView(SettingRatio.RATIO_FULL.id)
                    reconnectView(SettingRatio.RATIO_FULL.id)
                }

                SettingRatio.RATIO_FULL.id -> {
                    ratioState = SettingRatio.RATIO_1_1.id
                    binding.imageviewRatio.setImageResource(R.drawable.ic_1_1_ratio_22_33)
                    resizeCameraView(SettingRatio.RATIO_1_1.id)
                    reconnectView(SettingRatio.RATIO_1_1.id)
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
            SettingRatio.RATIO_1_1.id -> {
                constraintSet.connect(
                    binding.cameraPreview.id,
                    ConstraintSet.TOP,
                    binding.imageviewReverse.id,
                    ConstraintSet.BOTTOM
                )
                constraintSet.connect(
                    binding.cameraPreview.id,
                    ConstraintSet.LEFT,
                    ConstraintSet.PARENT_ID,
                    ConstraintSet.LEFT
                )
                constraintSet.connect(
                    binding.cameraPreview.id,
                    ConstraintSet.RIGHT,
                    ConstraintSet.PARENT_ID,
                    ConstraintSet.RIGHT
                )
                constraintSet.connect(
                    binding.cameraPreview.id,
                    ConstraintSet.BOTTOM,
                    binding.buttonCapture.id,
                    ConstraintSet.TOP
                )
            }

            SettingRatio.RATIO_9_16.id -> {
                constraintSet.connect(
                    binding.cameraPreview.id,
                    ConstraintSet.TOP,
                    ConstraintSet.PARENT_ID,
                    ConstraintSet.TOP
                )
                constraintSet.connect(
                    binding.cameraPreview.id,
                    ConstraintSet.LEFT,
                    ConstraintSet.PARENT_ID,
                    ConstraintSet.LEFT
                )
                constraintSet.connect(
                    binding.cameraPreview.id,
                    ConstraintSet.RIGHT,
                    ConstraintSet.PARENT_ID,
                    ConstraintSet.RIGHT
                )
            }

            SettingRatio.RATIO_FULL.id -> {
                constraintSet.connect(
                    binding.cameraPreview.id,
                    ConstraintSet.TOP,
                    ConstraintSet.PARENT_ID,
                    ConstraintSet.TOP
                )
                constraintSet.connect(
                    binding.cameraPreview.id,
                    ConstraintSet.LEFT,
                    ConstraintSet.PARENT_ID,
                    ConstraintSet.LEFT
                )
                constraintSet.connect(
                    binding.cameraPreview.id,
                    ConstraintSet.RIGHT,
                    ConstraintSet.PARENT_ID,
                    ConstraintSet.RIGHT
                )
                constraintSet.connect(
                    binding.cameraPreview.id,
                    ConstraintSet.BOTTOM,
                    ConstraintSet.PARENT_ID,
                    ConstraintSet.BOTTOM
                )
            }
        }

        constraintSet.applyTo(binding.constraintLayout)
    }

    private fun resizeCameraView(id: Int) {
        val display = binding.root.resources?.displayMetrics
        val deviceWidth = display?.widthPixels ?: 0

        val layoutParams = binding.cameraPreview.layoutParams

        when (id) {
            SettingRatio.RATIO_1_1.id -> {
                layoutParams.height = deviceWidth * SettingRatio.AMOUNT_1_1
            }

            SettingRatio.RATIO_3_4.id -> {
                layoutParams.height = deviceWidth * SettingRatio.AMOUNT_3_4
            }

            SettingRatio.RATIO_9_16.id -> {
                layoutParams.height = deviceWidth * SettingRatio.AMOUNT_9_16
            }

            SettingRatio.RATIO_FULL.id -> {
                layoutParams.height = display?.heightPixels ?: 0
            }
        }

        binding.cameraPreview.layoutParams = layoutParams
        startCamera()
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
            val deviceWidth = display?.widthPixels ?: 0

            imageCapture = ImageCapture.Builder().apply {
                when (ratioState) {
                    SettingRatio.RATIO_1_1.id -> {
                        setTargetResolution(
                            Size(
                                deviceWidth,
                                deviceWidth * SettingRatio.AMOUNT_1_1
                            )
                        )
                    }

                    SettingRatio.RATIO_3_4.id -> {
                        setTargetResolution(
                            Size(
                                deviceWidth,
                                deviceWidth * SettingRatio.AMOUNT_3_4
                            )
                        )
                    }

                    SettingRatio.RATIO_9_16.id -> {
                        setTargetResolution(
                            Size(
                                deviceWidth,
                                deviceWidth * SettingRatio.AMOUNT_9_16
                            )
                        )
                    }

                    SettingRatio.RATIO_FULL.id -> {
                        setTargetResolution(
                            Size(
                                deviceWidth, display?.heightPixels ?: 0
                            )
                        )
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
    }
}
