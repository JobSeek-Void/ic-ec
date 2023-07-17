package team.jsv.icec.ui.main.start.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import team.jsv.presentation.databinding.RecentItemLayoutBinding

class RecentImageAdapter() :
    ListAdapter<String, RecentImageItemViewHolder>(RecentImageDiffUtil) {
    object RecentImageDiffUtil : DiffUtil.ItemCallback<String>() {
        //아이템 항목 자체가 같은지 비교
        override fun areItemsTheSame(
            oldItem: String,
            newItem: String
        ): Boolean {
            return oldItem == newItem
        }
        // 아이템의 내용을 비교
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
        return RecentImageItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecentImageItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

//class RecentImageAdapter(private val imagePaths: List<String>) :
//    RecyclerView.Adapter<RecentImageAdapter.ViewHolder>() {
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.recent_item_layout, parent, false)
//        return ViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val imagePath = imagePaths[position]
//        holder.imageView.loadImage(imagePath)
//
//        holder.imageView.setOnClickListener {
//            val activity =  as Activity
//            activity.startActivityWithAnimation<MainActivity>(intentBuilder = {
//                putExtra(Extras.ImagePath, imagePaths[position])
//            })
//        }
//    }
//
//    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val imageView: ImageView = itemView.findViewById(R.id.iv_item)
//
//
//    }
//
//    override fun getItemCount(): Int {
//        return imagePaths.size
//    }
//}