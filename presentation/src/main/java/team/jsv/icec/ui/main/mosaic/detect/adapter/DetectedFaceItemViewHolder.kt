package team.jsv.icec.ui.main.mosaic.detect.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import team.jsv.icec.ui.main.mosaic.detect.model.DetectFaceInfoViewItem
import team.jsv.icec.util.loadImage
import team.jsv.presentation.databinding.ViewholderDetectedFaceBinding

class DetectedFaceItemViewHolder(
    private val binding: ViewholderDetectedFaceBinding,
    private val listener: DetectedFaceListener
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: DetectFaceInfoViewItem) {
        binding.ivFace.loadImage(item.url)

        if (item.selected) {
            binding.ivCheckingBackground.visibility = View.VISIBLE
            binding.ivCheck.visibility = View.VISIBLE
        } else {
            binding.ivCheckingBackground.visibility = View.GONE
            binding.ivCheck.visibility = View.GONE
        }

        binding.item.setOnClickListener {
            listener.onItemClick(adapterPosition)
        }
    }
}