package dk.nodes.template.domain.interactors.photos

import dk.nodes.template.domain.repositories.PhotoRepository
import javax.inject.Inject

class RemoveAllPhotosInteractor @Inject constructor(private val photoRepository: PhotoRepository) {

    suspend fun removeAllPhotos() {
        return photoRepository.removeAll()
    }
}