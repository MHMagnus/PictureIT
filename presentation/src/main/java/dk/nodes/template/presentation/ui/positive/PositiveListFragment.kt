package dk.nodes.template.presentation.ui.positive

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
import dk.nodes.template.presentation.util.GridItemDecoration
import dk.nodes.template.presentation.ui.picture_detail.PictureDetailFragment
import dk.nodes.template.presentation.util.fadeOutAndHide
import kotlinx.android.synthetic.main.fragment_positive_list.*
import kotlinx.android.synthetic.main.fragment_positive_list.noResultsView

class PositiveListFragment : BaseFragment() {
    private val viewModel by viewModel<PositiveListViewModel>()
    private lateinit var adapter: PositiveListAdapter
    private var itemTouchHelper: ItemTouchHelper? = null
    private val swipeTreshhold = .1f
    private var imagesOnScreenList: MutableList<Photo> = ArrayList()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_positive_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        viewModel.viewState.observeNonNull(this) { state ->
            imagesOnScreenList = viewModel.getListOfPositivePhotos()
            showPositivePhotos(state)
        }
    }


    private fun initView() {
        positive_recycler_view.layoutManager = GridLayoutManager(context, 2)
        positive_recycler_view.addItemDecoration(GridItemDecoration(8, 2))
        adapter = PositiveListAdapter().also(positive_recycler_view::setAdapter)
        setItemTouchListener()
        setupListener()
    }

    private fun setupListener() {
        adapter.photoSelectedClickListener = { photo ->
            enlargePhoto(photo)
        }
    }

    private fun showPositivePhotos(state: PositiveListViewState) {
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
                ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT + ItemTouchHelper.RIGHT) {
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
                        removePositivePhoto(viewHolder)
                    }
                    ItemTouchHelper.LEFT -> {
                        removePositivePhoto(viewHolder)
                    }
                    else -> {
                        adapter.notifyItemChanged(viewHolder.adapterPosition)
                    }
                }
            }
        }
        itemTouchHelper = ItemTouchHelper(itemTouchCallback)
        itemTouchHelper?.attachToRecyclerView(positive_recycler_view)
    }


    private fun enlargePhoto(photo: Photo) {
        viewModel.addPhotoPathToPrefManager(photo.photoPath)
        setFragment(PictureDetailFragment())
    }


    private fun removePositivePhoto(viewHolder: RecyclerView.ViewHolder) {
        viewHolder.itemView.fadeOutAndHide()
        imagesOnScreenList = viewModel.getListOfPositivePhotos()
        val imagePath = imagesOnScreenList[viewHolder.adapterPosition]
        viewModel.removePositivePhoto(imagePath.photoPath)
        imagesOnScreenList.removeAt(viewHolder.adapterPosition)
        if (imagesOnScreenList.size < 1) {
                noResultsView.isVisible = true
        }
        (activity as MainActivity).generateModels()
        adapter.setData(imagesOnScreenList)
    }


}