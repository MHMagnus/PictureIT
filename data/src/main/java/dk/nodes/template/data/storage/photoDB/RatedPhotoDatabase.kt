package dk.nodes.template.data.storage.photoDB
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dk.nodes.template.domain.entities.SavedModelPhoto

@TypeConverters(DbDataConverter::class)
@Database(entities =[SavedModelPhoto::class], version = 2, exportSchema = false)
abstract class RatedPhotoDatabase :RoomDatabase(){

    abstract fun ratedPhotoDao():SaveModelDao

    companion object{
        @Volatile private var instance:RatedPhotoDatabase?=null
        private val LOCK=Any()

        operator fun invoke(context:Context)=instance
                ?:synchronized(LOCK){
                    instance
                            ?:buildDatabase(context).also{instance=it}
                }

        private fun buildDatabase(context:Context)=Room.databaseBuilder(context,
                RatedPhotoDatabase::class.java,"ratedphotos")
                .fallbackToDestructiveMigration()
                .build()
    }

}