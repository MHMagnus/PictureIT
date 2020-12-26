package dk.nodes.template.domain.entities


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "ratedphotos")

data class LabelledRatedPhoto (
        @SerializedName("id") @PrimaryKey val photoPath: String,
        @SerializedName("vectorvalues") val vectorValues: MutableList<Float>,
        @SerializedName("vectorlabels") val vectorLabels: MutableList<String>,
        @SerializedName("ratings")val rating: Int
)