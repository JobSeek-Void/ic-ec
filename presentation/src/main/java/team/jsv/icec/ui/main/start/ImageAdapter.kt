package team.jsv.icec.ui.main.start

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import team.jsv.icec.base.startActivityWithAnimation
import team.jsv.icec.ui.main.MainActivity
import team.jsv.icec.util.Extras
import team.jsv.icec.util.loadImage
import team.jsv.presentation.R

class ImageAdapter(private val context: Context, private val imagePaths: List<String>) :
    RecyclerView.Adapter<ImageAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.recent_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val imagePath = imagePaths[position]
        holder.imageView.loadImage(imagePath)
        holder.imageView.setOnClickListener {
            val activity = context as Activity
            activity.startActivityWithAnimation<MainActivity>(intentBuilder = {
                putExtra(Extras.ImagePath, imagePaths[position])
            })
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.iv_item)
    }

    override fun getItemCount(): Int {
        return imagePaths.size
    }
}

