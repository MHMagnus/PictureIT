package dk.nodes.template.domain.interactors.labels

import dk.nodes.template.domain.entities.Label
import dk.nodes.template.domain.repositories.PhotoRepository
import javax.inject.Inject

  class GetLabelFromIdInteractor @Inject constructor(private val photoRepository: PhotoRepository) {

    suspend fun getPhotos(labelId: Int): Label? {
        return photoRepository.getLabel(labelId)
    }
}