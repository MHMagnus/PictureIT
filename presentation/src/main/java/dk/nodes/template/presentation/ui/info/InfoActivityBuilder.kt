package dk.nodes.template.presentation.ui.info

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import dk.nodes.template.presentation.injection.ViewModelKey


@Module
internal abstract class InfoActivityBuilder {

    @Binds
    @IntoMap
    @ViewModelKey(InfoActivityViewModel::class)
    abstract fun bindInfoActivtyViewMode(viewModel: InfoActivityViewModel): ViewModel

    @ContributesAndroidInjector(
            modules = []
    )
    internal abstract fun infoActivity(): InfoActivity
}