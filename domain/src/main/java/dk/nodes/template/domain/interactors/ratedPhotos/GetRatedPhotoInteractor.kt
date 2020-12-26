package dk.nodes.template.domain.interactors.ratedPhotos

import dk.nodes.template.domain.entities.SavedModelPhoto
import dk.nodes.template.domain.repositories.PhotoRepository
import javax.inject.Inject

class GetRatedPhotoInteractor @Inject constructor(
        private val photoRepository: PhotoRepository) {

    suspend fun getPhotos(photoPath: String): SavedModelPhoto? {
        return photoRepository.getSaveModelPhoto(photoPath)

    }
}