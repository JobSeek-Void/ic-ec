@file:SuppressLint("ResourceType")

package team.jsv.icec.ui.camera

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.util.Size
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
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
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.roundToInt

class CameraActivity :
    BaseActivity<ActivityCameraBinding>(R.layout.activity_camera) {
    private val deviceHeight get() = screenHeight(this)
    private val deviceWidth get() = screenWidth(this)

    private lateinit var cameraExecutor: ExecutorService
    private lateinit var cameraSelector: CameraSelector
    private var imageCapture: ImageCapture? = null
    private val viewModel: CameraViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermissions(PermissionUtil.getPermissions())

        setReverseBtMargin()
        cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

        viewModel.initSetRatio()
        observeRatio()
    }

    override fun onResume() {
        super.onResume()
        initClickListener()
    }

    private fun getStatusBarHeightDP(context: Context) : Int {
        var result = 0

        val resourceId: Int =
            context.resources.getIdentifier("status_bar_height", "dimen", "android")

        if (resourceId > 0) {
            result = context.resources.getDimension(resourceId).toInt()
        }

        return result
    }

    private fun setReverseBtMargin() {
        val layoutParams = binding.ivReverse.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.setMargins(0, getStatusBarHeightDP(this), 0, 0)
        binding.ivReverse.layoutParams = layoutParams
    }

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

    private fun observeRatio() {
        viewModel.ratioState.observe(this) { ratioState ->
            val layoutParams = binding.cameraPreview.layoutParams

            binding.cameraPreview.layoutParams =
                resizeView(layoutParams, ratioState, deviceWidth, deviceHeight).apply {
                    startCamera()
                }
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

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        imageCapture.takePicture(
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(imageProxy: ImageProxy) {
                    val buffer = imageProxy.planes[0].buffer
                    val bytes = ByteArray(buffer.capacity())
                    buffer.get(bytes)
                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    imageProxy.close()
                    //Todo(jiiiiiyoon): viewModel에 저장
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e("사진저장실패", "Photo capture failed: ${exception.message}", exception)
                }
            })
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
}
