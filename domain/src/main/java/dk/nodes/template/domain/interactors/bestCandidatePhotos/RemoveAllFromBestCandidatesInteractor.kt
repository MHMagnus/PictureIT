package dk.nodes.template.domain.interactors.bestCandidatePhotos

import dk.nodes.template.domain.repositories.PhotoRepository
import javax.inject.Inject

class RemoveAllFromBestCandidatesInteractor @Inject constructor(private val photoRepository: PhotoRepository) {

    suspend fun removeBestCandidates() {
        return photoRepository.removeAllBestCandidates()
    }

}
