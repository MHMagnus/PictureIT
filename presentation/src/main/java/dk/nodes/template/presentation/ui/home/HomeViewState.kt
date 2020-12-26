package dk.nodes.template.presentation.ui.home

import dk.nodes.template.domain.entities.Photo
import dk.nodes.template.presentation.util.SingleEvent
import dk.nodes.template.presentation.util.ViewError

data class HomeViewState(
        var list: MutableList<Photo> = mutableListOf(),
        var oldList: MutableList<Photo> = mutableListOf(),
        var allImagesInRoom: MutableList<Photo> = mutableListOf()
        )