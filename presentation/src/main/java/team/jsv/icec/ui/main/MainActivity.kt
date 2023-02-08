@file:SuppressLint("ResourceType")

package team.jsv.icec

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import me.piruin.quickaction.ActionItem
import me.piruin.quickaction.QuickAction
import team.jsv.presentation.R
import team.jsv.presentation.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/*
* 함수화
* 1. 재사용 목적
* 2. 가독성 목적, 분리를 위함
* DRY 원칙, Don't Repeat Yourself 절대 반복하지마라
* */

enum class Ratio(val id: Int) {
    OneOnOne(1),
    FourOnThree(2),
    SixteenOnNine(3)
}

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class MainActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityMainBinding
    private lateinit var cameraExecutor: ExecutorService
    private var imageCapture: ImageCapture? = null
    private var cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

    private var quickAction: QuickAction? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        getLastImageFromGallery()
        startCameraWithPermission()
        settingQuickAction()
    }

    override fun onResume() {
        super.onResume()
        initClickListener()
    }

    private fun initClickListener() {
        viewBinding.ratioButton.setOnClickListener { view ->
            quickAction!!.show(view)
        }

        viewBinding.captureButton.setOnClickListener {
            // 사진 찍기 버튼
            takePhoto()
        }

        viewBinding.reverseButton.setOnClickListener {
            // 좌우 반전 버튼
            if (cameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA) {
                cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                startCamera()
            } else {
                cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
                startCamera()
            }
        }

        viewBinding.galleryButton.setOnClickListener {

        }
    }

    private fun settingQuickAction() {
        QuickAction.setDefaultColor(Color.GRAY)
        QuickAction.setDefaultTextColor(Color.BLACK)

        val nextItem = ActionItem(Ratio.OneOnOne.id, "Next", R.drawable.ratiobutton)
        val prevItem = ActionItem(Ratio.FourOnThree.id, "Prev", R.drawable.logo_wip)

        //setSticky(true)를 사용하여 항목을 클릭한 후 QuickAction 대화 상자가 닫히지 않도록 합니다.
        prevItem.isSticky = true
        nextItem.isSticky = true

        // 가로 세로 정하기
        quickAction = QuickAction(this, QuickAction.HORIZONTAL)

        quickAction!!.setColorRes(R.color.MainColor)
        quickAction!!.setTextColorRes(R.color.white)

        quickAction!!.addActionItem(nextItem, prevItem)

        quickAction!!.setOnActionItemClickListener { item -> //here we can filter which action item was clicked with pos or actionId parameter
            val title = item.title

            if (title == "Next") {
                val layoutParams = viewBinding.cameraPreview.layoutParams
                layoutParams.height = 1500
                viewBinding.cameraPreview.layoutParams = layoutParams
            } else {
                val layoutParams = viewBinding.cameraPreview.layoutParams
                layoutParams.height = 800
                viewBinding.cameraPreview.layoutParams = layoutParams
            }

            if (!item.isSticky) quickAction!!.remove(item)
        }

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

//            try {
//                val bitmap = getBitmap(contentResolver, imageURI)
//                viewBinding.galleryButton.setImageBitmap(bitmap)
//            } catch (e: FileNotFoundException) {
//                e.printStackTrace()
//            } catch (e: IOException) {
//                e.printStackTrace()
//            }

            Glide.with(this)
                .load(imageURI)
                .into(viewBinding.galleryButton)
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

            imageCapture = ImageCapture.Builder().build()

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

    private fun startCameraWithPermission() {
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }
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

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS =
            mutableListOf(
                Manifest.permission.CAMERA
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()

        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    }
}