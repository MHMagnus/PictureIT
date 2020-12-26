package dk.nodes.template.domain.entities


import androidx.annotation.Nullable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "ratedphotos")

data class RatedPhoto(
        @SerializedName("id") @PrimaryKey val photoPath: String,
        @SerializedName("vectorvalues") val vectorValues: FloatArray,
        @SerializedName("vectorlabels") val vectorLabels: IntArray,
        @SerializedName("ratings")val rating: Int
)