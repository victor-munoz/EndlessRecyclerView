package demo.victormunoz.githubusers.macher

import android.support.test.espresso.matcher.BoundedMatcher
import android.support.v7.widget.RecyclerView
import android.view.View
import com.google.common.base.Preconditions.checkNotNull
import org.hamcrest.Description
import org.hamcrest.Matcher

object RecyclerViewMatcher {

    fun atPosition(position: Int, itemMatcher: Matcher<View>): Matcher<View> {
        checkNotNull(itemMatcher)
        return object : BoundedMatcher<View, RecyclerView>(RecyclerView::class.java) {
            override fun describeTo(description: Description) {
                description.appendText("has item at position $position: ")
                itemMatcher.describeTo(description)
            }

            override fun matchesSafely(view: RecyclerView): Boolean {
                val viewHolder = view.findViewHolderForAdapterPosition(position)
                // has no item on such position
                return viewHolder != null && itemMatcher.matches(viewHolder.itemView)
            }
        }
    }
}