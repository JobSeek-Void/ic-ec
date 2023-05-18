package team.jsv.icec.ui.main.start.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import team.jsv.icec.ui.main.start.model.GalleryImageItem
import team.jsv.presentation.databinding.ViewholderGalleryImageBinding

class GalleryImageAdapter :
    ListAdapter<GalleryImageItem, GalleryImageItemViewHolder>(GalleryImageDiffUtil) {

    companion object {
        val GalleryImageDiffUtil = object : DiffUtil.ItemCallback<GalleryImageItem>() {
            override fun areItemsTheSame(
                oldItem: GalleryImageItem,
                newItem: GalleryImageItem
            ): Boolean {
                return oldItem.url == newItem.url
            }

            override fun areContentsTheSame(
                oldItem: GalleryImageItem,
                newItem: GalleryImageItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    //object GalleryImageDiffUtil : DiffUtil.ItemCallback<GalleryImageItem>() {
//        override fun areItemsTheSame(
//            oldItem: GalleryImageItem,
//            newItem: GalleryImageItem
//        ): Boolean {
//            return oldItem.url == newItem.url
//        }
//
//        override fun areContentsTheSame(
//            oldItem: GalleryImageItem,
//            newItem: GalleryImageItem
//        ): Boolean {
//            return oldItem == newItem
//        }
//    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryImageItemViewHolder {
        val binding = ViewholderGalleryImageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return GalleryImageItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GalleryImageItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    override fun submitList(list: MutableList<GalleryImageItem>?) {
        super.submitList(list)
    }
}