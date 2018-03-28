package demo.victormunoz.githubusers.features.allusers;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.test.espresso.IdlingResource;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;
import demo.victormunoz.githubusers.App;
import demo.victormunoz.githubusers.R;
import demo.victormunoz.githubusers.features.allusers.AllUsersContract.AdapterListener;
import demo.victormunoz.githubusers.features.allusers.AllUsersContract.PresenterListener;
import demo.victormunoz.githubusers.features.userDetails.UserDetailsActivity;
import demo.victormunoz.githubusers.model.entity.User;
import demo.victormunoz.githubusers.services.FloatingIconService;
import demo.victormunoz.githubusers.utils.espresso.EspressoIdlingResource;
import demo.victormunoz.githubusers.utils.recyclerview.MarginDecoration;

@SuppressWarnings("WeakerAccess")
public class AllUsersActivity extends AppCompatActivity implements PresenterListener, AdapterListener {

    private final static int REQUEST_CODE = 5463;
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
    AllUsersAdapter mAllUsersAdapter;
    @Inject
    AllUsersContract.ViewListener mViewListener;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);
        //data binding
        ButterKnife.bind(this);
        //Dependency injection
        ((App) getApplication()).getUsersComponent(this).inject(this);
        //Set ActionBar
        setSupportActionBar(toolbar);
        //Set recyclerView
        setRecyclerView();
        //check permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            checkPermission();
        }
    }

    @Override
    public void setSupportActionBar(@Nullable Toolbar toolbar){
        super.setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowTitleEnabled(true);
    }

    @Override
    protected void onDestroy(){
        recyclerView.setAdapter(mAllUsersAdapter);
        mViewListener = null;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(this)) {
            startService(new Intent(this, FloatingIconService.class));
        }
        super.onDestroy();
    }

    @Override
    public void addUsers(@NonNull List<User> users){
        mAllUsersAdapter.addUsers(users);
    }

    /**
     * call to a new activity to show the user's profile information.
     *
     * @param view      the view to be use in the shared element transition.
     * @param loginName the loginName of the user, will be use to make a API request.
     * @param avatarURL URL of the user's avatar image. The shared element transition needs the
     *                  new activity download the image before start. Sending the url we
     *                  avoid to call the api to get the url and make a second call to download the image,
     *                  this way we can start the animation faster.
     */
    @Override
    public void goToUserDetails(@NonNull View view, String loginName, String avatarURL){
        Intent intent = new Intent(this, UserDetailsActivity.class);
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, view, view.getTransitionName());
        intent.putExtra(UserDetailsActivity.USER_LOGIN, loginName);
        intent.putExtra(UserDetailsActivity.USER_PICTURE_URL, avatarURL);
        startActivity(intent, options.toBundle());
    }

    @Override
    public void showError(){
        String errorMessage = getString(R.string.error_downloading_more_users);
        if (mAllUsersAdapter.getItemCount() == 0) {
            errorMessage = getString(R.string.error_downloading_users);
        }
        Snackbar snackbar = Snackbar
                .make(imageView, errorMessage, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.retry, view -> mViewListener.onRetry());
        View sbView = snackbar.getView();
        TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
        Drawable cloudError = ResourcesCompat.getDrawable(getResources(), R.drawable.icon_cloud_error, null);
        textView.setCompoundDrawablesWithIntrinsicBounds(cloudError, null, null, null);
        textView.setCompoundDrawablePadding(getResources().getDimensionPixelOffset(R.dimen.margin_normal));
        snackbar.show();
    }

    @Override
    public void onEndOfTheList(){
        mViewListener.onEndOfTheList();
    }

    @Override
    public void onItemClick(View view, int position){
        User user = mAllUsersAdapter.getItem(position);
        mViewListener.onItemClick(view, user);
    }

    private void setRecyclerView(){
        recyclerView.setAdapter(mAllUsersAdapter);
        int numColumns = getResources().getInteger(R.integer.columns);
        int itemMargin = getResources().getDimensionPixelSize(R.dimen.margin_small);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, numColumns));
        recyclerView.addItemDecoration(new MarginDecoration(itemMargin, numColumns));
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkPermission(){
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse(getResources().getString(R.string.package_uri,getPackageName())));
        startActivityForResult(intent, REQUEST_CODE);
    }

    @NonNull
    @VisibleForTesting
    public IdlingResource getCountingIdlingResource() {
        return EspressoIdlingResource.getIdlingResource();
    }
}
