package dk.nodes.template.data.storage.photoDB

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class DbDataConverter {

    inline fun <reified T> genericType() = object : TypeToken<T>() {}.type

    private val gson by lazy { Gson() }


    @TypeConverter
    fun fromVectorValue(data: FloatArray): String{
        val type = genericType<FloatArray>()
        return gson.toJson(data, type)

    }

    @TypeConverter
    fun toVectorValue(jsonValue : String) : FloatArray{
        val type = genericType<FloatArray>()
        return gson.fromJson<FloatArray>(jsonValue,type)
    }


    @TypeConverter
    fun fromVectorLabels(data: IntArray): String{
        val type = genericType<IntArray>()
        return gson.toJson(data, type)

    }

    @TypeConverter
    fun toVectorLabels(jsonValue : String) : IntArray{
        val type = genericType<IntArray>()
        return gson.fromJson<IntArray>(jsonValue,type)
    }
}