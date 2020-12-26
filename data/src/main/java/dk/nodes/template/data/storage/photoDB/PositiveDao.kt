package dk.nodes.template.data.storage.photoDB

import androidx.room.*
import dk.nodes.template.domain.entities.Photo

@Dao
interface PositiveDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addPositivePhoto(photo: Photo)

    @Query("DELETE FROM photos WHERE photoPath = :photoPath and classifier = 1  ")
    suspend fun removePositivePhoto(photoPath: String?)


    @Query("SELECT * FROM photos WHERE classifier = 1")
    suspend fun getAllPositivePhotos() : MutableList<Photo>

    @Query("DELETE FROM photos WHERE classifier = 1")
    suspend fun deleteAllPositivePhotos()
}