package dk.nodes.template.presentation.ui.savemodel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import dk.nodes.template.presentation.R
import dk.nodes.template.presentation.ui.base.BaseFragment
import dk.nodes.utils.android.fragment.hideKeyboard
import kotlinx.android.synthetic.main.fragment_save_model.*

class SaveModelFragment : BaseFragment() {
    private val viewModel by viewModel<SaveModelViewModel>()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_save_model, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
    }


    private fun setupListeners() {
        savedModel1Et.setText(viewModel.loadModel1Name())
        savedModel2Et.setText(viewModel.loadModel2Name())
        savedModel3Et.setText(viewModel.loadModel3Name())
        savedModel4Et.setText(viewModel.loadModel4Name())

        deleteStoredModelsFromSaveIv?.setOnClickListener {

            AlertDialog.Builder(requireContext())
                    .setTitle("Delete All Models?")
                    .setMessage("Would you like to Delete all models?")
                    .setPositiveButton("Yes") { dialog, _ ->
                        viewModel.removeAllStoredModels()
                        Toast.makeText(requireContext(), "All Stored Models has Been Deleted", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("Cancel") { dialog, which ->
                        return@setNegativeButton
                    }
                    .show()
        }

        savedModel1?.setOnClickListener {
            alertDialogBuilderForSaveModel(22, 21, 1, savedModel1Et.text.toString(), savedModel1)
        }

        savedModel2?.setOnClickListener {
            alertDialogBuilderForSaveModel(32, 31, 2, savedModel2Et.text.toString(), savedModel2)
        }

        savedModel3?.setOnClickListener {
            alertDialogBuilderForSaveModel(42, 41, 3, savedModel3Et.text.toString(), savedModel3)
        }

        savedModel4?.setOnClickListener {
            alertDialogBuilderForSaveModel(52, 51, 4, savedModel4Et.text.toString(), savedModel4)
        }
    }

    private fun alertDialogBuilderForSaveModel(positiveClassifier: Int, negativeClassifier: Int, number: Int, textFromEditText: String, view: ImageView) {
        AlertDialog.Builder(requireContext())
                .setTitle("Save Model")
                .setMessage("Would you like to save the model?")
                .setPositiveButton("Yes") { dialog, _ ->
                    Toast.makeText(requireContext(), "You have successfully stored a model", Toast.LENGTH_SHORT).show()
                    viewModel.saveModelToRoom(positiveClassifier, negativeClassifier)
                    viewModel.addTitleOfSavedModel(number, textFromEditText)
                    hideKeyboard()
                    glideImageIn(view)
                }
                .setNegativeButton("Cancel") { dialog, which ->
                    return@setNegativeButton
                }
                .show()
    }

    private fun glideImageIn(view: ImageView) {
        val imageFromPositiveList = viewModel.getItemFromPositiveList()
        Glide.with(view)
                .load(imageFromPositiveList)
                .centerCrop()
                .into(view)
    }
}



