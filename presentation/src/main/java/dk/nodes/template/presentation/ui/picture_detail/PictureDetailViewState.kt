package dk.nodes.template.presentation.ui.picture_detail

import dk.nodes.template.domain.entities.Photo
import dk.nodes.template.presentation.util.SingleEvent
import dk.nodes.template.presentation.util.ViewError

data class PictureDetailViewState(
        val photo: Photo? = null,
        var headerLabel: String = "",
        var listOfConfidences: MutableList<String> = mutableListOf(),
        var concatinationOfLabels: MutableList<String> = mutableListOf(),
        val viewError: SingleEvent<ViewError>? = null,
        val isLoading: Boolean = false
)