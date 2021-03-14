package dk.nodes.template.presentation.ui.savemodel

import androidx.lifecycle.viewModelScope
import dk.nodes.template.domain.entities.SavedModelPhoto
import dk.nodes.template.domain.interactors.negativePhotos.GetAllNegativePhotosInteractor
import dk.nodes.template.domain.interactors.positivePhotos.GetAllPositivePhotosInteractor
import dk.nodes.template.domain.interactors.ratedPhotos.InsertListOfRatedPhotosInteractor
import dk.nodes.template.domain.interactors.ratedPhotos.RemoveAllRatedPhotos
import dk.nodes.template.domain.managers.PrefManager
import dk.nodes.template.presentation.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class SaveModelViewModel @Inject constructor(
        private val getAllPositivePhotosInteractor: GetAllPositivePhotosInteractor,
        private val getAllNegativePhotosInteractor: GetAllNegativePhotosInteractor,
        private val insertListOfRatedPhotosInteractor: InsertListOfRatedPhotosInteractor,
        private val removeAllRatedPhotos: RemoveAllRatedPhotos,
        private val prefManager: PrefManager
) : BaseViewModel<SaveModelViewState>(SaveModelViewState()) {

    init {
        getSvmModelPhotos()
    }


    fun addTitleOfSavedModel(number: Int, text: String) = viewModelScope.launch(Dispatchers.IO) {
        when (number) {
            1 ->         prefManager.setString("SavedModel1Title", text)
            2 ->         prefManager.setString("SavedModel2Title", text)
            3 ->         prefManager.setString("SavedModel3Title", text)
            4 ->         prefManager.setString("SavedModel4Title", text)
        }
    }


    fun removeAllStoredModels() = viewModelScope.launch(Dispatchers.IO) {
        removeAllRatedPhotos.removeAllRatedPhotos()
    }

    private fun getSvmModelPhotos() {
        viewModelScope.launch(Dispatchers.Main) {
            val posResults = getAllPositivePhotosInteractor.getPhotos()
            val negativeResult = getAllNegativePhotosInteractor.getAllNegativePhotos()
            Timber.d("$posResults test123 is the posResult")
            Timber.d("$negativeResult test123 is the negativeResult")
            state = state.copy(
                    posList = posResults,
                    negativeList = negativeResult
            )
        }
    }

    fun saveModelToRoom(positiveClassifier: Int, negativeClassifier: Int) = viewModelScope.launch(Dispatchers.IO) {
        val currentModelsPhotos: MutableList<SavedModelPhoto> = mutableListOf()

        Timber.d("${state.posList} test123 posList in SaveModeFragment")
        Timber.d("${state.negativeList} test123 negativeList in SaveModelFragment")

        for (photo in state.posList) {
            currentModelsPhotos.add(SavedModelPhoto(photo.photoPath, positiveClassifier))
        }
        for (photo in state.negativeList) {
            currentModelsPhotos.add(SavedModelPhoto(photo.photoPath, negativeClassifier))
        }
        insertListOfRatedPhotosInteractor.storeRatedPhotos(currentModelsPhotos)

        Timber.d("$currentModelsPhotos test123 currentModelsPhotos")
    }

    fun getItemFromPositiveList(): String {
        return state.posList[0].photoPath
    }

    fun loadModel1Name(): String? {
        return prefManager.getString("SavedModel1Title", "")
    }
    fun loadModel2Name(): String? {
        return prefManager.getString("SavedModel2Title", "")
    }
    fun loadModel3Name(): String? {
        return prefManager.getString("SavedModel3Title", "")
    }
    fun loadModel4Name(): String? {
        return prefManager.getString("SavedModel4Title", "")
    }
}