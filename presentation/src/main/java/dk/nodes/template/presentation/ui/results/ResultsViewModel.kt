package dk.nodes.template.presentation.ui.results

import androidx.lifecycle.viewModelScope
import dk.nodes.template.domain.entities.Photo
import dk.nodes.template.domain.interactors.bestCandidatePhotos.RetrieveListOfBestCandidatesInteractor
import dk.nodes.template.domain.interactors.negativePhotos.AddNegativePhotoInteractor
import dk.nodes.template.domain.interactors.positivePhotos.AddPositivePhotoInteractor
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
        private val prefManager: PrefManager
) : BaseViewModel<ResultsViewState>(ResultsViewState()) {

    init {
        fetchPictures()
    }

    private fun fetchPictures() {
        viewModelScope.launch(Dispatchers.Main) {
            val result = retrieveListOfBestCandidatesInteractor.retrieveBestCandidates()
            state = state.copy(list = result)
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