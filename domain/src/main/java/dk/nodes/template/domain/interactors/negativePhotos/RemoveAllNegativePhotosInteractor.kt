package dk.nodes.template.domain.interactors.negativePhotos

import dk.nodes.template.domain.repositories.PhotoRepository
import javax.inject.Inject

class RemoveAllNegativePhotosInteractor @Inject constructor(private val photoRepository: PhotoRepository) {

    suspend fun removeNegativePhotos() {
        return photoRepository.removeAllNegativePhotos()
    }

}
