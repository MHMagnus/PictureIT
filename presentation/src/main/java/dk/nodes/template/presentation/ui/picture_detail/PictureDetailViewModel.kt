package dk.nodes.template.presentation.ui.picture_detail

import androidx.lifecycle.viewModelScope
import dk.nodes.template.domain.entities.Label
import dk.nodes.template.domain.entities.RatedPhoto
import dk.nodes.template.domain.interactors.labels.GetLabelFromIdInteractor
import dk.nodes.template.domain.managers.PrefManager
import dk.nodes.template.presentation.ui.base.BaseViewModel
import dk.nodes.template.presentation.util.getRatedPhoto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


class PictureDetailViewModel @Inject constructor(
        private val prefManager: PrefManager,
        private val getLabelFromIdInteractor: GetLabelFromIdInteractor) : BaseViewModel<PictureDetailViewState>(PictureDetailViewState()) {


    fun getPathFromPrefManager(): String? {
        return prefManager.getString("PhotoToBeEnlargedInPref", null)
    }

    private fun loadLabelsAndValues(photoPath: String): RatedPhoto {
        return getRatedPhoto(photoPath, 7)
    }

    private fun getLabelName(id: Int) {
        var tempNumber = 1
        val concatinatedLabel: MutableList<String> = mutableListOf()
        try {
            viewModelScope.launch(Dispatchers.Main) {
                val labelFromRoom = getLabelFromIdInteractor.getPhotos(id)
                setHeaderTv(labelFromRoom)
                if (labelFromRoom != null) {
                    for (l in state.concatinationOfLabels){
                        concatinatedLabel.add(l)
                        tempNumber++}
                    concatinatedLabel.add("$tempNumber) ${labelFromRoom.labelName}\n")
                }
                concatinatedLabel.sort()
                if (concatinatedLabel.isNotEmpty()) {
                    state = state.copy(concatinationOfLabels = concatinatedLabel)
                }
            }
        } catch (e: Exception) {
            Timber.e("$e was caught while trying to retrieve Labels from labelsdb")
            return
        }
    }

    private fun setHeaderTv(labelFromRoom: Label?) {
        if (state.headerLabel.isBlank()) {
            if (labelFromRoom != null) {
                state = state.copy(headerLabel = labelFromRoom.labelName)
            }
        }
    }


    fun setUpDataForTextViews(photoPath: String) {
        val concatinatedValues: MutableList<String> = mutableListOf()
        val photo = loadLabelsAndValues(photoPath)
        for (vl in photo.vectorLabels) {
            getLabelName(vl)
        }
        for (vv in photo.vectorValues) {
            concatinatedValues.add("$vv, \n")
        }
        state = state.copy(listOfConfidences = concatinatedValues)
    }
}