package team.jsv.icec.util

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import team.jsv.icec.ui.main.mosaic.result.MosaicResultActivity
import team.jsv.presentation.R
import java.io.File
import java.net.URI

/**
 * [Context] 확장 함수 이미지 공유 기능 입니다.
 * @param imagePath 공유할 이미지의 content URI
 * EX) "content://media/external/images/media/1000000150"
 * @return Unit
 */
fun Context.shareImage(
    contentUri: String
) {
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = MosaicResultActivity.SHARE_TYPE

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P &&
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
        ) {
            val fileUri = FileProvider.getUriForFile(
                this@shareImage,
                "${this@shareImage.packageName}.provider",
                File(URI(contentUri).path)
            )
            putExtra(Intent.EXTRA_STREAM, fileUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        } else {
            putExtra(Intent.EXTRA_STREAM, contentUri.toUri())
        }
    }

    this.startActivity(Intent.createChooser(shareIntent, this.getString(R.string.share_text)))
}