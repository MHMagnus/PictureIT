package dk.nodes.template.data.storage.photoDB

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import dk.nodes.template.domain.entities.Label


import dk.nodes.template.domain.entities.Photo


@Database(entities = [Label::class], version = 1,exportSchema = false)
abstract class LabelsDatabase  : RoomDatabase() {

    abstract fun labelDao(): LabelDao


    companion object {
        @Volatile private var instance: LabelsDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context)= instance
                ?: synchronized(LOCK){
                    instance
                            ?: buildDatabase(context).also { instance = it}
                }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(context,
                LabelsDatabase::class.java, "labels")
                .build()
    }

}