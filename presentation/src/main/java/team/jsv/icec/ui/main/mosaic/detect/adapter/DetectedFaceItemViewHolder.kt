package team.jsv.icec.ui.main.mosaic.detect.adapter

import androidx.recyclerview.widget.RecyclerView
import team.jsv.icec.ui.main.mosaic.detect.model.DetectFaceInfoViewItem
import team.jsv.icec.util.loadImage
import team.jsv.presentation.R
import team.jsv.presentation.databinding.ViewholderDetectedFaceBinding

class DetectedFaceItemViewHolder(
    private val binding: ViewholderDetectedFaceBinding,
    private val onClick: (Int) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: DetectFaceInfoViewItem) {
        binding.data = item
        binding.ivFace.loadImage(item.url)
        binding.ivCheck.loadImage(R.drawable.ic_select_mark_14)

        binding.item.setOnClickListener {
            onClick(adapterPosition)
        }
    }
}