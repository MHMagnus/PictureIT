package dk.nodes.template.domain.interactors.photos


import dk.nodes.template.domain.entities.Photo
import dk.nodes.template.domain.repositories.PhotoRepository
import javax.inject.Inject

class AddPhotoInteractor @Inject constructor(
        private val photoRepository: PhotoRepository) {


    suspend fun addPhoto(photo: Photo): Boolean {
        return photoRepository.addPhoto(photo)
    }

}