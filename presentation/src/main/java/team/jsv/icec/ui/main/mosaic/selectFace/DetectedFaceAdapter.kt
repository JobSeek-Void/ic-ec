package team.jsv.icec.ui.main.mosaic.selectFace

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import team.jsv.domain.model.DetectFaceInfo
import team.jsv.domain.model.Face
import team.jsv.icec.util.loadImage
import team.jsv.presentation.databinding.ViewholderDetectedFaceBinding

class DetectedFaceAdapter :
    ListAdapter<DetectFaceInfo, DetectedFaceAdapter.FaceViewHolder>(DetectedFaceDiffUtil()) {

    class DetectedFaceDiffUtil : DiffUtil.ItemCallback<DetectFaceInfo>() {
        override fun areItemsTheSame(oldItem: DetectFaceInfo, newItem: DetectFaceInfo): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: DetectFaceInfo, newItem: DetectFaceInfo): Boolean {
            return oldItem == newItem
        }
    }

    class FaceViewHolder(private val binding: ViewholderDetectedFaceBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(faceInfo: DetectFaceInfo) {
            binding.imageviewFace.loadImage(faceInfo.url)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FaceViewHolder {
        val binding = ViewholderDetectedFaceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FaceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FaceViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}
