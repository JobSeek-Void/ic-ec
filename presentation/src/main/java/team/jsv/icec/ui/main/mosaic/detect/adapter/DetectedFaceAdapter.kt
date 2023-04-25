package team.jsv.icec.ui.main.mosaic.detect.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import team.jsv.icec.ui.main.mosaic.detect.model.DetectFaceInfoViewItem
import team.jsv.presentation.databinding.ViewholderDetectedFaceBinding

class DetectedFaceAdapter(private val listener: DetectedFaceListener) :
    ListAdapter<DetectFaceInfoViewItem, DetectedFaceItemViewHolder>(DetectedFaceDiffUtil()) {

    private class DetectedFaceDiffUtil : DiffUtil.ItemCallback<DetectFaceInfoViewItem>() {
        override fun areItemsTheSame(oldItem: DetectFaceInfoViewItem, newItem: DetectFaceInfoViewItem): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: DetectFaceInfoViewItem, newItem: DetectFaceInfoViewItem): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetectedFaceItemViewHolder {
        val binding = ViewholderDetectedFaceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DetectedFaceItemViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: DetectedFaceItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}
