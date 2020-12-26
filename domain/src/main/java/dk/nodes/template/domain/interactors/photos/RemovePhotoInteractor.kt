package dk.nodes.template.domain.interactors.photos


import dk.nodes.template.domain.repositories.PhotoRepository
import javax.inject.Inject

class RemovePhotoInteractor @Inject constructor(
        private val photoRepository: PhotoRepository) {

    suspend fun removePhoto(photoPath: String?): Boolean {
        return photoRepository.removePositivePhoto(photoPath)


    }
}