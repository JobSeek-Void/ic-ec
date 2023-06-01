package team.jsv.icec.util

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import java.io.File
import java.io.FileOutputStream

private const val SaveImageMimeType = "image/png"
private const val FileExtensionName = ".png"
private const val CompressQuality = 100

internal fun Context.saveImage(bitmap: Bitmap) {
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
        saveImageOnAboveAndroidQ(bitmap)
    } else {
        saveImageOnUnderAndroidQ(bitmap)
    }
}

private fun Context.saveImageOnAboveAndroidQ(bitmap: Bitmap) {
    val fileName = System.currentTimeMillis().toString() + FileExtensionName

    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
        put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
        put(MediaStore.Images.Media.MIME_TYPE, SaveImageMimeType)
        put(MediaStore.Images.Media.IS_PENDING, 1)
    }

    val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

    contentResolver.openFileDescriptor(uri!!, "w", null).use { image ->
        image?.let {
            val fos = FileOutputStream(it.fileDescriptor)
            bitmap.compress(Bitmap.CompressFormat.PNG, CompressQuality, fos)
            fos.close()
            contentValues.clear()
            contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
            contentResolver.update(uri, contentValues, null, null)
        }
    }
}

private fun Context.saveImageOnUnderAndroidQ(bitmap: Bitmap) {
    val fileName = System.currentTimeMillis().toString() + FileExtensionName
    val externalStorage = Environment.getExternalStorageDirectory().absolutePath
    val path = "${externalStorage}/${Environment.DIRECTORY_PICTURES}"
    val dir = File(path)

    if (dir.exists().not()) {
        dir.mkdirs() // 폴더 없을경우 폴더 생성
    }
    val fileItem = File("$dir/$fileName").apply {
        createNewFile()
    }
    FileOutputStream(fileItem).use { fos ->
        bitmap.compress(Bitmap.CompressFormat.PNG, CompressQuality, fos)
        fos.close()
        sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(fileItem)))
    }
}