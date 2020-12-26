package dk.nodes.template.data.storage.photoDB

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dk.nodes.template.domain.entities.Photo

@Dao
interface PhotoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(photos: List<Photo>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addPhoto(photo: Photo?)

    @Query("DELETE FROM photos WHERE photoPath = :photoPath  ")
    suspend fun deletePhoto(photoPath: String?)

    @Query("SELECT * FROM photos WHERE classifier = 77")
    suspend fun getAllUnseenPhotos() : MutableList<Photo>

    @Query("SELECT * FROM photos WHERE classifier = 9")
    suspend fun getAllBestCandidatePhotos() : MutableList<Photo>

    @Query("DELETE FROM photos WHERE classifier = 9")
    suspend fun deleteAllCandidates()

    @Query("DELETE FROM photos WHERE classifier >= 0")
    suspend fun deleteAll()


}
