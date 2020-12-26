package dk.nodes.template.injection.modules

import android.content.Context
import android.preference.PreferenceManager
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.coroutineScope
import dagger.Binds
import dagger.Module
import dagger.Provides
import dk.nodes.template.App
import dk.nodes.template.data.injection.ProcessLifetime
import dk.nodes.template.inititializers.AppInitializer
import dk.nodes.template.inititializers.AppInitializerImpl
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@Module
abstract class AppModule {

    @Binds
    abstract fun bindAppInitalizer(initializer: AppInitializerImpl): AppInitializer

    @Module
    companion object {
        @JvmStatic
        @Provides
        fun provideContext(application: App): Context = application.applicationContext


        @JvmStatic
        @Provides
        @Singleton
        fun provideSharedPreferences(context: Context) = PreferenceManager.getDefaultSharedPreferences(context)

        @JvmStatic
        @Provides
        @ProcessLifetime
        fun provideProcessScope(): CoroutineScope {
            return ProcessLifecycleOwner.get().lifecycle.coroutineScope
        }
    }
}