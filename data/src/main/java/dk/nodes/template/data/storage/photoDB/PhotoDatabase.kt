package dk.nodes.template.data.storage.photoDB

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


import dk.nodes.template.domain.entities.Photo


@Database(entities = [Photo::class], version = 1,exportSchema = false)
abstract class PhotoDatabase : RoomDatabase() {

    abstract fun negativeDao(): NegativeDao
    abstract fun positiveDao(): PositiveDao
    abstract fun photoDao(): PhotoDao

    companion object {
        @Volatile private var instance: PhotoDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context)= instance
                ?: synchronized(LOCK){
            instance
                    ?: buildDatabase(context).also { instance = it}
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(context,
                PhotoDatabase::class.java, "photos")
                .build()
    }

}