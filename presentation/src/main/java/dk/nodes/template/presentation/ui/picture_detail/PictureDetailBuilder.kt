package dk.nodes.template.presentation.ui.picture_detail

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import dk.nodes.template.presentation.injection.ViewModelKey

@Module
abstract class PictureDetailBuilder {

    @ContributesAndroidInjector
    abstract fun pictureFragment(): PictureDetailFragment

    @Binds
    @IntoMap
    @ViewModelKey(PictureDetailViewModel::class)
    internal abstract fun bindSampleViewModel(viewModel: PictureDetailViewModel): ViewModel
}