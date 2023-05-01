@file:SuppressLint("ResourceType")

package team.jsv.icec.ui.camera

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import team.jsv.icec.base.BaseActivity
import team.jsv.icec.util.ConnenctState
import team.jsv.icec.util.PermissionUtil
import team.jsv.icec.util.SettingViewUtil.reconnectView
import team.jsv.icec.util.SettingViewUtil.resizeView
import team.jsv.icec.util.requestPermissions
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

class CameraActivity : BaseActivity<ActivityCameraBinding>(R.layout.activity_camera) {
    private val deviceHeight get() = screenHeight(this)
    private val deviceWidth get() = screenWidth(this)

    private lateinit var cameraExecutor: ExecutorService
    private lateinit var cameraSelector: CameraSelector
    private var imageCapture: ImageCapture? = null
    private val viewModel: CameraViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestPermissions(PermissionUtil.getPermissions())
        viewModel.setRatioState(SettingRatio.RATIO_1_1.id)
        cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
        resizeCameraView()
    }

    override fun onResume() {
        super.onResume()
        initClickListener()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
    private fun screenHeight(activity: Activity): Int {
        val displayMetrics = DisplayMetrics()

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            activity.windowManager.currentWindowMetrics.bounds.height()
        } else {
            @Suppress("DEPRECATION")
            val display = activity.windowManager.defaultDisplay
            @Suppress("DEPRECATION")
            display.getMetrics(displayMetrics)
            displayMetrics.heightPixels
        }
    }

    private fun screenWidth(activity: Activity): Int {
        val displayMetrics = DisplayMetrics()

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            activity.windowManager.currentWindowMetrics.bounds.width()
        } else {
            @Suppress("DEPRECATION")
            val display = activity.windowManager.defaultDisplay
            @Suppress("DEPRECATION")
            display.getMetrics(displayMetrics)
            displayMetrics.widthPixels
        }
    }

    private fun initClickListener() {
        binding.ivReverse.setOnClickListener {
            cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA) {
                CameraSelector.DEFAULT_BACK_CAMERA
            } else {
                CameraSelector.DEFAULT_FRONT_CAMERA
            }

            startCamera()
        }

        binding.ivClose.setOnClickListener {
            finish()
        }

        binding.ivRatio.setOnClickListener {
            when (viewModel.ratioState.value) {
                SettingRatio.RATIO_1_1.id -> {
                    viewModel.setRatioState(SettingRatio.RATIO_3_4.id)
                    binding.ivRatio.setImageResource(R.drawable.ic_3_4_ratio_22_33)
                }

                SettingRatio.RATIO_3_4.id -> {
                    viewModel.setRatioState(SettingRatio.RATIO_9_16.id)
                    binding.ivRatio.setImageResource(R.drawable.ic_9_16_ratio_22_33)
                    reconnectView(
                        constraintLayout = binding.constraintLayout,
                        targetViewGroup = binding.cameraPreview,
                        topView = binding.constraintLayout,
                        topViewSet = ConstraintSet.TOP,
                        bottomView = binding.constraintLayout,
                        bottomViewSet = ConstraintSet.BOTTOM,
                        connectStateValue = ConnenctState.TOP.id
                    )
                }

                SettingRatio.RATIO_9_16.id -> {
                    viewModel.setRatioState(SettingRatio.RATIO_FULL.id)
                    binding.ivRatio.setImageResource(R.drawable.ic_full_ratio_22_33)
                    reconnectView(
                        constraintLayout = binding.constraintLayout,
                        targetViewGroup = binding.cameraPreview,
                        topView = binding.constraintLayout,
                        topViewSet = ConstraintSet.TOP,
                        bottomView = binding.constraintLayout,
                        bottomViewSet = ConstraintSet.BOTTOM,
                        connectStateValue = ConnenctState.TOPBOTTOM.id
                    )
                }

                SettingRatio.RATIO_FULL.id -> {
                    viewModel.setRatioState(SettingRatio.RATIO_1_1.id)
                    binding.ivRatio.setImageResource(R.drawable.ic_1_1_ratio_22_33)
                    reconnectView(
                        constraintLayout = binding.constraintLayout,
                        targetViewGroup = binding.cameraPreview,
                        topView = binding.ivReverse,
                        topViewSet = ConstraintSet.BOTTOM,
                        bottomView = binding.btCapture,
                        bottomViewSet = ConstraintSet.TOP,
                        connectStateValue = ConnenctState.TOPBOTTOM.id
                    )
                }
            }
        }

        binding.btCapture.setOnClickListener {
            takePhoto()
        }
    }

    private fun resizeCameraView() {
        viewModel.ratioState.observe(this) { ratioState ->
            val layoutParams = binding.cameraPreview.layoutParams

            binding.cameraPreview.layoutParams =
                resizeView(layoutParams, ratioState, deviceWidth, deviceHeight)
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

            imageCaptureSetting()

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

    private fun imageCaptureSetting() {
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
                    startActivity(Intent(this@CameraActivity, CameraResultActivity::class.java))
                }
            }
        )
    }
}
