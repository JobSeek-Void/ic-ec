package team.jsv.icec.ui.main.start.adapter

import androidx.recyclerview.widget.RecyclerView
import team.jsv.icec.ui.main.start.model.ImageViewItem
import team.jsv.icec.ui.main.start.model.RecentImage
import team.jsv.icec.util.loadImage
import team.jsv.presentation.databinding.RecentItemLayoutBinding

class RecentImageItemViewHolder(
    private val binding: RecentItemLayoutBinding,
) : RecyclerView.ViewHolder(binding.root) {

    //레이아웃과 데이터를 연결하는 메소드
    fun bind(item: String) {
        binding.ivItem.apply {
            loadImage(item)
        }

//        binding.item.setOnClickListener {
//            onClick(adapterPosition)
//        }
    }
}