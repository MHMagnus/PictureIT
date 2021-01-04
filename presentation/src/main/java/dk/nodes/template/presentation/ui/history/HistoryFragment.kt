package dk.nodes.template.presentation.ui.history

import android.database.Cursor
import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.MediaStore
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
import dk.nodes.template.presentation.util.GridItemDecoration
import dk.nodes.template.presentation.util.VectorLab
import kotlinx.android.synthetic.main.fragment_history.*
import kotlinx.android.synthetic.main.fragment_positive_list.*

class HistoryFragment : BaseFragment() {
    private val viewModel by viewModel<HistoryViewModel>()
    private lateinit var adapter: HistoryAdapter
    private var itemTouchHelper: ItemTouchHelper? = null
    private val swipeTreshhold = .1f
    private val projection = arrayOf(MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA)
    private var imagesOnHomeScreen: MutableList<Photo> = ArrayList()
    private var cursor: Cursor? = null
    private var vectorLab: VectorLab? = null

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    private fun setCursor() {
        cursor = activity?.contentResolver?.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                MediaStore.Images.Media._ID
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).setGenerateModelsRunning(false)
        vectorLab = VectorLab[requireActivity()]
        setCursor()
        initView()
        viewModel.viewState.observeNonNull(this) { state ->
            showHistoryPhotos(state)
        }
    }

    private fun initView() {
        picturesHistoryRv.layoutManager = GridLayoutManager(context, 2)
        picturesHistoryRv.addItemDecoration(GridItemDecoration(8, 2))
        adapter = HistoryAdapter().also(picturesHistoryRv::setAdapter)
        picturesHistoryRv.isNestedScrollingEnabled = false
        setupAdapter()
        swipeRefreshLayout.setOnRefreshListener {
            showPhotos()
        }
    }

    private fun showHistoryPhotos(state: HistoryViewState) {
        var historyPhotos = state.list
        for (p in historyPhotos) {
            if (p.classifier != 77)
                historyPhotos.remove(p)
        }
        imagesOnHomeScreen = historyPhotos
        adapter.setData(imagesOnHomeScreen)
        adapter.notifyDataSetChanged()
    }

    private fun setupAdapter() {
        adapter.photoSelectedClickListener = { photo ->
            enlargePhoto(photo)
        }
        setItemTouchListener()
    }

    private fun showPhotos() {
        fragmentPictures.background = null
        val list: MutableList<Photo> = mutableListOf()
        addNewPhotosToFeed(list)
        setupAdapter()
        adapter.notifyDataSetChanged()
        swipeRefreshLayout.isRefreshing = false
    }

    private fun addNewPhotosToFeed(list: MutableList<Photo>) {
        val temp = viewModel.randomPicturesFromDb(vectorLab, cursor)
        for (s in temp) {
            list.add(element = Photo(s.toString(), 77))
        }
        viewModel.saveListOfPhotosToRoom(list)
        list.addAll(imagesOnHomeScreen)
        imagesOnHomeScreen = list

        adapter.setData(list)
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
                    }
                    ItemTouchHelper.LEFT -> {
                        (activity as MainActivity).animateAddOneToNegative()
                        addToNegativeList(viewHolder)
                    }
                }
                adapter.notifyItemChanged(viewHolder.adapterPosition)
            }

            override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
                val itemView = viewHolder.itemView

                if (dX > 0) {
                    val backgroundLeft = ColorDrawable(resources.getColor(R.color.testColor2))
                    backgroundLeft.setBounds(0, itemView.top, itemView.right + dX.toInt(), itemView.bottom)
                    backgroundLeft.draw(c)
                } else {
                    val backgroundRight = ColorDrawable(resources.getColor(R.color.testColor5))
                    backgroundRight.setBounds(itemView.right, itemView.top, itemView.right + dX.toInt(), itemView.bottom)
                    backgroundRight.draw(c)
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }
        itemTouchHelper = ItemTouchHelper(itemTouchCallback)
        itemTouchHelper?.attachToRecyclerView(picturesHistoryRv)
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