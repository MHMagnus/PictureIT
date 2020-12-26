package dk.nodes.template.domain.interactors.bestCandidatePhotos


import dk.nodes.template.domain.entities.Photo
import dk.nodes.template.domain.repositories.PhotoRepository
import javax.inject.Inject

class RetrieveListOfBestCandidatesInteractor @Inject constructor(private val photoRepository: PhotoRepository) {

    suspend fun retrieveBestCandidates(): MutableList<Photo> {
        return photoRepository.getAllBestCandidates()
    }

}

