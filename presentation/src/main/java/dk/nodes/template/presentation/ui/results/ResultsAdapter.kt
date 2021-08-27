package dk.nodes.template.presentation.ui.results

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dk.nodes.template.domain.entities.Photo
import dk.nodes.template.presentation.R
import dk.nodes.utils.android.view.inflate
import kotlinx.android.synthetic.main.row_pictures_results.view.*


class ResultsAdapter : RecyclerView.Adapter<ResultsAdapter.ViewHolder>() {
    var photoSelectedClickListener: ((photo: Photo) -> Unit)? = null
    private var list: MutableList<Photo> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.inflate(R.layout.row_pictures_results))
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        list[position].let { holder.bind(position) }
    }

    fun setData(list: MutableList<Photo>) {
        println("this is list $list")
        val diff = DiffUtil.calculateDiff(PostDiff(this.list, list))
        this.list.clear()
        this.list.plusAssign(list)
        diff.dispatchUpdatesTo(this)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(position: Int) {
            val photo: Photo = list[position]
            itemView.run {
                Glide.with(pictureIv.context)
                    .load(photo.photoPath)
                    .centerCrop()
                    .into(pictureIv)
                pictureIv?.setOnClickListener {
                    photoSelectedClickListener?.invoke(photo)
                }
                when (photo.classifier) {
                    1 -> {
                        Glide.with(this.imageStateIv.context)
                            .load(R.drawable.ic_icon_l_plus_green)
                            .centerCrop()
                            .into(imageStateIv)
                    }
                    2 -> {
                        Glide.with(this.imageStateIv.context)
                            .load(R.drawable.ic_action_minus_red)
                            .centerCrop()
                            .into(imageStateIv)
                    }
                    else -> {
                        Glide.with(this.imageStateIv.context)
                            .load(R.drawable.circle_bluegrey)
                            .centerCrop()
                            .into(imageStateIv)
                    }
                }
            }
        }

    }


    private inner class PostDiff(
        private val oldList: MutableList<Photo>,
        private val newList: MutableList<Photo>
    ) : DiffUtil.Callback() {
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return newList[newItemPosition].photoPath == oldList[oldItemPosition].photoPath
        }

        override fun getOldListSize(): Int {
            return oldList.size
        }

        override fun getNewListSize(): Int {
            return newList.size
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return newList[newItemPosition] == oldList[oldItemPosition]
        }
    }
}
