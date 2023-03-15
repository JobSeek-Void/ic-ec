package team.jsv.icec.adapter

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import team.jsv.icec.data.RecyclerViewData
import team.jsv.presentation.databinding.ActivityMainMosaicStep1Binding

class RecyclerViewAdapter(val itemList: ArrayList<RecyclerViewData>) :
    RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    interface OnItemClickListener{
        fun OnItemClick(url: Bitmap)
    }

    var itemClickListener:OnItemClickListener?=null

    inner class ViewHolder(val binding: ActivityMainMosaicStep1Binding):RecyclerView.ViewHolder(binding.root){
        init {
            binding.root.setOnClickListener {
                itemClickListener?.OnItemClick(itemList[adapterPosition].imgBitmap)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ActivityMainMosaicStep1Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
//            textView.text = items[position].fName
//            imageView.setImageResource(items[position].id1)
        }
    }

    override fun getItemCount(): Int {
//        return items.size
    }
}