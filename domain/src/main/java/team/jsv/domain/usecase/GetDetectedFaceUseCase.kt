package team.jsv.domain.usecase

import team.jsv.domain.repository.ImageRepository
import java.io.File
import javax.inject.Inject

class GetDetectedFaceUseCase @Inject constructor(
    private val repository: ImageRepository
) {
    suspend operator fun invoke(
        currentTime: String,
        threshold: Float,
        image: File
    ) = runCatching {
        repository.getDetectedFace(
            currentTime = currentTime,
            threshold = threshold,
            image = image
        )
    }
}