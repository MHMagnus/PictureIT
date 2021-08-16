package dk.nodes.template.presentation.ui.results

import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import dk.nodes.template.presentation.ui.savemodel.SaveModelFragment
import dk.nodes.template.presentation.util.GridItemDecoration
import kotlinx.android.synthetic.main.fragment_negative_list.*
import kotlinx.android.synthetic.main.fragment_results.*
import kotlinx.android.synthetic.main.fragment_results.noResultsView
import kotlinx.android.synthetic.main.row_pictures.*
import kotlinx.android.synthetic.main.row_pictures_results.*
import timber.log.Timber

class ResultsFragment : BaseFragment() {
    private val viewModel by viewModel<ResultsViewModel>()
    private lateinit var adapter: ResultsAdapter
    private var itemTouchHelper: ItemTouchHelper? = null
    private val swipeTreshhold = .1f
    private var imagesOnHomeScreen: MutableList<Photo> = ArrayList()
    var positivesList = mutableListOf<Photo>()
    var negativesList = mutableListOf<Photo>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        return inflater.inflate(R.layout.fragment_results, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.viewState.observeNonNull(this) { state ->
            initView()
            showPhotos(state)
        }
    }

    private fun initView() {
        picturesResultsRv.layoutManager = GridLayoutManager(context, 2)
        picturesResultsRv.addItemDecoration(GridItemDecoration(4, 2))
        picturesResultsRv.isNestedScrollingEnabled = false
        adapter = ResultsAdapter().also(picturesResultsRv::setAdapter)
        swipeRefreshLayout?.setOnRefreshListener {
            setFragment(ResultsFragment())
        }
    }

    private fun setupAdapter() {
        adapter.photoSelectedClickListener = { photo ->
            enlargePhoto(photo)
        }
        setItemTouchListener()
    }

    private fun showPhotos(state: ResultsViewState) {
            imagesOnHomeScreen = state.list.asReversed()
            adapter.setData(state.list.asReversed(), positivesList, negativesList)
        if (state.list.size > 0) {
            noResultsView?.visibility = View.GONE
            setupAdapter()
            adapter.notifyDataSetChanged()
        }
    }

    private fun enlargePhoto(photo: Photo) {
        viewModel.addPhotoPathToPrefManager(photo.photoPath)
        setFragment(PictureDetailFragment())
    }

    private fun setItemTouchListener() {
        val itemTouchCallback = object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT + ItemTouchHelper.LEFT) {
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
                        (activity as MainActivity).animateAddOneToPositive()
                        addToPositiveList(viewHolder)
                        adapter.notifyItemChanged(viewHolder.adapterPosition)
                    }
                    ItemTouchHelper.LEFT -> {
                        (activity as MainActivity).animateAddOneToNegative()
                        addToNegativeList(viewHolder)
                        adapter.notifyItemChanged(viewHolder.adapterPosition)
                    }
                    else -> {
                        adapter.notifyItemChanged(viewHolder.adapterPosition)
                    }
                }
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                val itemView = viewHolder.itemView

                if (dX > 0) {
                    val backgroundLeft =
                        ColorDrawable(resources.getColor(if (itemView.right * swipeTreshhold < dX) R.color.testColor2 else R.color.lightGrey))
                    backgroundLeft.setBounds(0, itemView.top, itemView.right + dX.toInt(), itemView.bottom)
                    backgroundLeft.draw(c)
                } else {
                    val backgroundRight =
                        ColorDrawable(resources.getColor(if (itemView.right * swipeTreshhold < (dX * -1)) R.color.testColor5 else R.color.lightGrey))
                    backgroundRight.setBounds(itemView.right, itemView.top, itemView.right + dX.toInt(), itemView.bottom)
                    backgroundRight.draw(c)
                }

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }
        itemTouchHelper = ItemTouchHelper(itemTouchCallback)
        itemTouchHelper?.attachToRecyclerView(picturesResultsRv)
    }


    private fun addToPositiveList(viewHolder: RecyclerView.ViewHolder) {
        Toast.makeText(requireContext(), "You Have Added a Positive Photo", Toast.LENGTH_SHORT).show()
        val getStringFromViewHolder = getStringFromAdapterPosition(viewHolder)
        val photo = Photo(getStringFromViewHolder.toString(), 1)
        viewModel.addPositivePhoto(photo)
        (activity as MainActivity).animatePositiveBackToNormal()
        (activity as MainActivity).generateModels()
    }

    private fun getStringFromAdapterPosition(viewHolder: RecyclerView.ViewHolder): String? {
        return imagesOnHomeScreen[viewHolder.adapterPosition].photoPath
    }

    private fun addToNegativeList(viewHolder: RecyclerView.ViewHolder) {
        Toast.makeText(requireContext(), "You Have Added a Negative Photo", Toast.LENGTH_SHORT).show()
        val getPhotoFromPosition = getStringFromAdapterPosition(viewHolder)
        val photo = Photo(getPhotoFromPosition.toString(), 2)
        viewModel.addNegativePhoto(photo)
        (activity as MainActivity).animateNegativeBackToNormal()
        (activity as MainActivity).generateModels()

    }

}