package dk.nodes.template.presentation.ui.info

import androidx.lifecycle.viewModelScope
import dk.nodes.template.domain.entities.InfoItem
import dk.nodes.template.domain.entities.MenuType
import dk.nodes.template.presentation.R
import dk.nodes.template.presentation.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class InfoActivityViewModel @Inject constructor(

) : BaseViewModel<InfoActivityViewState>(InfoActivityViewState()) {

    fun loadInfo(menuType: MenuType?) {
        when (menuType) {
            MenuType.ABOUT -> showAbout()
            MenuType.NICETOKNOW -> showNiceToKnow()
            MenuType.TERMS -> showTerms()
            else -> showError()
        }
    }

    private fun showAbout() = viewModelScope.launch(Dispatchers.Main) {
        val res =  InfoItem("Guide to retrieve relevant images", "In order to browse or explore your image collection, you have to add at least one image to negative and one to positve. You add an image to Negative by swiping left in Pictures / Results and to add to Positive you swipe right Then you press the scope icon in the bottom navigtation menu.", R.drawable.positive_photo_for_thesis, R.drawable.negative_image_for_thesis, R.drawable.photo_results_for_thesis )
        state = state.copy(data = res, isLoading = false)
    }

    private fun showNiceToKnow() = viewModelScope.launch(Dispatchers.Main) {
        val res =  InfoItem("Guide to store a model", "In the bottomnavigation bar, click on 'Load Model', in the view, click on the floppy disk, press underneath one of the four 'No Image Available' and write a text, when you have written a header click the image over the edit text. An image from your positive list will show when it is done.", R.drawable.save_model_one, R.drawable.save_mode_three, R.drawable.save_model_four)
        state = state.copy(data = res, isLoading = false)
    }

    private fun showTerms() = viewModelScope.launch(Dispatchers.Main) {
        val res =  InfoItem("Guide to load a store model", "In the bottomNavigation, click on 'Load Model' the click either of the four icons if an image is shown. You will now be able to see the images needed to recreate the model in Positive and Negative. You can build on and save as you will.", R.drawable.load_stored_model_one, R.drawable.load_stored_model_two, null)
        state = state.copy(data = res, isLoading = false)
    }

    private fun showError() {
        state = state.copy(data = null, isLoading = false)
    }

}