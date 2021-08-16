package dk.nodes.template.presentation.ui.results

import androidx.lifecycle.viewModelScope
import dk.nodes.template.domain.entities.Photo
import dk.nodes.template.domain.interactors.bestCandidatePhotos.RetrieveListOfBestCandidatesInteractor
import dk.nodes.template.domain.interactors.negativePhotos.AddNegativePhotoInteractor
import dk.nodes.template.domain.interactors.negativePhotos.GetAllNegativePhotosInteractor
import dk.nodes.template.domain.interactors.positivePhotos.AddPositivePhotoInteractor
import dk.nodes.template.domain.interactors.positivePhotos.GetAllPositivePhotosInteractor
import dk.nodes.template.domain.interactors.ratedPhotos.GetAllRatedPhotosInteractor
import dk.nodes.template.domain.managers.PrefManager
import dk.nodes.template.presentation.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class ResultsViewModel @Inject constructor(
    private val addPositivePhotoInteractor: AddPositivePhotoInteractor,
    private val addNegativePhotoInteractor: AddNegativePhotoInteractor,
    private val retrieveListOfBestCandidatesInteractor: RetrieveListOfBestCandidatesInteractor,
    private val getAllPositivePhotosInteractor: GetAllPositivePhotosInteractor,
    private val getAllNegativePhotosInteractor: GetAllNegativePhotosInteractor,

    private val prefManager: PrefManager
) : BaseViewModel<ResultsViewState>(ResultsViewState()) {

    init {
        fetchPictures()
    }


    private fun fetchPictures() {
        viewModelScope.launch(Dispatchers.Main) {
            val result = retrieveListOfBestCandidatesInteractor.retrieveBestCandidates()
            val positives = getAllPositivePhotosInteractor.getPhotos()
            val negatives = getAllNegativePhotosInteractor.getAllNegativePhotos()
            var temp: MutableList<Photo> = mutableListOf()
            result.forEach { resultImage ->
                when {
                    positives.contains(resultImage) -> {
                        temp.add(Photo(resultImage.photoPath, 1))
                    }
                    negatives.contains(resultImage) -> {
                        temp.add(Photo(resultImage.photoPath, 2))
                    }
                    else -> {
                        temp.add(resultImage)
                    }
                }
            }
            state = state.copy(list = temp, listNegatives = negatives, listPositives = positives)
        }
    }

    fun addNegativePhoto(photo: Photo) = viewModelScope.launch(Dispatchers.Main) {
        addNegativePhotoInteractor.addNegativePhoto(photo)
    }


    fun addPositivePhoto(photo: Photo) = viewModelScope.launch(Dispatchers.Main) {
        addPositivePhotoInteractor.addPositivePhoto(photo)
    }


    fun addPhotoPathToPrefManager(path: String?) {
        prefManager.setString("PhotoToBeEnlargedInPref", path.toString())

    }
}