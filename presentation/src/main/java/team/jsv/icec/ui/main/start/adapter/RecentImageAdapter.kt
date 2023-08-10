package team.jsv.icec.ui.main.start.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import team.jsv.presentation.databinding.RecentItemLayoutBinding

class RecentImageAdapter(private val onClick: (String) -> Unit) :
    ListAdapter<String, RecentImageItemViewHolder>(RecentImageDiffUtil) {
    object RecentImageDiffUtil : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(
            oldItem: String,
            newItem: String
        ): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(
            oldItem: String,
            newItem: String
        ): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentImageItemViewHolder {
        val binding =
            RecentItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecentImageItemViewHolder(binding, onClick)
    }


    override fun onBindViewHolder(holder: RecentImageItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}