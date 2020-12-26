package dk.nodes.template.data.injection

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dk.nodes.template.data.storage.photoDB.*

import javax.inject.Singleton

@Module
class DatabaseModule {



    @Provides
    @Singleton
    fun provideCandidateRoomDb(context: Context): CandidatePhotoDatabase {
        return Room.databaseBuilder(context, CandidatePhotoDatabase::class.java, "candidatephotos").build()
    }

    @Provides
    @Singleton
    fun provideRatedRoomDb(context: Context): RatedPhotoDatabase {
        return Room.databaseBuilder(context, RatedPhotoDatabase::class.java, "photos").build()
    }

    @Provides
    @Singleton
    fun provideLabelsDb(context: Context): LabelsDatabase {
        return Room.databaseBuilder(context, LabelsDatabase::class.java, "labels").build()
    }


    @Provides
    @Singleton
    fun provideRoomDb(context: Context): PhotoDatabase {
        return Room.databaseBuilder(context, PhotoDatabase::class.java, "photos").build()
    }


    @Provides
    @Singleton
    fun provideNegativeDao(photoDatabase: PhotoDatabase): NegativeDao {
        return photoDatabase.negativeDao()
    }

    @Provides
    @Singleton
    fun providePositiveDao(photoDatabase: PhotoDatabase): PositiveDao {
        return photoDatabase.positiveDao()
    }

    @Provides
    @Singleton
    fun provideRatedPhotoDao(candidatePhotoDatabase: CandidatePhotoDatabase): PhotoDao {
        return candidatePhotoDatabase.photoDao()
    }

    @Provides
    @Singleton
    fun providePhotoDao(ratedPhotoDatabase: RatedPhotoDatabase): SaveModelDao {
        return ratedPhotoDatabase.ratedPhotoDao()
    }

    @Provides
    @Singleton
    fun provideLabelDao(labelsDatabase: LabelsDatabase): LabelDao {
        return labelsDatabase.labelDao()
    }

}