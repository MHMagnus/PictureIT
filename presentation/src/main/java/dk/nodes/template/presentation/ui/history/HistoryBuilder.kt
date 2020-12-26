package dk.nodes.template.presentation.ui.history

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import dk.nodes.template.presentation.injection.ViewModelKey

@Module
abstract class HistoryBuilder {

    @ContributesAndroidInjector
    abstract fun historyFragment(): HistoryFragment

    @Binds
    @IntoMap
    @ViewModelKey(HistoryViewModel::class)
    internal abstract fun bindSampleViewModel(viewModel: HistoryViewModel): ViewModel
}