package dk.nodes.template.domain.interactors.ratedPhotos

import dk.nodes.template.domain.entities.RatedPhoto
import dk.nodes.template.domain.entities.SavedModelPhoto
import dk.nodes.template.domain.repositories.PhotoRepository
import javax.inject.Inject

class InsertListOfRatedPhotosInteractor@Inject constructor(private val photoRepository: PhotoRepository) {

    suspend fun storeRatedPhotos(list: List<SavedModelPhoto>) {
        photoRepository.storeSaveModelPhotosList(list)
    }

}