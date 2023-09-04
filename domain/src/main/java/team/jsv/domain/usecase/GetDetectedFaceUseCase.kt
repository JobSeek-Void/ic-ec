package team.jsv.domain.usecase

import team.jsv.domain.repository.ImageRepository
import java.io.File
import java.util.UUID
import javax.inject.Inject

class GetDetectedFaceUseCase @Inject constructor(
    private val repository: ImageRepository
) {
    suspend operator fun invoke(
        threshold: Float,
        image: File
    ) = runCatching {
        repository.getDetectedFace(
            randomSeed = UUID.randomUUID().toString(),
            threshold = threshold,
            image = image
        )
    }
}