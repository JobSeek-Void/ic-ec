@file:SuppressLint("ResourceType")
package team.jsv.icec

import android.Manifest
import android.R
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.Images.Media.getBitmap
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
import team.jsv.presentation.databinding.ActivityMainBinding
import java.io.FileNotFoundException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class MainActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityMainBinding
    private lateinit var cameraExecutor: ExecutorService
    private var imageCapture: ImageCapture? = null
    private var cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

    private val ID_UP = 1
    private val ID_DOWN = 2

    private var quickAction: QuickAction? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions (
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }
        //init()

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
            getLastImageFromGallery()
        }

        //기본 색상 구성
        QuickAction.setDefaultColor(Color.GRAY)
        QuickAction.setDefaultTextColor(Color.BLACK)

        val nextItem = ActionItem(ID_DOWN, "Next", R.drawable.arrow_down_float)
        val prevItem = ActionItem(ID_UP, "Prev", R.drawable.arrow_up_float)

        //setSticky(true)를 사용하여 항목을 클릭한 후 QuickAction 대화 상자가 닫히지 않도록 합니다.
        prevItem.isSticky = true
        nextItem.isSticky = true

        // 가로 세로 정하기
        quickAction = QuickAction(this, QuickAction.HORIZONTAL)

        quickAction!!.setColorRes(R.color.holo_purple)
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

        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.")
        sendIntent.type = "text/plain"

        viewBinding.ratioButton.setOnClickListener { view ->
            quickAction!!.show(view)
        }

        cameraExecutor = Executors.newSingleThreadExecutor()
    }


    fun init() {
        viewBinding.ratioButton.setOnClickListener {
            // 비율 옵션 버튼
        }

        viewBinding.galleryButton.setOnClickListener {
            // 갤러리 버튼
        }

        viewBinding.captureButton.setOnClickListener {
            // 사진 찍기 버튼
            takePhoto()
        }

        viewBinding.reverseButton.setOnClickListener {
            // 좌우 반전 버튼
            if (cameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA) {
                cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            } else {
                cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
            }
        }
    }

    private fun getLastImageFromGallery(){
        val uriExternal: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(
            MediaStore.Images.ImageColumns._ID,
            MediaStore.Images.Media._ID,
            MediaStore.Images.ImageColumns.DATE_ADDED,
            MediaStore.Images.ImageColumns.MIME_TYPE
        )
        val cursor: Cursor = this.contentResolver.query(uriExternal, projection, null,
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
                .transform(CenterCrop(), RoundedCorners(90))
                .into(viewBinding.galleryButton)
        }
        cursor.close()
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
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
                    this, cameraSelector, preview, imageCapture)
            } catch (e: Exception) {
                Log.e("cameraError", "Use case binding failed ", e)
            }
        }, ContextCompat.getMainExecutor(this))
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
            .Builder(contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues)
            .build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e("captureError", "Photo capture failed: ${exc.message}", exc)
                }

                override fun
                        onImageSaved(output: ImageCapture.OutputFileResults){
                    val msg = "Photo capture succeeded: ${output.savedUri}"
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS =
            mutableListOf (
                Manifest.permission.CAMERA
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()

        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    }
}