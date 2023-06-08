package team.jsv.icec.ui.camera

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.util.Log
import android.util.Size
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import team.jsv.icec.base.BaseFragment
import team.jsv.icec.util.ConnenctState
import team.jsv.icec.util.SettingViewUtil
import team.jsv.icec.util.SettingViewUtil.getStatusBarHeightDP
import team.jsv.icec.util.deviceHeight
import team.jsv.icec.util.deviceWidth
import team.jsv.presentation.R
import team.jsv.presentation.databinding.FragmentCameraBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraFragment : BaseFragment<FragmentCameraBinding>(R.layout.fragment_camera) {

    private val viewModel: CameraViewModel by activityViewModels()
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var cameraSelector: CameraSelector
    private var imageCapture: ImageCapture? = null

    override fun onResume() {
        super.onResume()
        initClickEvent()
    }

    override fun initView() {
        setReverseBtMargin()
        observeRatio()
        observeCameraSelector()
    }

    private fun setReverseBtMargin() {
        val layoutParams = binding.ivReverse.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.setMargins(0, getStatusBarHeightDP(requireContext()), 0, 0)
        binding.ivReverse.layoutParams = layoutParams
    }

    private fun observeCameraSelector() {
        viewModel.isFrontCamera.observe(this) {
            cameraSelector = if (it) {
                CameraSelector.DEFAULT_FRONT_CAMERA
            } else {
                CameraSelector.DEFAULT_BACK_CAMERA
            }
        }
    }

    private fun initClickEvent() {
        binding.ivReverse.setOnClickListener {
            if (viewModel.isFrontCamera.value == true) {
                cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                viewModel.setIsFrontCamera(false)
            } else {
                cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
                viewModel.setIsFrontCamera(true)
            }

            startCamera()
        }

        binding.ivClose.setOnClickListener {
            popBackStack()
        }

        binding.ivRatio.setOnClickListener {
            when (viewModel.ratioState.value) {
                SettingRatio.RATIO_1_1.id -> {
                    viewModel.setRatioState(SettingRatio.RATIO_3_4.id)
                }

                SettingRatio.RATIO_3_4.id -> {
                    viewModel.setRatioState(SettingRatio.RATIO_9_16.id)
                }

                SettingRatio.RATIO_9_16.id -> {
                    viewModel.setRatioState(SettingRatio.RATIO_FULL.id)
                }

                SettingRatio.RATIO_FULL.id -> {
                    viewModel.setRatioState(SettingRatio.RATIO_1_1.id)
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

            binding.cameraPreview.layoutParams = SettingViewUtil.resizeView(
                layoutParams,
                ratioState,
                this.deviceWidth,
                this.deviceHeight
            )

            startCamera()

            when (ratioState) {
                SettingRatio.RATIO_1_1.id -> {
                    binding.ivRatio.setImageResource(R.drawable.ic_1_1_ratio_22_33)
                    SettingViewUtil.reconnectView(
                        constraintLayout = binding.constraintLayout,
                        targetView = binding.cameraPreview,
                        topView = binding.ivReverse,
                        topViewSet = ConstraintSet.BOTTOM,
                        bottomView = binding.btCapture,
                        bottomViewSet = ConstraintSet.TOP,
                        connectStateValue = ConnenctState.TOPBOTTOM.id
                    )
                }

                SettingRatio.RATIO_3_4.id -> {
                    binding.ivRatio.setImageResource(R.drawable.ic_3_4_ratio_22_33)
                }

                SettingRatio.RATIO_9_16.id -> {
                    binding.ivRatio.setImageResource(R.drawable.ic_9_16_ratio_22_33)
                    SettingViewUtil.reconnectView(
                        constraintLayout = binding.constraintLayout,
                        targetView = binding.cameraPreview,
                        topView = binding.constraintLayout,
                        topViewSet = ConstraintSet.TOP,
                        bottomView = binding.constraintLayout,
                        bottomViewSet = ConstraintSet.BOTTOM,
                        connectStateValue = ConnenctState.BOTTOM.id
                    )
                }

                SettingRatio.RATIO_FULL.id -> {
                    binding.ivRatio.setImageResource(R.drawable.ic_full_ratio_22_33)
                    SettingViewUtil.reconnectView(
                        constraintLayout = binding.constraintLayout,
                        targetView = binding.cameraPreview,
                        topView = binding.constraintLayout,
                        topViewSet = ConstraintSet.TOP,
                        bottomView = binding.constraintLayout,
                        bottomViewSet = ConstraintSet.BOTTOM,
                        connectStateValue = ConnenctState.TOPBOTTOM.id
                    )
                }
            }
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.cameraPreview.surfaceProvider)
            }

            imageCaptureSetting()

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    viewLifecycleOwner, cameraSelector, preview, imageCapture
                )
            } catch (e: Exception) {
                Log.e("cameraError", "Use case binding failed ", e)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        imageCapture.takePicture(ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(imageProxy: ImageProxy) {
                    val buffer = imageProxy.planes[0].buffer
                    val bytes = ByteArray(buffer.capacity())
                    buffer.get(bytes)

                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    val rotatedBitmap = rotateBitmap(bitmap, imageProxy)
                    imageProxy.close()

                    viewModel.setBitmapImage(rotatedBitmap)

                    navController.navigate(R.id.action_cameraFragment_to_cameraResultFragment)
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e("사진저장실패", "Photo capture failed: ${exception.message}", exception)
                }
            })
    }

    private fun rotateBitmap(bitmap: Bitmap, imageProxy: ImageProxy): Bitmap {
        val rotationDegrees = imageProxy.imageInfo.rotationDegrees

        val matrix = Matrix()

        if (viewModel.isFrontCamera.value == true) {
            matrix.preScale(1f, -1f)
        }

        matrix.postRotate(rotationDegrees.toFloat())

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    private fun imageCaptureSetting() {
        imageCapture = ImageCapture.Builder().apply {
            setTargetResolution(
                Size(
                    binding.cameraPreview.width, binding.cameraPreview.height
                )
            )
        }.build()
    }
}