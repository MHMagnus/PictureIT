package dk.nodes.template.domain.interactors.labels

import dk.nodes.template.domain.entities.Label
import dk.nodes.template.domain.repositories.PhotoRepository
import javax.inject.Inject

class InsertListOfLabelsInteractor @Inject constructor(private val photoRepository: PhotoRepository) {

   suspend fun storeLabelsList(list: List<Label>) {
        photoRepository.storeLabelList(list)}
}