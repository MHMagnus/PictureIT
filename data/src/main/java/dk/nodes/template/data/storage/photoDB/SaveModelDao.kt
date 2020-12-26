package dk.nodes.template.data.storage.photoDB;

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dk.nodes.template.domain.entities.Photo
import dk.nodes.template.domain.entities.RatedPhoto
import dk.nodes.template.domain.entities.SavedModelPhoto

@Dao
interface SaveModelDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(ratedphotos: List<SavedModelPhoto>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addPhoto(ratedphotos: SavedModelPhoto?)

    @Query("SELECT * FROM ratedphotos WHERE photoPath =:photoPath")
    suspend fun getSaveModelPhotoWithId(photoPath : String) : SavedModelPhoto

    @Query("DELETE FROM ratedphotos")
    suspend fun deleteAll()

    @Query("DELETE FROM ratedphotos WHERE photoPath = :photoPath  ")
    suspend fun deletePhoto(photoPath: String?)

    @Query("SELECT * FROM ratedphotos")
    suspend fun getAllSaveModelPhotos() : MutableList<SavedModelPhoto>
}

