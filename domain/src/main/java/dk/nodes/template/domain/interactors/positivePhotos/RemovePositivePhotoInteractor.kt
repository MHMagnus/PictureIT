package dk.nodes.template.domain.interactors.positivePhotos


import dk.nodes.template.domain.repositories.PhotoRepository
import javax.inject.Inject

class RemovePositivePhotoInteractor @Inject constructor(
        private val photoRepository: PhotoRepository) {

    suspend fun removePositivePhoto(photoPath: String?): Boolean {
        return photoRepository.removePositivePhoto(photoPath)


    }
}