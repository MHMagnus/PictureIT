package dk.nodes.template.presentation.ui.results

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import dk.nodes.template.presentation.injection.ViewModelKey

@Module
abstract class ResultsBuilder {

    @ContributesAndroidInjector
    abstract fun resultsFragment(): ResultsFragment

    @Binds
    @IntoMap
    @ViewModelKey(ResultsViewModel::class)
    internal abstract fun bindSampleViewModel(viewModel: ResultsViewModel): ViewModel
}