package dk.nodes.template.domain.interactors.memory



import dk.nodes.template.domain.entities.Photo
import dk.nodes.template.domain.repositories.PhotoRepository
import javax.inject.Inject

class GetMemoryPhotosInteractor @Inject constructor(private val photoRepository: PhotoRepository) {

    //Get all photos with classifier 0

    suspend fun retrieveMemoryPhotos(): MutableList<Photo> {
        return photoRepository.getPhotosForHistory()
    }

}