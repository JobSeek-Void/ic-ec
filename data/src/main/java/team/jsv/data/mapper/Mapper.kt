package team.jsv.data.mapper

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import team.jsv.data.dto.response.BlurResponse
import team.jsv.data.dto.response.FaceInfo
import team.jsv.data.dto.response.FaceResponse
import team.jsv.data.dto.response.MosaicResponse
import team.jsv.domain.model.DetectFaceInfo
import team.jsv.domain.model.Face
import team.jsv.domain.model.MosaicImage
import java.io.File

internal fun FaceResponse.toDomain() =
    Face(
        originalImage = originalImage,
        faceList = faceList.map { it.toDomainModel() }
    )

internal fun FaceInfo.toDomainModel() =
    DetectFaceInfo(
        url = url,
        coordinates = coordinates
    )

internal fun File.toMultipartImage() =
    MultipartBody.Part.createFormData(
        name = "image",
        filename = this.name,
        body = this.asRequestBody("image/*".toMediaTypeOrNull())
    )

internal fun MosaicResponse.toDomain() =
    MosaicImage(
        mosaicImage = mosaicImage
    )

internal fun BlurResponse.toDomain() =
    MosaicImage(
        mosaicImage = blurImage
    )