package demo.victormunoz.githubusers;

import android.support.design.widget.CoordinatorLayout;
import android.support.test.filters.MediumTest;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.scrollToPosition;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import demo.victormunoz.githubusers.ui.userdetail.UserDetailActivity;
import demo.victormunoz.githubusers.ui.users.UsersActivity;


import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@MediumTest
public class NavigationTest {
    private final static  String FIRST_USER_LOGINNAME="mojombo";
    private final static String TWENTY_NINTH_USER_LOGINNAME="bmizerany";

    @Rule
    public ActivityTestRule<UsersActivity> mUsersActivityTestRule =
            new ActivityTestRule<>(UsersActivity.class);


    @Before
    public void setUp() {
        //Initializes Intents and begins recording intents
        Intents.init();
        //set idle
        Espresso.registerIdlingResources(mUsersActivityTestRule.getActivity().getCountingIdlingResource());
        //trick to allow scrollToPosition inside CoordinatorLayout, otherwise the scroll will not be
        // performed
        mUsersActivityTestRule.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                RecyclerView recyclerView = (RecyclerView) mUsersActivityTestRule.getActivity().
                        findViewById(R.id.recycler_view);
                CoordinatorLayout.LayoutParams params =
                        (CoordinatorLayout.LayoutParams) recyclerView.getLayoutParams();
                params.setBehavior(null);
                recyclerView.requestLayout();
            }
        });

    }

    @Test
    public void clickFirstUser_openDetailActivity() throws Exception {
        onView(withId(R.id.recycler_view)).perform(actionOnItemAtPosition(0, click()));
        intended(hasComponent(UserDetailActivity.class.getName()));
        onView(withId(R.id.user_login)).check(matches(withText(FIRST_USER_LOGINNAME)));
        Espresso.pressBack();

    }

    @Test
    public void clickLastUser_openDetailActivity() throws Exception {
        onView(withId(R.id.recycler_view)).perform(scrollToPosition(29));
        onView(withId(R.id.recycler_view)).perform(actionOnItemAtPosition(29, click()));
        intended(hasComponent(UserDetailActivity.class.getName()));
        onView(withId(R.id.user_login)).check(matches(withText(TWENTY_NINTH_USER_LOGINNAME)));
        Espresso.pressBack();
        onView(withId(R.id.recycler_view)).perform(scrollToPosition(0));

    }

    @After
    public void releaseAndUnregister() {
        Intents.release();
        Espresso.unregisterIdlingResources(
                mUsersActivityTestRule.getActivity().getCountingIdlingResource());
    }

}