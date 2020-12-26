package dk.nodes.template.domain.interactors.ratedPhotos

import dk.nodes.template.domain.repositories.PhotoRepository
import javax.inject.Inject

class RemoveRatedPhotoInteractor @Inject constructor(
        private val photoRepository: PhotoRepository) {

    suspend fun removePhoto(photoPath: String): Boolean {
        return photoRepository.removeSaveModelPhoto(photoPath)
    }
}

