package dk.nodes.template.domain.interactors.ratedPhotos

import dk.nodes.template.domain.entities.RatedPhoto
import dk.nodes.template.domain.entities.SavedModelPhoto
import dk.nodes.template.domain.repositories.PhotoRepository
import javax.inject.Inject

class GetAllRatedPhotosInteractor @Inject constructor(private val photoRepository: PhotoRepository) {


    suspend fun retrieveSaveModelPhotos(): MutableList<SavedModelPhoto> {
        return photoRepository.getSaveModelPhotos()
    }
}