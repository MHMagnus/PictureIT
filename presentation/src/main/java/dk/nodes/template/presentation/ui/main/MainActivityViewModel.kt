package dk.nodes.template.presentation.ui.main

import android.content.Context
import android.os.Build
import androidx.lifecycle.viewModelScope
import dk.nodes.template.domain.entities.Label
import dk.nodes.template.domain.entities.Photo
import dk.nodes.template.domain.interactors.bestCandidatePhotos.RemoveAllFromBestCandidatesInteractor
import dk.nodes.template.domain.interactors.bestCandidatePhotos.SaveListOfPhotosInteractor
import dk.nodes.template.domain.interactors.labels.GetLabelFromIdInteractor
import dk.nodes.template.domain.interactors.labels.InsertListOfLabelsInteractor
import dk.nodes.template.domain.interactors.negativePhotos.GetAllNegativePhotosInteractor
import dk.nodes.template.domain.interactors.negativePhotos.RemoveAllNegativePhotosInteractor
import dk.nodes.template.domain.interactors.positivePhotos.GetAllPositivePhotosInteractor
import dk.nodes.template.domain.interactors.positivePhotos.RemoveAllPositivePhotosInteractor
import dk.nodes.template.presentation.ui.base.BaseViewModel
import dk.nodes.template.presentation.ui.main.LocalSVM.svm_model
import dk.nodes.template.presentation.util.SVM
import dk.nodes.template.presentation.util.VectorLab
import dk.nodes.template.presentation.util.getFullPath
import dk.nodes.template.presentation.util.getRatedPhotoList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import javax.inject.Inject
import kotlin.Comparator


class MainActivityViewModel @Inject constructor(
        private val getAllNegativePhotosInteractor: GetAllNegativePhotosInteractor,
        private val getAllPositivePhotosInteractor: GetAllPositivePhotosInteractor,
        private val vectorLab: VectorLab,
        private val saveListOfPhotosInteractor: SaveListOfPhotosInteractor,
        private val removeAllFromBestCandidatesInteractor: RemoveAllFromBestCandidatesInteractor,
        private val insertListOfLabelsInteractor: InsertListOfLabelsInteractor,
        private val removeAllNegativePhotosInteractor: RemoveAllNegativePhotosInteractor,
        private val removeAllPositivePhotosInteractor: RemoveAllPositivePhotosInteractor,
        private val getLabelFromIdInteractor: GetLabelFromIdInteractor

) : BaseViewModel<MainActivityViewState>(MainActivityViewState()) {

    private var generateModelAlreadyRunning: Boolean = false

    fun setGenerateModelAlreadyRunning(bool: Boolean) {
        generateModelAlreadyRunning = bool
    }

    fun getLabelsFromAssets(context: Context) = viewModelScope.launch(Dispatchers.IO) {
        val id = 0
        val list: MutableList<Label> = mutableListOf()
        if (getLabelFromIdInteractor.getPhotos(0) == null) {
            importLabelsFromAssetsAndPersistInRoom(context, list, id)
        }
    }

    private suspend fun importLabelsFromAssetsAndPersistInRoom(context: Context, list: MutableList<Label>, id: Int) {
        var id = id
        val file = context.assets.open("labels_words.txt")
        file.bufferedReader().forEachLine {
            list.add(Label(id, it))
            id++
        }
        insertListOfLabelsInteractor.storeLabelsList(list)
    }


    fun generateModels() = viewModelScope.launch(Dispatchers.IO) {
        if (!generateModelAlreadyRunning) {
            setGenerateModelAlreadyRunning(true)
            Timber.d("generateModelisCalled and generateModelAlready running is $generateModelAlreadyRunning")

            val list = getRatedPhotoList(getAllPositivePhotosInteractor.getPhotos(), 1) + getRatedPhotoList(getAllNegativePhotosInteractor.getAllNegativePhotos(), -1)

            if (list.isNotEmpty()) {
                for (item in list) {
                    state.listOfConfidences.add(item.vectorValues)
                    state.listOfLabels.add(item.vectorLabels)
                    state.listOfRatings.add(item.rating)
                }
                buildSvmModel()
            }
        } else {
            Timber.d("returned as generateModel is already running test123")
            return@launch
        }
    }

    private fun buildSvmModel() {
        val model = SVM.buildModel(state.listOfRatings,
                state.listOfConfidences,
                state.listOfLabels)

        Timber.d("${state.listOfBestCandidates} listofBC ${state.listOfConfidences} listofC ${state.listOfLabels} listOfL test123 in BuildSVMModel")
        val bestPaths = getBestCandidateDistanceBased(model)
        // add the next best from bestCandidatePaths
        state.listOfBestCandidates.clear()

        for (i in bestPaths) {
            val fullPathToAdd = getFullPath(i)
            if (!state.listOfBestCandidates.contains(fullPathToAdd)) {
                state.listOfBestCandidates.add(fullPathToAdd)
            }
        }
        saveBestCandidatesInRoom()
        setGenerateModelAlreadyRunning(false)
        Timber.d("buildSvmModel has finished and generateModelAlreadyRunning is set to $generateModelAlreadyRunning")
    }

    fun deleteCandidates() = viewModelScope.launch(Dispatchers.IO) {
        state.list.clear()
        state.listOfLabels.clear()
        state.listOfRatings.clear()
        state.listOfConfidences.clear()
        state.listOfBestCandidates.clear()
        removeAllFromBestCandidatesInteractor.removeBestCandidates()
        vectorLab.removeSeen()
    }

    fun emptyLists() = viewModelScope.launch(Dispatchers.IO) {
        removeAllNegativePhotosInteractor.removeNegativePhotos()
        removeAllPositivePhotosInteractor.removePositivePhotos()
    }

    private fun saveBestCandidatesInRoom() = viewModelScope.launch(Dispatchers.Main) {
        val listOfBestCandidates: MutableList<Photo> = mutableListOf()
        for (path in state.listOfBestCandidates) {
            if (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        Files.exists(Paths.get(path.toString()))
                    } else {
                        TODO("VERSION.SDK_INT < O")
                    }) {
                listOfBestCandidates.add(Photo(getFullPath(path).toString(), 9))
            }
        }
        removeAllFromBestCandidatesInteractor.removeBestCandidates()
        saveListOfPhotosInteractor.saveListOfPhotos(listOfBestCandidates)
    }

    private fun getBestCandidateDistanceBased(model: svm_model): List<String?> {
        val bestCandidatePaths: MutableMap<String?, Double?> = HashMap()
        // Map <imageID, labelList>
        val unseenLabels: Map<Int, List<Int>?>? = vectorLab.queryUnseenFeatures()
        // Map <imageID, probsList>
        val unseenConfidence: Map<Int, List<Float>?>? = vectorLab.queryUnseenProbs()
        // Setting up a map for the best candidates ids and distances
        val bestCandidates: MutableMap<Int, Double> = HashMap() // 6 best distances
        val bestPaths: List<String?>
        val iterator = unseenLabels?.entries?.iterator()
        if (iterator != null) {
            while (iterator.hasNext()) {
                val entry = iterator.next()
                val key = entry.key
                val oneProbsVectorList = unseenConfidence?.get(key)
                val labelsVectorList = unseenLabels[key]

                //convert to arrays
                val testLabels = IntArray(labelsVectorList!!.size)
                val testProbs = FloatArray(oneProbsVectorList!!.size)
                for (i in labelsVectorList.indices) {
                    testProbs[i] = oneProbsVectorList[i]
                    testLabels[i] = labelsVectorList[i]
                }

                // get distance for each unseen image
                val distance = SVM.doPredictionDistanceBased(model, testProbs, testLabels)
                if (bestCandidates.size < 16) {
                    bestCandidates[key] = distance
                } else {
                    bestCandidates[key] = distance
                    val c = Comparator<Double> { o1, o2 ->
                        // -1 if o1 < o2
                        if (o1 < o2) -1 else if (o1 == o2) 0 else 1 // o1 > o2
                    }
                    var min: Map.Entry<Int, Double>? = null
                    // Getting the minimum distance value in bestCandidates
                    for (bestEntry in bestCandidates.entries) {
                        if (min == null || c.compare(min.value, bestEntry.value) > 0) {
                            min = bestEntry
                        }
                    }
                    if (min != null) {
                        if (distance >= min.value) {
                            bestCandidates.remove(min.key)
                            bestCandidates[key] = distance
                        }
                    }
                }
            }
        }
        for (item in bestCandidates) {
            if (item.value > 0.0) {
                val path = vectorLab.getPathFromID(item.key)
                bestCandidatePaths[path] = bestCandidates[item.key]
            }
        }
        bestPaths = orderBestCandidatePaths(bestCandidatePaths)
        return bestPaths
    }

    private fun orderBestCandidatePaths(bestCandPaths: Map<String?, Double?>): List<String?> {
        val list: List<Map.Entry<String?, Double?>> = LinkedList(bestCandPaths.entries)
        val orderedBestPaths: MutableList<String?> = ArrayList()
        val comp: java.util.Comparator<Map.Entry<String?, Double?>> =
                Comparator { o1, o2 ->
                    o2.value?.let {
                        o1.value?.compareTo(it)
                    }!!
                }
        Collections.sort(list, comp)
        for ((key) in list) {
            orderedBestPaths.add(key)
        }
        return orderedBestPaths
    }
}