package team.jsv.icec.util

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

object PermissionUtil {
    /**
     * ICEC에서 사용하는 권한을 요청하는 함수입니다.
     * return mutalbeListof(사용하려는 권한들)...
     * */
    fun getPermissions(): List<String> {
        return mutableListOf(
            Manifest.permission.CAMERA
        ).apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                add(Manifest.permission.READ_MEDIA_IMAGES)
            } else {
                add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }
    }
}

inline fun AppCompatActivity.requestPermissions(
    permission: List<String>,
    crossinline onPermissionAccepted: () -> Unit,
    crossinline onPermissionDenied: () -> Unit
) {
    val requestPermissions = permission.filter {
        ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
    }
    if(requestPermissions.isNotEmpty()) {
        this.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            if (result.values.all { it }) {
                onPermissionAccepted()
            } else {
                onPermissionDenied()
            }
        }.launch(requestPermissions.toTypedArray())
    }
}