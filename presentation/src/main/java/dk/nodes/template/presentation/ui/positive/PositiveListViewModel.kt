package dk.nodes.template.presentation.ui.positive

import androidx.lifecycle.viewModelScope
import dk.nodes.template.domain.entities.Photo
import dk.nodes.template.domain.interactors.photos.AddPhotoInteractor
import dk.nodes.template.domain.interactors.positivePhotos.GetAllPositivePhotosInteractor
import dk.nodes.template.domain.interactors.positivePhotos.RemovePositivePhotoInteractor
import dk.nodes.template.domain.managers.PrefManager
import dk.nodes.template.presentation.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class PositiveListViewModel @Inject constructor(
        private val getAllPositivePhotosInteractor: GetAllPositivePhotosInteractor,
        private val removePositivePhotoInteractor: RemovePositivePhotoInteractor,
        private val addPhotoInteractor: AddPhotoInteractor,
        private val prefManager: PrefManager
) : BaseViewModel<PositiveListViewState>(PositiveListViewState()) {

    init {
        fetchPictures()
    }

    fun fetchPictures() {
        viewModelScope.launch(Dispatchers.Main) {
            val result = getAllPositivePhotosInteractor.getPhotos()
            state = state.copy(list = result)
        }
    }


    fun removePositivePhoto(imagePath: String)= viewModelScope.launch(Dispatchers.Main) {
        removePositivePhotoInteractor.removePositivePhoto(imagePath)
        addPhotoInteractor.addPhoto(Photo(imagePath,77))
    }

    fun getListOfPositivePhotos(): MutableList<Photo> {
        return state.list
    }

    fun addPhotoPathToPrefManager(path: String?) {
        prefManager.setString("PhotoToBeEnlargedInPref",path.toString())

    }

}