package dk.nodes.template.presentation.ui.home

import android.database.Cursor
import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import dk.nodes.template.domain.entities.Photo
import dk.nodes.template.presentation.R
import dk.nodes.template.presentation.ui.base.BaseFragment
import dk.nodes.template.presentation.ui.main.MainActivity
import dk.nodes.template.presentation.ui.picture_detail.PictureDetailFragment
import dk.nodes.template.presentation.util.GridItemDecoration
import dk.nodes.template.presentation.util.VectorLab
import dk.nodes.template.presentation.util.VectorLab.Companion.get
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment
    : BaseFragment() {
    private var imagesOnHomeScreen: MutableList<String?> = ArrayList()
    private val viewModel by viewModel<HomeViewModel>()
    private lateinit var adapter: HomeAdapter
    private val projection = arrayOf(MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA)
    private var cursor: Cursor? = null
    private var vectorLab: VectorLab? = null
    private val swipeTreshhold = 0.1f
    private var itemTouchHelper: ItemTouchHelper? = null


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
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
        vectorLab = get(requireActivity())
        setCursor()
        initView()
        showPhotos()
    }

    companion object {
        @JvmField
        var NUMBEROFHIGHESTPROBS = 5
    }

    private fun initView() {
        picturesRv.layoutManager = GridLayoutManager(context, 2)
        picturesRv.addItemDecoration(GridItemDecoration(8, 2))
        picturesRv.isNestedScrollingEnabled = false
        adapter = HomeAdapter().also(picturesRv::setAdapter)
        setupAdapter()

    }

     private fun showPhotos() {
        val list: MutableList<Photo> = mutableListOf()
            setListOfRandomImages(list)
            setupAdapter()
            adapter.notifyDataSetChanged()
    }

     private fun setListOfRandomImages(list: MutableList<Photo>) {
        imagesOnHomeScreen.clear()
        imagesOnHomeScreen = viewModel.randomPicturesFromDb(vectorLab, cursor)
        for (s in imagesOnHomeScreen) {
            list.add(element = Photo(s.toString(), 77))
        }
        viewModel.saveListOfPhotosToRoom(list)

        adapter.setData(list)
    }

    private fun setupAdapter() {
        adapter.photoSelectedClickListener = { photo ->
            enlargePhoto(photo)
        }
        setItemTouchListener()
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
            override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
                val itemView = viewHolder.itemView

                if (dX > 0) {
                    val backgroundLeft = ColorDrawable(resources.getColor(if (itemView.right * swipeTreshhold < dX) R.color.testColor2 else R.color.lightGrey))
                    backgroundLeft.setBounds(0, itemView.top, itemView.right + dX.toInt(), itemView.bottom)
                    backgroundLeft.draw(c)
                } else {
                    val backgroundRight = ColorDrawable(resources.getColor(if (itemView.right * swipeTreshhold < (dX * -1)) R.color.testColor5 else R.color.lightGrey))
                    backgroundRight.setBounds(itemView.right, itemView.top, itemView.right + dX.toInt(), itemView.bottom)
                    backgroundRight.draw(c)
                }

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }
        itemTouchHelper = ItemTouchHelper(itemTouchCallback)
        itemTouchHelper?.attachToRecyclerView(picturesRv)
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
        return imagesOnHomeScreen[viewHolder.adapterPosition]
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

