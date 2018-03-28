package demo.victormunoz.githubusers;

import android.support.design.widget.CoordinatorLayout;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.intent.Intents;
import android.support.test.filters.MediumTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import demo.victormunoz.githubusers.features.allusers.AllUsersActivity;
import demo.victormunoz.githubusers.features.userDetails.UserDetailsActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.contrib.RecyclerViewActions.scrollToPosition;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@MediumTest
public class NavigationTest {
    private final static String FIRST_USER_LOGINNAME = "mojombo";
    private final static String TWENTY_NINTH_USER_LOGINNAME = "bmizerany";

    @Rule
    public final ActivityTestRule<AllUsersActivity> mUsersActivityTestRule = new ActivityTestRule<>(AllUsersActivity.class);


    @Before
    public void setUp(){
        //Initializes Intents and begins recording intents
        Intents.init();
        //set idle
        IdlingRegistry.getInstance().register(mUsersActivityTestRule.getActivity().getCountingIdlingResource());
        //trick to allow scrollToPosition inside CoordinatorLayout, otherwise the scroll will not be
        // performed
        mUsersActivityTestRule.getActivity().runOnUiThread(() -> {
            RecyclerView recyclerView = mUsersActivityTestRule.getActivity().
                    findViewById(R.id.recycler_view);
            CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) recyclerView.getLayoutParams();
            params.setBehavior(null);
            recyclerView.requestLayout();
        });

    }

    @Test
    public void clickUser_openDetailActivity(){
        int firstUserPosition = 0;
        onView(withId(R.id.recycler_view)).perform(actionOnItemAtPosition(firstUserPosition, click()));
        intended(hasComponent(UserDetailsActivity.class.getName()));
        onView(withId(R.id.user_login)).check(matches(withText(FIRST_USER_LOGINNAME)));
        Espresso.pressBack();

    }

    @Test
    public void scrollAndClickUser_openDetailActivity(){
        int userPosition = 29;
        onView(withId(R.id.recycler_view))
                .perform(scrollToPosition(userPosition))
                .perform(actionOnItemAtPosition(userPosition, click()));
        intended(hasComponent(UserDetailsActivity.class.getName()));
        onView(withId(R.id.user_login)).check(matches(withText(TWENTY_NINTH_USER_LOGINNAME)));
        Espresso.pressBack();

    }

    @After
    public void releaseAndUnregister(){
        Intents.release();
        IdlingRegistry.getInstance().unregister(mUsersActivityTestRule.getActivity().getCountingIdlingResource());
        onView(withId(R.id.recycler_view)).perform(scrollToPosition(0));
    }

}