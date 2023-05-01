package team.jsv.icec.util

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class HorizontalSpaceItemDecoration(private val space: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        when (parent.getChildAdapterPosition(view)) {
            0 -> {
                outRect.right = space
            }
            state.itemCount - 1 -> {
                outRect.left = space
            }
            else -> {
                outRect.left = space
                outRect.right = space
            }
        }
    }

}