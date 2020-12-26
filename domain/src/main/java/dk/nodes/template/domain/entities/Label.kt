package dk.nodes.template.domain.entities

import com.google.gson.annotations.SerializedName

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "labels")

data class Label (
    @SerializedName("labelid")
    @PrimaryKey
    var labelID: Int =0,

    @SerializedName ("labeLName")
    var labelName:  String =""
)