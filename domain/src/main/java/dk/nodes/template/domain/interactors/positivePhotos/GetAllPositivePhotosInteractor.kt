package dk.nodes.template.domain.interactors.positivePhotos

import dk.nodes.template.domain.entities.Photo
import dk.nodes.template.domain.repositories.PhotoRepository

import javax.inject.Inject

class GetAllPositivePhotosInteractor @Inject constructor(
        private val photoRepository: PhotoRepository) {

    suspend fun getPhotos(): MutableList<Photo> {
        return photoRepository.getAllPositivePhotos()

    }
}