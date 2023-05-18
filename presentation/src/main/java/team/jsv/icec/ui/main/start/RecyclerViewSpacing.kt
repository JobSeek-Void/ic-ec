package team.jsv.icec.ui.main.start

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewSpacing(
    val space: Int,
    val topSpace: Int,
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State,
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val index = (view.layoutParams as GridLayoutManager.LayoutParams).spanIndex
        val position = parent.getChildLayoutPosition(view)
        if (index == 0) {
            outRect.right = space / 2
        } else {
            outRect.left = topSpace / 2
        }
        if (position < 2) {
            outRect.top = 0
        } else {
            outRect.top = topSpace
        }
    }
}