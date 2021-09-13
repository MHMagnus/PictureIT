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
import kotlinx.coroutines.withContext
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
            withContext(Dispatchers.Main) {
                val result = retrieveListOfBestCandidatesInteractor.retrieveBestCandidates()
                val positives = getAllPositivePhotosInteractor.getPhotos()
                val negatives = getAllNegativePhotosInteractor.getAllNegativePhotos()
                println("this is result: $result")
                println("this is positives: $positives ")
                println("this is negatives: $negatives")
                    state = state.copy(list = result, listNegatives = negatives, listPositives = positives)
                    println("this is state.list in VM: ${state.list}")
                }
        }
    }

    fun stateDotList(): MutableList<Photo> {
        val list:  MutableList<Photo> = mutableListOf()

        state.list.forEach { normalPhoto ->
            state.listPositives.find { positivePhoto -> positivePhoto.photoPath == normalPhoto.photoPath }?.let { list.add(Photo(normalPhoto.photoPath,1)) }
            state.listNegatives.find { negativePhoto -> negativePhoto.photoPath == normalPhoto.photoPath }?.let { list.add(Photo(normalPhoto.photoPath, 2)) }
            state.listPositives.none { positivePhoto -> positivePhoto.photoPath == normalPhoto.photoPath }?.let { list.add(Photo(normalPhoto.photoPath,9)) }
            state.listNegatives.none { negativePhoto -> negativePhoto.photoPath == normalPhoto.photoPath }?.let { list.add(Photo(normalPhoto.photoPath, 9)) }

        }
        println("this is stateDotList: ${list}")
        return list
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