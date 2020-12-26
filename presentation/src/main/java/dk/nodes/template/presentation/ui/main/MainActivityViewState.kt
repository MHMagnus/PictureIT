package dk.nodes.template.presentation.ui.main


import dk.nodes.template.domain.entities.Photo
import dk.nodes.template.presentation.util.SingleEvent

data class MainActivityViewState(
        var list: MutableList<Photo> = mutableListOf(),
        val listOfConfidences: MutableList<FloatArray> = mutableListOf(),
        val listOfLabels: MutableList<IntArray> = mutableListOf(),
        val listOfRatings: MutableList<Int> = mutableListOf(),
        val listOfBestCandidates: MutableList<String?> = mutableListOf(),
        val errorMessage: SingleEvent<String>? = null,
        val isLoading: Boolean = false
)