package dk.nodes.template.data.models.network


import android.content.Context
import dk.nodes.template.data.storage.photoDB.*
import dk.nodes.template.domain.entities.Label
import dk.nodes.template.domain.entities.Photo
import dk.nodes.template.domain.entities.SavedModelPhoto
import dk.nodes.template.domain.repositories.PhotoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PhotoRepositoryImpl @Inject constructor(private val context: Context) : PhotoRepository {

    private var photoDao: PhotoDao? = null
    private var positiveDao: PositiveDao? = null
    private var negativeDao: NegativeDao? = null
    private var saveModelDao: SaveModelDao? = null
    private var labelDao: LabelDao? = null


    init {
        val db = PhotoDatabase.invoke(context)
        positiveDao = db.positiveDao()
        negativeDao = db.negativeDao()

        val canddb = CandidatePhotoDatabase.invoke(context)
        photoDao = canddb.photoDao()

        val rateddb = RatedPhotoDatabase.invoke(context)
        saveModelDao = rateddb.ratedPhotoDao()

        val labelsdb = LabelsDatabase.invoke(context)
        labelDao = labelsdb.labelDao()
    }

    override suspend fun getLabel(labelId: Int): Label? {
        return withContext(Dispatchers.IO) { labelDao?.getLabelWithId(labelId) }
    }

    override suspend fun storeLabelList(labelList: List<Label>) {
        labelDao?.insertList(labelList)
    }


    //Rated Photo
    override suspend fun getSaveModelPhoto(photoPath: String): SavedModelPhoto? {
        return withContext(Dispatchers.IO) { saveModelDao?.getSaveModelPhotoWithId(photoPath) }
    }

    override suspend fun addSaveModelPhoto(photo: SavedModelPhoto): Boolean {
        withContext(Dispatchers.IO) { saveModelDao?.addPhoto(photo) }
        return true
    }

    override suspend fun storeSaveModelPhotosList(saveModelPhotoList: List<SavedModelPhoto>) {
        saveModelDao?.insertList(saveModelPhotoList)
    }

    override suspend fun removeAllSaveModelPhotos() {
        withContext(Dispatchers.IO) { saveModelDao?.deleteAll() }
    }


    override suspend fun removeSaveModelPhoto(photoPath: String): Boolean {
       withContext(Dispatchers.IO) { saveModelDao?.deletePhoto(photoPath) }
        return true
    }

    override suspend fun removeAllBestCandidates() {
        withContext(Dispatchers.IO) { photoDao?.deleteAllCandidates() }
    }

    override suspend fun removeAll() {
        withContext(Dispatchers.IO) { photoDao?.deleteAll() }
    }

    override suspend fun storePhotosList(photoList: List<Photo>) {
        photoDao?.insertList(photoList)
    }

    override suspend fun getPhotosForHistory(): MutableList<Photo> {
        return withContext(Dispatchers.IO) { photoDao?.getAllUnseenPhotos() } ?: mutableListOf()
    }

    override suspend fun getAllBestCandidates(): MutableList<Photo> {
        return withContext(Dispatchers.IO) { photoDao?.getAllBestCandidatePhotos() } ?: mutableListOf()
    }


    override suspend fun addPhoto(photo: Photo): Boolean {
        withContext(Dispatchers.IO) { photoDao?.addPhoto(photo) }
        return true
    }

    override suspend fun getAllPositivePhotos(): MutableList<Photo> {
        return withContext(Dispatchers.IO) { positiveDao?.getAllPositivePhotos() }
                ?: mutableListOf()
    }

    override suspend fun addToPositivePhotos(photo: Photo): Boolean {
        withContext(Dispatchers.IO) { positiveDao?.addPositivePhoto(photo) }
        return true
    }

    override suspend fun removePositivePhoto(photoPath: String?): Boolean {
        withContext(Dispatchers.IO) { positiveDao?.removePositivePhoto(photoPath) }
        return false
    }

    override suspend fun getAllNegativePhotos(): MutableList<Photo> {
        return withContext(Dispatchers.IO) { negativeDao?.getAllNegativePhotos() }
                ?: mutableListOf()
    }

    override suspend fun addNegativePhoto(photo: Photo): Boolean {
        withContext(Dispatchers.IO) { negativeDao?.addNegativePhoto(photo) }
        return true
    }

    override suspend fun removeFromNegative(imagePath: String?): Boolean {
        withContext(Dispatchers.IO) { negativeDao?.removeNegativePhoto(imagePath) }
        return false
    }

    override suspend fun removeAllNegativePhotos() {
        withContext(Dispatchers.IO) { negativeDao?.deleteAllNegativePhotos() }
    }

    override suspend fun removeAllPositivePhotos() {
        withContext(Dispatchers.IO) { positiveDao?.deleteAllPositivePhotos() }
    }

    override suspend fun getSaveModelPhotos(): MutableList<SavedModelPhoto> {
        return withContext(Dispatchers.IO) { saveModelDao?.getAllSaveModelPhotos() } ?: mutableListOf()
    }
}