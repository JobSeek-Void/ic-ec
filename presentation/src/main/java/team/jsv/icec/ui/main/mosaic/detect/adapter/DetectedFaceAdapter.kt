package team.jsv.icec.ui.main.mosaic.detect.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import team.jsv.icec.ui.main.mosaic.detect.model.DetectFaceInfoViewItem
import team.jsv.presentation.databinding.ViewholderDetectedFaceBinding

class DetectedFaceAdapter(private val onClick: (Int) -> Unit) :
    ListAdapter<DetectFaceInfoViewItem, DetectedFaceItemViewHolder>(DetectedFaceDiffUtil()) {

    init {
        setHasStableIds(true)
    }

    private class DetectedFaceDiffUtil : DiffUtil.ItemCallback<DetectFaceInfoViewItem>() {
        override fun areItemsTheSame(oldItem: DetectFaceInfoViewItem, newItem: DetectFaceInfoViewItem): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: DetectFaceInfoViewItem, newItem: DetectFaceInfoViewItem): Boolean {
            return oldItem == newItem
        }
    }

    fun updateSelection(selectedIndices: List<Int>) {
        val updatedList = currentList.mapIndexed { index, detectFaceInfoViewItem ->
            detectFaceInfoViewItem.copy(selected = selectedIndices.contains(index))
        }
        submitList(updatedList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetectedFaceItemViewHolder {
        val binding = ViewholderDetectedFaceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DetectedFaceItemViewHolder(binding, onClick)
    }

    override fun onBindViewHolder(holder: DetectedFaceItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}
