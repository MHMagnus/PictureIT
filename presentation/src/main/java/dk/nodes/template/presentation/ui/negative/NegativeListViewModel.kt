package dk.nodes.template.presentation.ui.negative

import androidx.lifecycle.viewModelScope
import dk.nodes.template.domain.entities.Photo
import dk.nodes.template.domain.interactors.negativePhotos.GetAllNegativePhotosInteractor
import dk.nodes.template.domain.interactors.negativePhotos.RemoveNegativePhotoInteractor
import dk.nodes.template.domain.interactors.photos.AddPhotoInteractor
import dk.nodes.template.domain.managers.PrefManager
import dk.nodes.template.presentation.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class NegativeListViewModel @Inject constructor(
        private val getAllNegativePhotosInteractor: GetAllNegativePhotosInteractor,
        private val removeNegativePhotoInteractor: RemoveNegativePhotoInteractor,
        private val addPhotoInteractor: AddPhotoInteractor,
        private val prefManager: PrefManager
) : BaseViewModel<NegativeListViewState>(NegativeListViewState()) {

    init {
        fetchPictures()
    }

    fun fetchPictures() {
        viewModelScope.launch(Dispatchers.Main) {
            val result = getAllNegativePhotosInteractor.getAllNegativePhotos()
            state = state.copy(list = result)
        }
    }

    fun removeNegativePhoto(imagePath: String)= viewModelScope.launch(Dispatchers.Main) {
        addPhotoInteractor.addPhoto(Photo(imagePath,77))
        removeNegativePhotoInteractor.removeNegativePhoto(imagePath)

    }

    fun getListOfNegativePhotos(): MutableList<Photo> {
        return state.list
    }

    fun addPhotoPathToPrefManager(path: String?) {
        prefManager.setString("PhotoToBeEnlargedInPref",path.toString())

    }

}