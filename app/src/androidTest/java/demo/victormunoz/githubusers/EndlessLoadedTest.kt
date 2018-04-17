package demo.victormunoz.githubusers

import android.support.design.widget.CoordinatorLayout
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.IdlingRegistry
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.filters.MediumTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.support.v7.widget.RecyclerView
import android.view.View
import com.google.common.base.Preconditions.checkNotNull
import demo.victormunoz.githubusers.EndlessLoadedTest.Matchers.withItemCount
import demo.victormunoz.githubusers.features.allusers.AllUsersActivity
import demo.victormunoz.githubusers.macher.RecyclerViewMatcher.atPosition
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@MediumTest
class EndlessLoadedTest {
    @get:Rule
    val mAllUsersActivityTestRule = ActivityTestRule(AllUsersActivity::class.java)

    private fun allowScrollToPosition() {
        mAllUsersActivityTestRule.activity.runOnUiThread {
            val recyclerView = mAllUsersActivityTestRule.activity.findViewById<RecyclerView>(R.id.recycler_view)
            val params = recyclerView.layoutParams as CoordinatorLayout.LayoutParams
            params.behavior = null
            recyclerView.requestLayout()
        }
    }

    @Before
    fun setUp() {
        val idlingResource = mAllUsersActivityTestRule.activity.countingIdlingResource
        IdlingRegistry.getInstance().register(idlingResource)
        allowScrollToPosition()
    }

    @Test
    fun scrollDown_getMoreUsers() {
        //check first page
        onView(withId(R.id.recycler_view))
                .perform(scrollToPosition<RecyclerView.ViewHolder>(0))
                .check(matches(atPosition(0, isDisplayed())))

        //load second page
        onView(withId(R.id.recycler_view)).perform(scrollToPosition<RecyclerView.ViewHolder>(29))

        //check second page
        onView(withId(R.id.recycler_view))
                .perform(scrollToPosition<RecyclerView.ViewHolder>(30))
                .check(matches(atPosition(30, isDisplayed())))

        //load third page
        onView(withId(R.id.recycler_view)).perform(scrollToPosition<RecyclerView.ViewHolder>(59))

        //check third page
        onView(withId(R.id.recycler_view))
                .perform(scrollToPosition<RecyclerView.ViewHolder>(60))
                .check(matches(atPosition(60, isDisplayed())))

        //check total items
        onView(withId(R.id.recycler_view)).check(matches(withItemCount(90)))
    }

    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(mAllUsersActivityTestRule.activity.countingIdlingResource)
        onView(withId(R.id.recycler_view)).perform(scrollToPosition<RecyclerView.ViewHolder>(0))
    }

    internal object Matchers {
        fun withItemCount(size: Int): Matcher<View> {
            return object : TypeSafeMatcher<View>() {
                public override fun matchesSafely(view: View): Boolean {
                    return (checkNotNull(view) as RecyclerView).adapter.itemCount == size
                }

                override fun describeTo(description: Description) {
                    checkNotNull(description).appendText("recyclerView should have $size items")
                }
            }
        }
    }

}
