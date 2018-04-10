package demo.victormunoz.githubusers.utils.recyclerview

import android.graphics.Rect
import android.support.annotation.IntRange
import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * Avoid margin duplication between the items. Only the first
 * row will have a top margin and only the first column will have left margin.
 */
class MarginDecoration
/**
 * constructor
 *
 * @param margin  desirable margin size in px between the items inside recyclerView
 * @param columns number of columns of the RecyclerView
 */
(@param:IntRange(from = 0) private val margin: Int, @param:IntRange(from = 0) private val columns: Int) : RecyclerView.ItemDecoration() {

    /**
     * Set different margins for the items inside the recyclerView depending of his column and row
     * position.
     */
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State?) {
        //item position
        val position = parent.getChildLayoutPosition(view)
        //set right margin to all
        outRect.right = margin
        //set bottom margin to all
        outRect.bottom = margin
        //we only add top margin to the first row
        if (position < columns) {
            outRect.top = margin
        }
        //add left margin only to the first column
        if (position % columns == 0) {
            outRect.left = margin
        }
    }
}