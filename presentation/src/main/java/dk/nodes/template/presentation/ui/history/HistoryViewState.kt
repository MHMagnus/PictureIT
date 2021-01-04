package dk.nodes.template.presentation.ui.history

import dk.nodes.template.domain.entities.Photo
import dk.nodes.template.presentation.util.SingleEvent
import dk.nodes.template.presentation.util.ViewError

data class HistoryViewState(
        var list: MutableList<Photo> = mutableListOf(),
        var listOfImagesOnDevice: MutableList<String> = mutableListOf(),
        val viewError: SingleEvent<ViewError>? = null,
        val isLoading: Boolean = false
)