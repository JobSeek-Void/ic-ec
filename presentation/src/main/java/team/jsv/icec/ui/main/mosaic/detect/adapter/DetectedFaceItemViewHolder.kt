package team.jsv.icec.ui.main.mosaic.detect.adapter

import androidx.recyclerview.widget.RecyclerView
import team.jsv.icec.ui.main.mosaic.detect.model.DetectFaceInfoViewItem
import team.jsv.icec.util.gone
import team.jsv.icec.util.loadImage
import team.jsv.icec.util.visible
import team.jsv.presentation.databinding.ViewholderDetectedFaceBinding

class DetectedFaceItemViewHolder(
    private val binding: ViewholderDetectedFaceBinding,
    private val listener: DetectedFaceListener
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: DetectFaceInfoViewItem) {
        binding.ivFace.loadImage(item.url)

        if (item.selected) {
            binding.ivCheckingBackground.visible()
            binding.ivCheck.visible()
        } else {
            binding.ivCheckingBackground.gone()
            binding.ivCheck.gone()
        }

        binding.item.setOnClickListener {
            listener.onItemClick(adapterPosition)
        }
    }
}