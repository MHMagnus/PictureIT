package dk.nodes.template.presentation.ui.negative

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import dk.nodes.template.presentation.injection.ViewModelKey

@Module
abstract class NegativeListBuilder {

    @ContributesAndroidInjector
    abstract fun negativeListFragment(): NegativeListFragment

    @Binds
    @IntoMap
    @ViewModelKey(NegativeListViewModel::class)
    internal abstract fun bindSampleViewModel(viewModel: NegativeListViewModel): ViewModel
}