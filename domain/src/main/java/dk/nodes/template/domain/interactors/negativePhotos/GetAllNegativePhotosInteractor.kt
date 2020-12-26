package dk.nodes.template.domain.interactors.negativePhotos


import dk.nodes.template.domain.entities.Photo
import dk.nodes.template.domain.repositories.PhotoRepository
import javax.inject.Inject

class GetAllNegativePhotosInteractor @Inject constructor(
        private val photoRepository: PhotoRepository) {

    suspend fun getAllNegativePhotos(): MutableList<Photo> {
        return photoRepository.getAllNegativePhotos()

    }
}