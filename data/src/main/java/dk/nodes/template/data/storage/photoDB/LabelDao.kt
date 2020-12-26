package dk.nodes.template.data.storage.photoDB

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dk.nodes.template.domain.entities.Label

@Dao
interface LabelDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(labels: List<Label>)

    @Query("SELECT * FROM labels WHERE labelID =:id")
    suspend fun getLabelWithId(id : Int) : Label
}