package dk.nodes.template.presentation.ui.loadmodel

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import dk.nodes.template.presentation.injection.ViewModelKey
import dk.nodes.template.presentation.ui.savemodel.SaveModelFragment
import dk.nodes.template.presentation.ui.savemodel.SaveModelViewModel

@Module
abstract class LoadModelBuilder {

    @ContributesAndroidInjector
    abstract fun loadModelFragment(): LoadModelFragment

    @Binds
    @IntoMap
    @ViewModelKey(LoadModelViewModel::class)
    internal abstract fun bindSampleViewModel(viewModel: LoadModelViewModel): ViewModel
}