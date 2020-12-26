package dk.nodes.template.domain.interactors.bestCandidatePhotos


import dk.nodes.template.domain.entities.Photo
import dk.nodes.template.domain.repositories.PhotoRepository
import javax.inject.Inject

class SaveListOfPhotosInteractor @Inject constructor(private val photoRepository: PhotoRepository) {
    
    suspend fun saveListOfPhotos(list: List<Photo>) {
        photoRepository.storePhotosList(list)
    }

}

