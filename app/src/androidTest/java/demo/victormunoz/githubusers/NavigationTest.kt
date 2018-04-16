package demo.victormunoz.githubusers

import android.support.design.widget.CoordinatorLayout
import android.support.test.espresso.Espresso
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.IdlingRegistry
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import android.support.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import android.support.test.espresso.intent.Intents
import android.support.test.espresso.intent.Intents.intended
import android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.espresso.matcher.ViewMatchers.withText
import android.support.test.filters.MediumTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.support.v7.widget.RecyclerView
import demo.victormunoz.githubusers.features.allusers.AllUsersActivity
import demo.victormunoz.githubusers.features.userDetails.UserDetailsActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@MediumTest
class NavigationTest {

    @Rule
    var mUsersActivityTestRule = ActivityTestRule(AllUsersActivity::class.java)


    @Before
    fun setUp() {
        //Initializes Intents and begins recording intents
        Intents.init()
        //set idle
        IdlingRegistry.getInstance().register(mUsersActivityTestRule.activity.countingIdlingResource)
        //trick to allow scrollToPosition inside CoordinatorLayout, otherwise the scroll will not be performed
        mUsersActivityTestRule.activity.runOnUiThread {
            val recyclerView = mUsersActivityTestRule.activity.findViewById<RecyclerView>(R.id.recycler_view)
            val params = recyclerView.layoutParams as CoordinatorLayout.LayoutParams
            params.behavior = null
            recyclerView.requestLayout()
        }

    }

    @Test
    fun clickUser_openDetailActivity() {
        val firstUserPosition = 0
        onView(withId(R.id.recycler_view)).perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(firstUserPosition, click()))
        intended(hasComponent(UserDetailsActivity::class.java.name))
        onView(withId(R.id.user_login)).check(matches(withText(FIRST_USER_LOGINNAME)))
        Espresso.pressBack()

    }

    @Test
    fun scrollAndClickUser_openDetailActivity() {
        val userPosition = 29
        onView(withId(R.id.recycler_view))
                .perform(scrollToPosition<RecyclerView.ViewHolder>(userPosition))
                .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(userPosition, click()))
        intended(hasComponent(UserDetailsActivity::class.java.name))
        onView(withId(R.id.user_login)).check(matches(withText(TWENTY_NINTH_USER_LOGINNAME)))
        Espresso.pressBack()

    }

    @After
    fun releaseAndUnregister() {
        Intents.release()
        IdlingRegistry.getInstance().unregister(mUsersActivityTestRule.activity.countingIdlingResource)
        onView(withId(R.id.recycler_view)).perform(scrollToPosition<RecyclerView.ViewHolder>(0))
    }

    companion object {
        private const val FIRST_USER_LOGINNAME = "mojombo"
        private const val TWENTY_NINTH_USER_LOGINNAME = "bmizerany"
    }

}