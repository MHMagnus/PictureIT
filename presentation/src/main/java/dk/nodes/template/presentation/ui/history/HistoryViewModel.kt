package dk.nodes.template.presentation.ui.history

import android.database.Cursor
import android.os.Build
import android.provider.MediaStore
import androidx.lifecycle.viewModelScope
import dk.nodes.template.domain.entities.Photo
import dk.nodes.template.domain.interactors.bestCandidatePhotos.SaveListOfPhotosInteractor
import dk.nodes.template.domain.interactors.memory.GetMemoryPhotosInteractor
import dk.nodes.template.domain.interactors.negativePhotos.AddNegativePhotoInteractor
import dk.nodes.template.domain.interactors.photos.RemoveAllPhotosInteractor
import dk.nodes.template.domain.interactors.photos.RemovePhotoInteractor
import dk.nodes.template.domain.interactors.positivePhotos.AddPositivePhotoInteractor
import dk.nodes.template.domain.managers.PrefManager
import dk.nodes.template.presentation.ui.base.BaseViewModel
import dk.nodes.template.presentation.util.VectorLab
import dk.nodes.template.presentation.util.getFullPath
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import javax.inject.Inject

class HistoryViewModel @Inject constructor(
        private val addPositivePhotoInteractor: AddPositivePhotoInteractor,
        private val addNegativePhotoInteractor: AddNegativePhotoInteractor,
        private val saveListOfPhotosInteractor: SaveListOfPhotosInteractor,
        private val getMemoryPhotosInteractor: GetMemoryPhotosInteractor,
        private val removeAllPhotosInteractor: RemoveAllPhotosInteractor,
        private val prefManager: PrefManager
) : BaseViewModel<HistoryViewState>(HistoryViewState()) {

    init {
        fetchPicturesFromRoom()
    }

    private fun fetchPicturesFromRoom() = viewModelScope.launch(Dispatchers.Main) {
        val result = getMemoryPhotosInteractor.retrieveMemoryPhotos()
        state = state.copy(list = result)
    }


    fun addPhotoPathToPrefManager(path: String?) {
        prefManager.setString("PhotoToBeEnlargedInPref", path.toString())
    }

    fun addNegativePhoto(photo: Photo) = viewModelScope.launch(Dispatchers.Main) {
        addNegativePhotoInteractor.addNegativePhoto(photo)
    }

    fun addPositivePhoto(photo: Photo) = viewModelScope.launch(Dispatchers.Main) {
        addPositivePhotoInteractor.addPositivePhoto(photo)
    }


    private fun randomImageFromDB(cursor: Cursor, vectorLab: VectorLab): String? {

        val numberOfUnseenImages = cursor.count
        Timber.d("Number of images on device: $numberOfUnseenImages test123")
        vectorLab.sizeOfDatabase()


        return if (state.list.size == 0) {
            val imagePathIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
            val random = Random()
            if (numberOfUnseenImages > 1) {
                cursor.moveToPosition(random.nextInt(numberOfUnseenImages))
            }
            getFullPath(imagePathIndex.let { cursor.getString(it) })
        } else {
            val randomID = vectorLab.queryRandomUnseen()
            getFullPath(vectorLab.getPathFromID(randomID))
        }
    }

    fun randomPicturesFromDb(vectorLab: VectorLab?, cursor: Cursor?): MutableList<String?> {
        val pathSet: MutableSet<String?> = HashSet()
        while (pathSet.size < 16) {
            if (cursor != null && vectorLab != null) {
                val temp = randomImageFromDB(cursor, vectorLab)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (Files.exists(Paths.get(temp.toString()))) {
                        pathSet.add(temp)
                    }
                }
            }
        }
        return ArrayList(pathSet)
    }

    fun saveListOfPhotosToRoom(list: MutableList<Photo>) = viewModelScope.launch(Dispatchers.Main) {

        var temp: MutableList<Photo> = mutableListOf()

        val savedImages = getMemoryPhotosInteractor.retrieveMemoryPhotos()

        list.addAll(savedImages)
        temp = list
        removeAllPhotosInteractor.removeAllPhotos()
        saveListOfPhotosInteractor.saveListOfPhotos(temp)
    }
}