package dk.nodes.template.presentation.ui.loadmodel

import androidx.lifecycle.viewModelScope
import dk.nodes.template.domain.entities.Photo
import dk.nodes.template.domain.entities.SavedModelPhoto
import dk.nodes.template.domain.interactors.negativePhotos.AddNegativePhotoInteractor
import dk.nodes.template.domain.interactors.positivePhotos.AddPositivePhotoInteractor
import dk.nodes.template.domain.interactors.ratedPhotos.GetAllRatedPhotosInteractor
import dk.nodes.template.domain.interactors.ratedPhotos.RemoveAllRatedPhotos
import dk.nodes.template.domain.managers.PrefManager
import dk.nodes.template.presentation.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class LoadModelViewModel @Inject constructor(
        private val getAllRatedPhotosInteractor: GetAllRatedPhotosInteractor,
        private val addNegativePhotoInteractor: AddNegativePhotoInteractor,
        private val addPositivePhotoInteractor: AddPositivePhotoInteractor,
        private val prefManager: PrefManager
) : BaseViewModel<LoadModelViewState>(LoadModelViewState()) {

    init {
        getSaveModelFromRoom()
    }

    fun loadModelOneName(): String? {
        return prefManager.getString("SavedModel1Title", "")
    }

    fun loadModelTwoName(): String? {
        return prefManager.getString("SavedModel2Title", "")
    }

    fun loadModelThreeName(): String? {
        return prefManager.getString("SavedModel3Title", "")
    }

    fun loadModelFourName(): String? {
        return prefManager.getString("SavedModel4Title", "")
    }

    fun loadModelOne() {
        addPhotosToRespectiveRelevanceGroup(state.listOneFromRoom)
    }

    fun loadModelTwo() {
        addPhotosToRespectiveRelevanceGroup(state.listTwoFromRoom)
    }

    fun loadModelThree() {
        addPhotosToRespectiveRelevanceGroup(state.listThreeFromRoom)
    }

    fun loadModelFour() {
        addPhotosToRespectiveRelevanceGroup(state.listFourFromRoom)
    }

    private fun getSaveModelFromRoom() {
        var storedModelFromRoom: MutableList<SavedModelPhoto>
        val tempListOne: MutableList<SavedModelPhoto> = mutableListOf()
        val tempListTwo: MutableList<SavedModelPhoto> = mutableListOf()
        val tempListThree: MutableList<SavedModelPhoto> = mutableListOf()
        val tempListFour: MutableList<SavedModelPhoto> = mutableListOf()


        viewModelScope.launch(Dispatchers.Main) {
            storedModelFromRoom = getAllRatedPhotosInteractor.retrieveSaveModelPhotos()
            Timber.d("$storedModelFromRoom test123 this is the storedModelFromRoom ")
            for (savedPhoto in storedModelFromRoom) {
                when (savedPhoto.classifier) {
                    21 -> tempListOne.add(savedPhoto)
                    22 -> tempListOne.add(savedPhoto)
                    31 -> tempListTwo.add(savedPhoto)
                    32 -> tempListTwo.add(savedPhoto)
                    41 -> tempListThree.add(savedPhoto)
                    42 -> tempListThree.add(savedPhoto)
                    51 -> tempListFour.add(savedPhoto)
                    52 -> tempListFour.add(savedPhoto)
                }
            }
            state = state.copy(
                    listOneFromRoom = tempListOne,
                    listTwoFromRoom = tempListTwo,
                    listThreeFromRoom = tempListThree,
                    listFourFromRoom = tempListFour)

            Timber.d("${state.listOneFromRoom}")
        }
    }

    private fun addPhotosToRespectiveRelevanceGroup(formerModelsPhotosFromRoom: MutableList<SavedModelPhoto>) {
        Timber.d("$formerModelsPhotosFromRoom test 123 is list of formerModelsPhotosFromRoom")
        for (storedImage in formerModelsPhotosFromRoom) {
            if ((storedImage.classifier % 2) == 0) {
                GlobalScope.launch(Dispatchers.Main) {
                    addNegativePhotoInteractor.addNegativePhoto(Photo(storedImage.photoPath, 1))
                    Timber.d("$storedImage test123 was added to negative")
                }
            }
            if ((storedImage.classifier % 2) == 1) {
                GlobalScope.launch(Dispatchers.Main) {
                    addPositivePhotoInteractor.addPositivePhoto(Photo(storedImage.photoPath, 2))
                    Timber.d("$storedImage test123 was added to positive")

                }
            }
        }
    }

    fun getPictureFromList(list: MutableList<SavedModelPhoto>): String {
        var temp = ""
        for (photo in list)
            if (photo.classifier%2 ==0)
                temp = photo.photoPath
        return temp

    }
}