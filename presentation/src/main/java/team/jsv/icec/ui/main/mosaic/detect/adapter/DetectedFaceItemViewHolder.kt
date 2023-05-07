package team.jsv.icec.ui.main.mosaic.detect.adapter

import androidx.recyclerview.widget.RecyclerView
import team.jsv.icec.ui.main.mosaic.detect.model.DetectFaceInfoViewItem
import team.jsv.icec.util.dp
import team.jsv.icec.util.gone
import team.jsv.icec.util.loadImage
import team.jsv.icec.util.setStokeColor
import team.jsv.icec.util.visible
import team.jsv.presentation.R
import team.jsv.presentation.databinding.ViewholderDetectedFaceBinding

class DetectedFaceItemViewHolder(
    private val binding: ViewholderDetectedFaceBinding,
    private val onClick: (Int) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: DetectFaceInfoViewItem) {
        binding.ivFace.loadImage(item.url)
        binding.ivCheck.loadImage(R.drawable.ic_select_mark_14)

        if (item.selected) {
            binding.ivCheck.visible()
            binding.ivFace.apply {
                strokeWidth = 2.dp
                setStokeColor(R.color.SubColor)
            }
        } else {
            binding.ivCheck.gone()
            binding.ivFace.apply {
                setStokeColor(R.color.transparent)
            }
        }

        binding.item.setOnClickListener {
            onClick(adapterPosition)
        }
    }
}