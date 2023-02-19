@file:SuppressLint("ResourceType")

package team.jsv.icec

import android.Manifest
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
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.AspectRatio.RATIO_4_3
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import team.jsv.presentation.databinding.ActivityMainBinding
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
class MainActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityMainBinding
    private lateinit var cameraExecutor: ExecutorService
    private var imageCapture: ImageCapture? = null
    private var cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

    //    private var quickAction: QuickAction? = null
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        startCameraWithPermission()
        getLastImageFromGallery()
        //settingQuickAction()
    }

    override fun onResume() {
        super.onResume()
        initClickListener()
        resizeCameraView(RatioId.FourOnThree.id)
    }

    private fun initClickListener() {
        viewBinding.imageviewOneOnOne.setOnClickListener {
            resizeCameraView(RatioId.OneOnOne.id)
        }

        viewBinding.imageviewThreeOnFour.setOnClickListener {
            resizeCameraView(RatioId.FourOnThree.id)
        }

        viewBinding.imageviewNineOnSixteen.setOnClickListener {
            resizeCameraView(RatioId.NineOnSixteen.id)
        }

//        viewBinding.ratioButton.setOnClickListener { view ->
//            quickAction!!.show(view)
//        }

        viewBinding.buttonCapture.setOnClickListener {
            // 사진 찍기 버튼
            takePhoto()
        }

        viewBinding.buttonReverse.setOnClickListener {
            // 좌우 반전 버튼
            if (cameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA) {
                cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                startCamera()
            } else {
                cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
                startCamera()
            }
        }

        viewBinding.imageviewGallery.setOnClickListener {
            openGallery()
        }
    }

//    private fun settingQuickAction() {
//        QuickAction.setDefaultColor(Color.GRAY)
//        QuickAction.setDefaultTextColor(Color.BLACK)
//
//        val oneOnOneItem = ActionItem(Ratio.OneOnOne.id, "", R.drawable.one_on_one)
//        val fourOnThreeItem = ActionItem(Ratio.FourOnThree.id, "", R.drawable.four_on_three)
//        val nineOnSixteenItem = ActionItem(Ratio.NineOnSixteen.id, "", R.drawable.nine_on_sixteen)
//
//        //setSticky(true)를 사용하여 항목을 클릭한 후 QuickAction 대화 상자가 닫히지 않도록 합니다.
//        oneOnOneItem.isSticky = true
//        fourOnThreeItem.isSticky = true
//        nineOnSixteenItem.isSticky = true
//
//        // 가로 세로 정하기
//        quickAction = QuickAction(this, QuickAction.HORIZONTAL)
//
//        quickAction!!.setColorRes(R.color.MainColor)
//        quickAction!!.setTextColorRes(R.color.white)
//
//        quickAction!!.addActionItem(oneOnOneItem, fourOnThreeItem, nineOnSixteenItem)
//
//
//        quickAction!!.setOnActionItemClickListener { item -> //here we can filter which action item was clicked with pos or actionId parameter
//            val id = item.actionId
//
//            when(id) {
//                Ratio.OneOnOne.id -> {
//                    resizeCameraView(Ratio.OneOnOne.id)
//                }
//                Ratio.FourOnThree.id -> {
//                    resizeCameraView(Ratio.FourOnThree.id)
//                }
//                Ratio.NineOnSixteen.id -> {
//                    resizeCameraView(Ratio.NineOnSixteen.id)
//                }
//            }
//
//            if (!item.isSticky) quickAction!!.remove(item)
//        }
//    }

    private fun resizeCameraView(id: Int) {
        val display = viewBinding.root.resources?.displayMetrics
        val deviceWidth = display?.widthPixels

        val layoutParams = viewBinding.cameraPreview.layoutParams

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

        viewBinding.cameraPreview.layoutParams = layoutParams
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

        Log.i("Cursor Last", cursor.moveToLast().toString())
        if (cursor.moveToFirst()) {
            val columnIndexID = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val imageId: Long = cursor.getLong(columnIndexID)
            val imageURI = Uri.withAppendedPath(uriExternal, "" + imageId)

            Glide.with(this)
                .load(imageURI)
                .into(viewBinding.imageviewGallery)
        }
        cursor.close()
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
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
                    it.setSurfaceProvider(viewBinding.cameraPreview.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder().apply {
                setTargetAspectRatio(RATIO_4_3)
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
                    val msg = "Photo capture succeeded: ${output.savedUri}"
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
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
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
        private val REQUIRED_PERMISSIONS =
            mutableListOf(
                Manifest.permission.CAMERA
            ).apply {
                add(getImageStoragePermission())
            }.toTypedArray()

        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    }
}

fun getImageStoragePermission(): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_IMAGES
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }
}