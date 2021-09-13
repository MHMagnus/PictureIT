package dk.nodes.template.presentation.ui.results

import dk.nodes.template.domain.entities.Photo
import dk.nodes.template.presentation.util.SingleEvent
import dk.nodes.template.presentation.util.ViewError

data class ResultsViewState(
        val list: MutableList<Photo> = mutableListOf(),
        val listPositives: MutableList<Photo> = mutableListOf(),
        val listNegatives: MutableList<Photo> = mutableListOf(),
        val viewError: SingleEvent<ViewError>? = null,
        val isLoading: Boolean = false
)