package demo.victormunoz.githubusers;

import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.IdlingResource;
import android.support.test.filters.MediumTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import demo.victormunoz.githubusers.features.allusers.AllUsersActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.scrollToPosition;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.google.common.base.Preconditions.checkNotNull;
import static demo.victormunoz.githubusers.endlessLoadedTest.Matchers.withItemCount;
import static demo.victormunoz.githubusers.macher.RecyclerViewMatcher.atPosition;

@RunWith(AndroidJUnit4.class)
@MediumTest
public class endlessLoadedTest {
    @Rule
    public final ActivityTestRule<AllUsersActivity> mAllUsersActivityTestRule = new ActivityTestRule<>(AllUsersActivity.class);

    private void allowScrollToPosition(){
        mAllUsersActivityTestRule.getActivity().runOnUiThread(() -> {
            RecyclerView recyclerView = mAllUsersActivityTestRule.getActivity().findViewById(R.id.recycler_view);
            CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) recyclerView.getLayoutParams();
            params.setBehavior(null);
            recyclerView.requestLayout();
        });
    }

    @Before
    public void setUp(){
        IdlingResource idlingResource = mAllUsersActivityTestRule.getActivity().getCountingIdlingResource();
        IdlingRegistry.getInstance().register(idlingResource);
        allowScrollToPosition();
    }

    @Test
    public void scrollDown_getMoreUsers(){
        //check first page
        onView(withId(R.id.recycler_view))
                .perform(scrollToPosition(0))
                .check(matches(atPosition(0, isDisplayed())));

        //load second page
        onView(withId(R.id.recycler_view)).perform(scrollToPosition(29));

        //check second page
        onView(withId(R.id.recycler_view))
                .perform(scrollToPosition(30))
                .check(matches(atPosition(30, isDisplayed())));

        //load third page
        onView(withId(R.id.recycler_view)).perform(scrollToPosition(59));

        //check third page
        onView(withId(R.id.recycler_view))
                .perform(scrollToPosition(60))
                .check(matches(atPosition(60, isDisplayed())));

        //check total items
        onView(withId(R.id.recycler_view)).check(matches(withItemCount(90)));
    }

    @After
    public void unregisterIdlingResource(){
        IdlingRegistry.getInstance().unregister(mAllUsersActivityTestRule.getActivity().getCountingIdlingResource());
        onView(withId(R.id.recycler_view)).perform(scrollToPosition(0));
    }

    static class Matchers {
        static Matcher<View> withItemCount(@SuppressWarnings("SameParameterValue") final int size){
            return new TypeSafeMatcher<View>() {
                @Override
                public boolean matchesSafely(@NonNull final View view){
                    return ((RecyclerView) checkNotNull(view)).getAdapter().getItemCount() == size;
                }

                @Override
                public void describeTo(@NonNull final Description description){
                    checkNotNull(description).appendText("recyclerView should have " + size + " items");
                }
            };
        }
    }

}
