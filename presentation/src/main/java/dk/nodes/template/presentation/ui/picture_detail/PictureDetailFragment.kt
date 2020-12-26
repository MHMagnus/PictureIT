package dk.nodes.template.presentation.ui.picture_detail


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import dk.nodes.template.presentation.R
import dk.nodes.template.presentation.extensions.observeNonNull
import dk.nodes.template.presentation.ui.base.BaseFragment
import dk.nodes.template.presentation.util.sharePhotoPicker
import kotlinx.android.synthetic.main.fragment_picture_detail.*


class PictureDetailFragment : BaseFragment() {

    private val viewModel by viewModel<PictureDetailViewModel>()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_picture_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.keepScreenOn = true
        loadPicture(viewModel.getPathFromPrefManager().toString())
        viewModel.setUpDataForTextViews(viewModel.getPathFromPrefManager().toString())
        setupListeners()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.viewState.observeNonNull(this) { state ->
            showLabelsTv(state)

        }
    }

    private fun showLabelsTv(state: PictureDetailViewState) {
        var label = ""
        val stateList = state.concatinationOfLabels
        for (l in stateList) {
            label += l
        }
        labelsTv?.text = label
    }


    private fun loadPicture(photoPath: String) {
        Glide.with(photoIv.context)
                .load(photoPath)
                .centerCrop()
                .into(photoIv)

    }


    private fun setupListeners() {
        shareBtn?.setOnClickListener {
            sharePhotoPicker(viewModel.getPathFromPrefManager().toString(), requireContext())
        }
    }

}

