package team.jsv.icec.util

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * ImageView.loadImage() 함수는 Glide를 이용하여 이미지를 로드하는 함수입니다.
 * 해당 함수의 파라미터로 Any 타입의 데이터를 받습니다.
 * Any 타입의 데이터는 String, Int, Uri 등이 될 수 있습니다.
 */

fun ImageView.loadImage(data: Any) {
    Glide.with(this.context)
        .load(data)
        .diskCacheStrategy(DiskCacheStrategy.NONE)
        .skipMemoryCache(true)
        .listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>?,
                isFirstResource: Boolean,
            ): Boolean {
                return false
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean,
            ): Boolean {
                CoroutineScope(Dispatchers.Main).launch {
                    setImageDrawable(resource)
                }
                return true
            }
        })
        .submit()
}

fun ImageView.toBitmap(): Bitmap {
    val bitmapDrawable = this.drawable as BitmapDrawable
    return bitmapDrawable.bitmap
}