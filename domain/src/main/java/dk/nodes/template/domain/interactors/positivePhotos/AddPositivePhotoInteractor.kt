package dk.nodes.template.domain.interactors.positivePhotos


import dk.nodes.template.domain.entities.Photo
import dk.nodes.template.domain.repositories.PhotoRepository
import javax.inject.Inject

class AddPositivePhotoInteractor @Inject constructor(
        private val photoRepository: PhotoRepository) {


    suspend fun addPositivePhoto(photo: Photo): Boolean {
        return photoRepository.addToPositivePhotos(photo)
    }

}