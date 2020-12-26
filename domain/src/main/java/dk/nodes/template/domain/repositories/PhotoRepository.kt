package dk.nodes.template.domain.repositories

import dk.nodes.template.domain.entities.Label
import dk.nodes.template.domain.entities.Photo
import dk.nodes.template.domain.entities.SavedModelPhoto

interface PhotoRepository {

    suspend fun getLabel(labelId: Int): Label?

    suspend fun storeLabelList(labelList: List<Label>)

    suspend fun getSaveModelPhoto(photoPath: String): SavedModelPhoto?

    suspend fun addSaveModelPhoto(photo: SavedModelPhoto): Boolean

    suspend fun storeSaveModelPhotosList(saveModelPhotoList: List<SavedModelPhoto>)

    suspend fun removeAllBestCandidates()

    suspend fun storePhotosList(photoList: List<Photo>)

    suspend fun addPhoto(photo: Photo): Boolean

    suspend fun getAllBestCandidates(): MutableList<Photo>

    suspend fun getAllPositivePhotos(): MutableList<Photo>

    suspend fun addToPositivePhotos(photo: Photo): Boolean

    suspend fun removePositivePhoto(photoPath: String?): Boolean

    suspend fun getAllNegativePhotos(): MutableList<Photo>

    suspend fun addNegativePhoto(photo: Photo): Boolean

    suspend fun removeFromNegative(imagePath: String?): Boolean

    suspend fun removeAllNegativePhotos()

    suspend fun removeAllPositivePhotos()

    suspend fun getPhotosForHistory(): MutableList<Photo>

    suspend fun removeAll()
    suspend fun removeAllSaveModelPhotos()
    suspend fun removeSaveModelPhoto(photoPath: String): Boolean
    suspend fun getSaveModelPhotos(): MutableList<SavedModelPhoto>
}