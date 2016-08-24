package demo.victormunoz.githubusers.ui.users;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.test.espresso.IdlingResource;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.BindDimen;
import butterknife.BindView;
import demo.victormunoz.githubusers.R;
import demo.victormunoz.githubusers.api.model.User;
import demo.victormunoz.githubusers.ui.App;
import demo.victormunoz.githubusers.ui.userdetail.UserDetailActivity;
import demo.victormunoz.githubusers.utils.espresso.EspressoIdlingResource;
import demo.victormunoz.githubusers.utils.recyclerview.RecyclerViewMargin;

/**
 * Create an Android App with the following functionality:
 *      It connects to GitHub public API.
 *      It can display GitHub users using this API call:https://developer.github.com/v3/users/#get-all-users
 *      The users are displayed in a list.
 *      For each user, show the avatar and the login name.
 *      Clicking on the user should open a browser to their profile.
 *      When we scroll down, more users are loaded and displayed
 * Write your code as it was ready for production.
 *      Feel free to use any libraries you want (but do NOT use the GitHub Android SDK or similar).
 *      There's no need to implement storage or caching.
 *      Create at least one Unit Test or Automated Test.
 *      Use Material Design as guideline for your UI.
 *      The app needs to run on an API 22 device.
 */
public class UsersActivity extends AppCompatActivity implements UsersContract.Views,UsersAdapter.UsersListener {
    @BindView(R.id.github_logo)
    ImageView imageView;
    @BindDimen(R.dimen.app_bar_height)
    int appBarHeight;
    @BindView(R.id.root)
    CoordinatorLayout root;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @Inject
    UsersAdapter mUsersAdapter;
    @Inject
    UsersContract.UserActionsListener mActionsListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        ButterKnife.bind(this);

        //Set ActionBar
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowTitleEnabled(true);
        //Dagger 2 injection
        ((App)getApplication()).getUsersComponent(this).inject(this);

        recyclerView.setAdapter(mUsersAdapter);
        int numColumns = getResources().getInteger(R.integer.columns);
        int itemMargin = getResources().getDimensionPixelSize(R.dimen.margin_between_content);
        RecyclerViewMargin decoration = new RecyclerViewMargin(itemMargin, numColumns);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, numColumns));
        recyclerView.addItemDecoration(decoration);
        mActionsListener.loadMoreUsers();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mActionsListener = null;
    }


    @Override
    public void addUsersToAdapter(List<User> users) {
        mUsersAdapter.addUsers(users);
    }

    @Override
    public void onLoadMoreUsersFail() {
       String errorMessage="Error downloading more users";
       if( mUsersAdapter.getItemCount()==0){
           errorMessage="Error downloading users";
       }
        Snackbar snackbar = Snackbar
                .make(imageView, errorMessage, Snackbar.LENGTH_INDEFINITE)
                .setAction("RETRY", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mActionsListener.loadMoreUsers();
                    }
                });
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        Drawable cloudError = ResourcesCompat.getDrawable(getResources(),R.drawable.icon_cloud_error,null);
        textView.setCompoundDrawablesWithIntrinsicBounds(cloudError,null,null,null);
        textView.setCompoundDrawablePadding( getResources().getDimensionPixelOffset(R.dimen.margin_content));
        snackbar.show();
    }

    /**
     * call to a new activity to show the user's profile information.
     * @param imageView the image to be use in the shared element transition.
     * @param loginName the loginName of the user, will be use to make a API request.
     * @param avatarURL URL of the user's avatar image. The shared element transition need the
     *                  new activity was downloading the image before start. Sending the url we
     *                  avoid to call the api to get the url and make a second call to downloaded.
     *                  this way we can start the animation more fast.
     */
    private void callDetailUserActivity(View imageView, String loginName, String avatarURL){
        Intent intent = new Intent(this, UserDetailActivity.class);
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(
                this, imageView, imageView.getTransitionName());
        intent.putExtra(UserDetailActivity.USER_LOGIN,loginName);
        intent.putExtra(UserDetailActivity.USER_PICTURE_URL,avatarURL);
        startActivity(intent,options.toBundle());
    }



    @VisibleForTesting
    public IdlingResource getCountingIdlingResource() {
        return EspressoIdlingResource.getIdlingResource();
    }

    @Override
    public void onUserClick(View view,String loginName,String avatarURL) {
        callDetailUserActivity(view,loginName,avatarURL);
    }

    @Override
    public void onEndOfTheList() {

        mActionsListener.loadMoreUsers();
    }
}
