package dk.nodes.template.presentation.ui.loadmodel

import dk.nodes.template.domain.entities.Photo
import dk.nodes.template.domain.entities.SavedModelPhoto
import dk.nodes.template.presentation.util.SingleEvent
import dk.nodes.template.presentation.util.ViewError

data class LoadModelViewState(
        val posList: MutableList<Photo> = mutableListOf(),
        val negativeList: MutableList<Photo> = mutableListOf(),
        val listOneFromRoom: MutableList<SavedModelPhoto> = mutableListOf(),
        val listTwoFromRoom: MutableList<SavedModelPhoto> = mutableListOf(),
        val listThreeFromRoom: MutableList<SavedModelPhoto> = mutableListOf(),
        val listFourFromRoom: MutableList<SavedModelPhoto> = mutableListOf(),
        val viewError: SingleEvent<ViewError>? = null,
        val isLoading: Boolean = false
)