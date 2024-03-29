package dk.nodes.template.domain.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlin.String
@Entity(tableName = "photos")
data class Photo(
        @SerializedName("id") @PrimaryKey val photoPath: String,
        @SerializedName("classifier") var classifier: Int
)

