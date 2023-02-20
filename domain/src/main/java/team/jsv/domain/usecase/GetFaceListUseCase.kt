package team.jsv.domain.usecase

import team.jsv.domain.repository.ImageRepository
import java.io.File
import javax.inject.Inject

class GetFaceListUseCase @Inject constructor(
    private val repository: ImageRepository
) {
    suspend operator fun invoke(
        currentTime: String,
        image: File
    ) = runCatching {
        repository.getFaceList(
            currentTime = currentTime,
            image = image
        )
    }
}