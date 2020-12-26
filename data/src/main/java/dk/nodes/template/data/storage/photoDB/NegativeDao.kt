package dk.nodes.template.data.storage.photoDB

import androidx.room.*
import dk.nodes.template.domain.entities.Photo

@Dao
interface NegativeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addNegativePhoto(photo: Photo?)

    @Query("DELETE FROM photos WHERE photoPath = :photoPath AND classifier = 2  ")
    suspend fun removeNegativePhoto(photoPath: String?)


    @Query("SELECT * FROM photos WHERE classifier = 2")
    suspend fun getAllNegativePhotos() : MutableList<Photo>

    @Query("DELETE FROM photos WHERE classifier = 2")
    suspend fun deleteAllNegativePhotos()
}