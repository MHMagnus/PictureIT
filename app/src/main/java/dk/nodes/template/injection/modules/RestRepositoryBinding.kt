package dk.nodes.template.injection.modules

import dagger.Binds
import dagger.Module
import dk.nodes.template.data.models.network.PhotoRepositoryImpl
import dk.nodes.template.domain.repositories.PhotoRepository
import javax.inject.Singleton

@Module
abstract class RestRepositoryBinding{
    @Binds
    @Singleton
    abstract fun bindPostRepository(photoRepositoryImpl: PhotoRepositoryImpl): PhotoRepository
}

