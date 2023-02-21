package team.jsv.data.mapper

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import team.jsv.data.dto.response.FaceResponse
import team.jsv.domain.model.Face
import java.io.File

internal fun FaceResponse.toDomain() = this.faceList.map {
    Face(it)
}

internal fun File.toMultipartImage() =
    MultipartBody.Part.createFormData(
        name = "image",
        filename = this.name,
        body = this.asRequestBody("image/*".toMediaTypeOrNull())
    )
