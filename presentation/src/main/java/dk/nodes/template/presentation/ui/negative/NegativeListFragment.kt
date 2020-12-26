package dk.nodes.template.presentation.ui.negative

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import dk.nodes.template.domain.entities.Photo
import dk.nodes.template.presentation.R
import dk.nodes.template.presentation.extensions.observeNonNull
import dk.nodes.template.presentation.ui.base.BaseFragment
import dk.nodes.template.presentation.ui.main.MainActivity
import dk.nodes.template.presentation.ui.picture_detail.PictureDetailFragment
import dk.nodes.template.presentation.util.GridItemDecoration
import dk.nodes.template.presentation.util.fadeOutAndHide
import kotlinx.android.synthetic.main.fragment_negative_list.*
import kotlinx.android.synthetic.main.fragment_negative_list.noResultsView

class NegativeListFragment : BaseFragment() {
    private val viewModel by viewModel<NegativeListViewModel>()
    private lateinit var adapter: NegativeListAdapter
    private var itemTouchHelper: ItemTouchHelper? = null
    private val swipeTreshhold = .1f
    private var imagesOnScreenList: MutableList<Photo> =
            ArrayList()


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_negative_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        viewModel.viewState.observeNonNull(this) { state ->
            showNegativePhotos(state)
            imagesOnScreenList = viewModel.getListOfNegativePhotos()
        }
    }

    private fun setupListener() {
        adapter.photoSelectedClickListener = { photo ->
            enlargePhoto(photo)
        }
    }

    private fun initView() {
        negativePicturesRv.layoutManager = GridLayoutManager(context, 2)
        negativePicturesRv.addItemDecoration(GridItemDecoration(8, 2))
        adapter = NegativeListAdapter().also(negativePicturesRv::setAdapter)
        setItemTouchListener()
        setupListener()
    }

    private fun showNegativePhotos(state: NegativeListViewState) {
        if (state.list.size > 0) {
            noResultsView.isVisible = false
            imagesOnScreenList = state.list
            adapter.setData(state.list)
            adapter.notifyDataSetChanged()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchPictures()
    }


    private fun setItemTouchListener() {
        val itemTouchCallback = object :
                ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT  + ItemTouchHelper.LEFT ) {
            override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    viewHolder1: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
                return swipeTreshhold
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
                when (swipeDir) {
                    ItemTouchHelper.RIGHT -> {
                        removeNegativePhoto(viewHolder)
                    }
                    ItemTouchHelper.LEFT -> {
                        removeNegativePhoto(viewHolder)
                    }
                    else -> {
                        adapter.notifyItemChanged(viewHolder.adapterPosition)
                    }
                }
            }
        }
        itemTouchHelper = ItemTouchHelper(itemTouchCallback)
        itemTouchHelper?.attachToRecyclerView(negativePicturesRv)
    }

    private fun enlargePhoto(photo: Photo) {
        viewModel.addPhotoPathToPrefManager(photo.photoPath)
        setFragment(PictureDetailFragment())
    }




    private fun removeNegativePhoto(viewHolder: RecyclerView.ViewHolder) {
        viewHolder.itemView.fadeOutAndHide()
        imagesOnScreenList = viewModel.getListOfNegativePhotos()
        val imagePath = imagesOnScreenList[viewHolder.adapterPosition]
        viewModel.removeNegativePhoto(imagePath.photoPath)
        imagesOnScreenList.removeAt(viewHolder.adapterPosition)
        if (imagesOnScreenList.size < 1) {
            noResultsView.isVisible = true
        }
        (activity as MainActivity).generateModels()
        adapter.setData(imagesOnScreenList)
    }

}