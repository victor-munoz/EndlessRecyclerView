package demo.victormunoz.githubusers.ui.userdetail;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.Bitmap;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Bundle;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.test.espresso.IdlingResource;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import demo.victormunoz.githubusers.R;
import demo.victormunoz.githubusers.api.model.User;
import demo.victormunoz.githubusers.ui.App;
import demo.victormunoz.githubusers.utils.espresso.EspressoIdlingResource;
import demo.victormunoz.githubusers.utils.picasso.ImageToCircleTransformation;

public class UserDetailActivity extends AppCompatActivity implements UserDetailContract.View {
    public static final String USER_LOGIN = "user login";
    public static final String USER_PICTURE_URL = "user picture URL";
    private static final String USER_DETAIL_FRAGMENT = "user's detail fragment";
    @BindView(R.id.app_bar)
    AppBarLayout appBar;
    @BindView(R.id.user_avatar)
    ImageView avatar;
    @BindView(R.id.user_login)
    TextView userLogin;
    @BindView(R.id.user_name)
    TextView name;
    @BindView(R.id.user_email)
    TextView email;
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout collapsing;
    private UserDetailFragment detailFragment;
    private String loginName;
    private String avatarURL;
    //UserDetailContract.UserActionsListener mActionsListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //postpone the transition start to wait for the avatar image be loaded
        postponeEnterTransition();
        setContentView(R.layout.activity_user_detail);
        ButterKnife.bind(this);
        //set Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        //get intent vars
        loginName = getIntent().getStringExtra(USER_LOGIN);
        avatarURL = getIntent().getStringExtra(USER_PICTURE_URL);


        //((App)getApplication() ).getUserDetailPresenterComponent(this).inject(this);

        if (null == savedInstanceState) {
           // mActionsListener.loadUserDetails(loginName);
            initFragment(UserDetailFragment.newInstance());
        }
        downloadProfileImage(true);
    }

    private void downloadProfileImage(final boolean isSharedElementOn) {
        Picasso.with(this)
                .load(avatarURL).fit().centerCrop()
                .transform(new ImageToCircleTransformation())
                .into(avatar, new Callback() {
                    @Override
                    public void onSuccess() {
                        if(isSharedElementOn){
                            startPostponedEnterTransition();
                            startRevealAnimation(getGradient(),200);
                        }
                        else{
                            startRevealAnimation(getGradient(),0);
                        }
                    }

                    @Override
                    public void onError() {
                        if(isSharedElementOn) {
                            startPostponedEnterTransition();
                        }
                        onLoadUserDetailsFail();
                    }
                });

    }

    /**
     * use Android Palette Library to create ShapeDrawable with a gradient effect
     */
    private ShapeDrawable getGradient() {
        Bitmap bitmap = ((BitmapDrawable) avatar.getDrawable()).getBitmap();
        Palette palette = Palette.from(bitmap).generate();
        int muted = palette.getMutedColor(
                ContextCompat.getColor(getBaseContext(), R.color.colorPrimary));
        int mutedDark = palette.getDarkMutedColor(
                ContextCompat.getColor(getBaseContext(), R.color.colorPrimaryDark));
        int x = appBar.getWidth();
        ShapeDrawable mDrawable = new ShapeDrawable(new RectShape());
        LinearGradient gradient = new LinearGradient(0, 0, x, 0, muted, mutedDark, Shader.TileMode.REPEAT);
        mDrawable.getPaint().setShader(gradient);
        return mDrawable;

    }

    private void startRevealAnimation(final ShapeDrawable gradient, long delay) {
        Animator reveal = ViewAnimationUtils.createCircularReveal(
                appBar
                , (int) (((LinearLayout) avatar.getParent()).getX() + (avatar.getWidth() / 2))
                , (int) (((LinearLayout) avatar.getParent()).getY() + (avatar.getHeight() / 2))
                , avatar.getWidth() / 4
                , appBar.getWidth());
        reveal.setStartDelay(delay);
        reveal.setDuration(1000);
        reveal.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                appBar.setBackground(gradient);
            }
        });
        reveal.start();
    }

    private void initFragment(Fragment fragment) {
        // Add the fragment to the layout
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.contentFrame, fragment, USER_DETAIL_FRAGMENT);
        transaction.commit();
        fragmentManager.executePendingTransactions();
        detailFragment = (UserDetailFragment) fragmentManager.findFragmentByTag(USER_DETAIL_FRAGMENT);

    }

    @Override
    public void displayUserDetails(final User user) {
        detailFragment.displayUserInfo(user);
        Typeface myTypeface = Typeface.createFromAsset(getAssets(), "fonts/heavy_data.ttf");
        name.setTypeface(myTypeface);
        name.setText(user.getFullName());
        email.setText(user.getEmail());
        userLogin.setText(user.getLoginName());
    }

    @Override
    public void onLoadUserDetailsFail() {
        String errorMessage = "Error downloading user's profile";
        Snackbar snackbar = Snackbar
                .make(avatar, errorMessage, Snackbar.LENGTH_INDEFINITE)
                .setAction("RETRY", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (avatar.getDrawable() == null) {
                            downloadProfileImage(false);
                        }
                        if(name.getText().length() == 0){
                            //mActionsListener.loadUserDetails(loginName);
                        }
                    }
                });
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        Drawable cloudError = ResourcesCompat.getDrawable(getResources(), R.drawable.icon_cloud_error, null);
        textView.setCompoundDrawablesWithIntrinsicBounds(cloudError, null, null, null);
        textView.setCompoundDrawablePadding(getResources().getDimensionPixelOffset(R.dimen.margin_content));
        snackbar.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finishAfterTransition();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @VisibleForTesting
    public IdlingResource getCountingIdlingResource() {
        return EspressoIdlingResource.getIdlingResource();
    }

}


