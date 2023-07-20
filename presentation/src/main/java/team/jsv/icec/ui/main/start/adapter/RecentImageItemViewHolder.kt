package team.jsv.icec.ui.main.start.adapter

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import team.jsv.presentation.databinding.RecentItemLayoutBinding

class RecentImageItemViewHolder(
    private val binding: RecentItemLayoutBinding,
    private val onClick: (String) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: String) {
        binding.ivItem.apply {
            Glide.with(binding.root).load(item).into(this)

            setOnClickListener {
                onClick(item)
            }
        }
    }
}
