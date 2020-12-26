package dk.nodes.template.domain.interactors.ratedPhotos

import dk.nodes.template.domain.repositories.PhotoRepository
import javax.inject.Inject

class RemoveAllRatedPhotos @Inject constructor(private val photoRepository: PhotoRepository) {

    suspend fun removeAllRatedPhotos() {
        return photoRepository.removeAllSaveModelPhotos()
    }
}