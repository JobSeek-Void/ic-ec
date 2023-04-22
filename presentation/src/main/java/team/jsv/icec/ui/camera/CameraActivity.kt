@file:SuppressLint("ResourceType")

package team.jsv.icec.ui.camera

import android.annotation.SuppressLint
import android.content.ContentValues
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import androidx.activity.viewModels
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import team.jsv.icec.base.BaseActivity
import team.jsv.icec.util.SettingViewUtil.reconnectView
import team.jsv.icec.util.SettingViewUtil.resizeView
import team.jsv.presentation.R
import team.jsv.presentation.databinding.ActivityCameraBinding
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.roundToInt

class CameraActivity :
    BaseActivity<ActivityCameraBinding>(R.layout.activity_camera) {

    companion object {
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HHmmss"
    }

    private val deviceHeight get() = binding.root.resources?.displayMetrics?.heightPixels ?: 0
    private val deviceWidth get() = binding.root.resources?.displayMetrics?.widthPixels ?: 0

    private lateinit var cameraExecutor: ExecutorService
    private lateinit var cameraSelector: CameraSelector
    private var imageCapture: ImageCapture? = null
    private val viewModel: CameraViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.setRatioState(SettingRatio.RATIO_1_1.id)
        cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
        resizeCameraView()
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
            when (viewModel.ratioState.value) {
                SettingRatio.RATIO_1_1.id -> {
                    viewModel.setRatioState(SettingRatio.RATIO_3_4.id)
                    binding.imageviewRatio.setImageResource(R.drawable.ic_3_4_ratio_22_33)
                }

                SettingRatio.RATIO_3_4.id -> {
                    viewModel.setRatioState(SettingRatio.RATIO_9_16.id)
                    binding.imageviewRatio.setImageResource(R.drawable.ic_9_16_ratio_22_33)
                    reconnectView(
                        binding.constraintLayout,
                        binding.cameraPreview,
                        binding.constraintLayout,
                        ConstraintSet.TOP,
                        binding.constraintLayout,
                        ConstraintSet.BOTTOM,
                        ConnenctState.TOP.id
                    )
                }

                SettingRatio.RATIO_9_16.id -> {
                    viewModel.setRatioState(SettingRatio.RATIO_FULL.id)
                    binding.imageviewRatio.setImageResource(R.drawable.ic_full_ratio_22_33)
                    reconnectView(
                        binding.constraintLayout,
                        binding.cameraPreview,
                        binding.constraintLayout,
                        ConstraintSet.TOP,
                        binding.constraintLayout,
                        ConstraintSet.BOTTOM,
                        ConnenctState.TOPBOTTOM.id
                    )
                }

                SettingRatio.RATIO_FULL.id -> {
                    viewModel.setRatioState(SettingRatio.RATIO_1_1.id)
                    binding.imageviewRatio.setImageResource(R.drawable.ic_1_1_ratio_22_33)
                    reconnectView(
                        binding.constraintLayout,
                        binding.cameraPreview,
                        binding.imageviewReverse,
                        ConstraintSet.BOTTOM,
                        binding.buttonCapture,
                        ConstraintSet.TOP,
                        ConnenctState.TOPBOTTOM.id
                    )
                }
            }
        }

        binding.buttonCapture.setOnClickListener {
            takePhoto()
        }
    }

    private fun resizeCameraView() {
        viewModel.ratioState.observe(this) { it ->
            val layoutParams = binding.cameraPreview.layoutParams

            binding.cameraPreview.layoutParams =
                resizeView(layoutParams, it, deviceWidth, deviceHeight)
            startCamera()
        }
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
                when (viewModel.ratioState.value) {
                    SettingRatio.RATIO_1_1.id -> {
                        setTargetResolution(
                            Size(
                                deviceWidth,
                                (deviceWidth * SettingRatio.AMOUNT_1_1).roundToInt()
                            )
                        )
                    }

                    SettingRatio.RATIO_3_4.id -> {
                        setTargetResolution(
                            Size(
                                deviceWidth,
                                (deviceWidth * SettingRatio.AMOUNT_3_4).roundToInt()
                            )
                        )
                    }

                    SettingRatio.RATIO_9_16.id -> {
                        setTargetResolution(
                            Size(
                                deviceWidth,
                                (deviceWidth * SettingRatio.AMOUNT_9_16).roundToInt()
                            )
                        )
                    }

                    SettingRatio.RATIO_FULL.id -> {
                        setTargetResolution(
                            Size(
                                deviceWidth, deviceHeight
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

        val name = SimpleDateFormat(FILENAME_FORMAT, Locale("ko", "KR"))
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
}
