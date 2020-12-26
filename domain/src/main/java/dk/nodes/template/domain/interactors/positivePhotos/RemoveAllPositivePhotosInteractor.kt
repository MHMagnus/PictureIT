package dk.nodes.template.domain.interactors.positivePhotos

import dk.nodes.template.domain.repositories.PhotoRepository
import javax.inject.Inject

class RemoveAllPositivePhotosInteractor @Inject constructor(private val photoRepository: PhotoRepository) {

    suspend fun removePositivePhotos() {
        return photoRepository.removeAllPositivePhotos()
    }

}
