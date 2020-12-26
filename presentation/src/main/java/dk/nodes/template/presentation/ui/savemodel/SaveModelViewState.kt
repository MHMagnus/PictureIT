package dk.nodes.template.presentation.ui.savemodel

import dk.nodes.template.domain.entities.Photo
import dk.nodes.template.domain.entities.RatedPhoto
import dk.nodes.template.domain.entities.SavedModelPhoto
import dk.nodes.template.presentation.util.SingleEvent
import dk.nodes.template.presentation.util.ViewError

data class SaveModelViewState(
        val posList: MutableList<Photo> = mutableListOf(),
        val negativeList: MutableList<Photo> = mutableListOf(),
        val listFromRoom: MutableList<SavedModelPhoto> = mutableListOf(),
        val viewError: SingleEvent<ViewError>? = null,
        val isLoading: Boolean = false
)