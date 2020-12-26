package dk.nodes.template.presentation.ui.positive

import dk.nodes.template.domain.entities.Photo
import dk.nodes.template.presentation.util.SingleEvent
import dk.nodes.template.presentation.util.ViewError

data class PositiveListViewState(
        val list: MutableList<Photo> = mutableListOf(),
        val viewError: SingleEvent<ViewError>? = null,
        val isLoading: Boolean = false
)