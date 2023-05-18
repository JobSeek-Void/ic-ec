package team.jsv.icec.ui.main.start.adapter

import androidx.recyclerview.widget.RecyclerView
import team.jsv.icec.ui.main.start.model.GalleryImageItem
import team.jsv.icec.util.loadImage
import team.jsv.presentation.databinding.ViewholderGalleryImageBinding


class GalleryImageItemViewHolder(
    private val binding: ViewholderGalleryImageBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: GalleryImageItem) {
        binding.data = item
        binding.ivGalleryImage.loadImage(item.url)
    }
}