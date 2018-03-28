package demo.victormunoz.githubusers.utils.recyclerview;

import android.graphics.Rect;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Avoid margin duplication between the items. Only the first
 * row will have a top margin and only the first column will have left margin.
 */
public class MarginDecoration extends RecyclerView.ItemDecoration {
    private final int columns;
    private final int margin;

    /**
     * constructor
     *
     * @param margin  desirable margin size in px between the items inside recyclerView
     * @param columns number of columns of the RecyclerView
     */
    public MarginDecoration(@IntRange(from = 0) int margin, @IntRange(from = 0) int columns){
        this.margin = margin;
        this.columns = columns;
    }

    /**
     * Set different margins for the items inside the recyclerView depending of his column and row
     * position.
     */
    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @Nullable RecyclerView.State state){
        //item position
        int position = parent.getChildLayoutPosition(view);
        //set right margin to all
        outRect.right = margin;
        //set bottom margin to all
        outRect.bottom = margin;
        //we only add top margin to the first row
        if (position < columns) {
            outRect.top = margin;
        }
        //add left margin only to the first column
        if (position % columns == 0) {
            outRect.left = margin;
        }
    }
}