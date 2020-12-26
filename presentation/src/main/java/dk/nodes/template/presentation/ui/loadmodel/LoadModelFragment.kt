package dk.nodes.template.presentation.ui.loadmodel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import dk.nodes.template.presentation.R
import dk.nodes.template.presentation.extensions.observeNonNull
import dk.nodes.template.presentation.ui.base.BaseFragment
import dk.nodes.template.presentation.ui.main.MainActivity
import dk.nodes.template.presentation.ui.savemodel.SaveModelFragment
import kotlinx.android.synthetic.main.fragment_load_model.*

class LoadModelFragment : BaseFragment() {
    private val viewModel by viewModel<LoadModelViewModel>()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_load_model, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.viewState.observeNonNull(this) { state ->
            setupImageForModelFour(state)
            setupImageForModelThree(state)
            setupImageForModelTwo(state)
            setupImageForModelOne(state)
        }
        setupListeners()
    }


    private fun setupImageForModelOne(state: LoadModelViewState) {
        val temp = viewModel.getPictureFromList(state.listOneFromRoom)
        if (temp.length >3)
        Glide.with(loadModelOne)
                .load(temp)
                .centerCrop()
                .into(loadModelOne)
    }

    private fun setupImageForModelTwo(state: LoadModelViewState) {
        val temp = viewModel.getPictureFromList(state.listTwoFromRoom)
        if (temp.length >3)
        Glide.with(loadModelTwo)
                .load(temp)
                .centerCrop()
                .into(loadModelTwo)
    }

    private fun setupImageForModelThree(state: LoadModelViewState) {
        val temp = viewModel.getPictureFromList(state.listThreeFromRoom)
        if (temp.length >3)
        Glide.with(loadModelThree)
                .load(temp)
                .centerCrop()
                .into(loadModelThree)
    }

    private fun setupImageForModelFour(state: LoadModelViewState) {
        val temp = viewModel.getPictureFromList(state.listFourFromRoom)
        if (temp.length >3)
        Glide.with(loadModelFour)
                .load(temp)
                .centerCrop()
                .into(loadModelFour)
    }

    private fun setupListeners() {
        setTexts()

        saveModelBtn?.setOnClickListener {
            it.setBackgroundColor(resources.getColor(R.color.blue))
            setFragment(SaveModelFragment())
        }

        loadModelOne?.setOnClickListener {
            loadModelOne()
            loadModelOne.setImageResource(R.drawable.ic_icon_m_search_blue)
        }

        loadModelTwo?.setOnClickListener {
            loadModelTwo()
            loadModelTwo.setImageResource(R.drawable.ic_icon_m_search_blue)
        }

        loadModelThree?.setOnClickListener {
            loadModelThree()
            loadModelThree.setImageResource(R.drawable.ic_icon_m_search_blue)
        }

        loadModelFour?.setOnClickListener {
            loadModelFour()
            loadModelFour.setImageResource(R.drawable.ic_icon_m_search_blue)
        }
    }

    private fun setTexts() {
        loadModel1Et.text = viewModel.loadModelOneName()

        loadModel2Et.text = viewModel.loadModelTwoName()

        loadModel3Et.text = viewModel.loadModelThreeName()

        loadModel4Et.text = viewModel.loadModelFourName()
    }

    private fun loadModelOne() {
       // showLoadDialog()
        viewModel.loadModelOne()
        (activity as MainActivity).generateModels()
    }


    private fun loadModelTwo() {
       //  showLoadDialog()
        viewModel.loadModelTwo()
        (activity as MainActivity).generateModels()
    }

    private fun loadModelThree() {
       // showLoadDialog()
        viewModel.loadModelThree()
        (activity as MainActivity).generateModels()
    }

    private fun loadModelFour() {
       // showLoadDialog()
        viewModel.loadModelFour()
        (activity as MainActivity).generateModels()
    }

/*    private fun showLoadDialog() {
        AlertDialog.Builder(requireContext())
                .setTitle("Load Model")
                .setMessage("You have loaded a model, see the photos added to Positives and Negatives and the Result in Results.")
                .setPositiveButton("ok") { dialog, _ ->
                }
                .show()
    }*/
}



