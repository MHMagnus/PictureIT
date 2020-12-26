package dk.nodes.template.domain.interactors.ratedPhotos

import dk.nodes.template.domain.entities.RatedPhoto
import dk.nodes.template.domain.entities.SavedModelPhoto
import dk.nodes.template.domain.repositories.PhotoRepository
import javax.inject.Inject

class AddRatedPhotoInteractor @Inject constructor(
        private val photoRepository: PhotoRepository) {


    suspend fun addPhoto(photo: SavedModelPhoto): Boolean {
        return photoRepository.addSaveModelPhoto(photo)
    }

}