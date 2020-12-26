package dk.nodes.template.presentation.ui.savemodel

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import dk.nodes.template.presentation.injection.ViewModelKey
import dk.nodes.template.presentation.ui.savemodel.SaveModelFragment
import dk.nodes.template.presentation.ui.savemodel.SaveModelViewModel

@Module
abstract class SaveModelBuilder {

    @ContributesAndroidInjector
    abstract fun saveModelFragment(): SaveModelFragment

    @Binds
    @IntoMap
    @ViewModelKey(SaveModelViewModel::class)
    internal abstract fun bindSampleViewModel(viewModel: SaveModelViewModel): ViewModel
}