package dk.nodes.template.presentation.ui.info

import dk.nodes.template.domain.entities.InfoItem

data class InfoActivityViewState(
        val isLoading: Boolean = false,
        val data: InfoItem? = null
)