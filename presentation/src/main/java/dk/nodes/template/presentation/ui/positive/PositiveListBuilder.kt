package dk.nodes.template.presentation.ui.positive

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import dk.nodes.template.presentation.injection.ViewModelKey

@Module
abstract class PositiveListBuilder {

    @ContributesAndroidInjector
    abstract fun positiveFragment(): PositiveListFragment

    @Binds
    @IntoMap
    @ViewModelKey(PositiveListViewModel::class)
    internal abstract fun bindSampleViewModel(viewModel: PositiveListViewModel): ViewModel
}