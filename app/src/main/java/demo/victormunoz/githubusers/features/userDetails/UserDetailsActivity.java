package demo.victormunoz.githubusers.features.userDetails;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Callback;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import demo.victormunoz.githubusers.App;
import demo.victormunoz.githubusers.R;
import demo.victormunoz.githubusers.model.entity.User;
import demo.victormunoz.githubusers.utils.picasso.ImageLoader;

@SuppressWarnings("WeakerAccess")
public class UserDetailsActivity extends AppCompatActivity implements UserDetailsContract.PresenterListener, Callback {
    public static final String USER_LOGIN = "user login";
    public static final String USER_PICTURE_URL = "user picture URL";
    private static final String USER_DETAIL_FRAGMENT = "user's detail fragment";
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.user_github)
    Button github;
    @BindView(R.id.user_blog)
    Button blog;
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

    @Inject
    UserDetailsContract.UserActionsListener mActionsListener;
    @Inject
    ImageLoader imageLoader;

    private UserDetailsFragment detailFragment;
    private String loginName;
    private String avatarURL;
    private boolean isSharedElementOn = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //postpone the transition start to wait for the avatar image be loaded
        postponeEnterTransition();

        setContentView(R.layout.activity_user_detail);
        //data binding
        ButterKnife.bind(this);
        //set Actionbar
        setSupportActionBar(toolbar);
        //get intent vars
        loginName = getIntent().getStringExtra(USER_LOGIN);
        avatarURL = getIntent().getStringExtra(USER_PICTURE_URL);
        //Dagger 2 injection
        ((App) getApplication()).getUserComponent(this).inject(this);

        if (null == savedInstanceState) {
            mActionsListener.getUserDetails(loginName);
            initFragment(UserDetailsFragment.newInstance());
        }
        downloadProfileImage();
    }
    @Override
    public void setSupportActionBar(@Nullable Toolbar toolbar){
        super.setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
    }

    @Override
    public void onActivityReenter(int resultCode, Intent data){
        super.onActivityReenter(resultCode, data);
        postponeEnterTransition();
    }

    @Override
    protected void onDestroy(){
        imageLoader.cancelAll();
        super.onDestroy();
    }

    private void downloadProfileImage(){
        imageLoader.load(avatarURL, avatar, this);
    }

    /**
     * use Android Palette Library to create ShapeDrawable with a gradient effect
     */
    @NonNull
    private ShapeDrawable getGradient(){
        Bitmap bitmap = ((BitmapDrawable) avatar.getDrawable()).getBitmap();
        Palette palette = Palette.from(bitmap).generate();
        int muted = palette.getMutedColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary));
        int mutedDark = palette.getDarkMutedColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimaryDark));
        int x = appBar.getWidth();
        ShapeDrawable mDrawable = new ShapeDrawable(new RectShape());
        LinearGradient gradient = new LinearGradient(0, 0, x, 0, muted, mutedDark, Shader.TileMode.REPEAT);
        mDrawable.getPaint().setShader(gradient);
        return mDrawable;

    }

    private void startRevealAnimation(final ShapeDrawable gradient, long delay){
        Animator reveal = ViewAnimationUtils.createCircularReveal(appBar, (int) (((LinearLayout) avatar.getParent()).getX() + (avatar.getWidth() / 2)), (int) (((LinearLayout) avatar.getParent()).getY() + (avatar.getHeight() / 2)), avatar.getWidth() / 4, appBar.getWidth());
        reveal.setStartDelay(delay);
        reveal.setDuration(1000);
        reveal.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation){
                super.onAnimationStart(animation);
                appBar.setBackground(gradient);
            }
        });
        reveal.start();
    }

    private void initFragment(Fragment fragment){
        // Add the fragment to the layout
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.contentFrame, fragment, USER_DETAIL_FRAGMENT);
        transaction.commit();
        fragmentManager.executePendingTransactions();
        detailFragment = (UserDetailsFragment) fragmentManager.findFragmentByTag(USER_DETAIL_FRAGMENT);

    }

    @Override
    public void displayUserDetails(@NonNull final User user){
        detailFragment.displayUserInfo(user);
        github.setVisibility(View.VISIBLE);
        github.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v){
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(user.getGitHubProfileUrl()));
                startActivity(browserIntent);
            }
        });
        if (user.getBlog() != null && Patterns.WEB_URL.matcher(user.getBlog()).matches()) {
            blog.setVisibility(View.VISIBLE);
            blog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v){
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(user.getBlog()));
                    startActivity(browserIntent);
                }
            });
        }

        Typeface myTypeface = Typeface.createFromAsset(getAssets(), "fonts/heavy_data.ttf");
        name.setTypeface(myTypeface);
        name.setText(user.getFullName());
        email.setText(user.getEmail());
        userLogin.setText(user.getLoginName());
    }

    @Override
    public void onLoadUserDetailsFail(){
        String errorMessage = getString(R.string.error_downloading_users_profile);
        Snackbar snackbar = Snackbar.make(avatar, errorMessage, Snackbar.LENGTH_INDEFINITE).setAction("RETRY", new View.OnClickListener() {
            @Override
            public void onClick(View view){
                if (avatar.getDrawable() == null) {
                    isSharedElementOn = false;
                    downloadProfileImage();
                }
                if (name.getText().length() == 0) {
                    mActionsListener.getUserDetails(loginName);
                }
            }
        });
        View sbView = snackbar.getView();
        TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
        Drawable cloudError = ResourcesCompat.getDrawable(getResources(), R.drawable.icon_cloud_error, null);
        textView.setCompoundDrawablesWithIntrinsicBounds(cloudError, null, null, null);
        textView.setCompoundDrawablePadding(getResources().getDimensionPixelOffset(R.dimen.margin_normal));
        snackbar.show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        switch (item.getItemId()) {
            case android.R.id.home:
                finishAfterTransition();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSuccess(){
        if (isSharedElementOn) {
            startPostponedEnterTransition();
            startRevealAnimation(getGradient(), 200);
        } else {
            startRevealAnimation(getGradient(), 0);
        }
    }

    @Override
    public void onError(){
        if (isSharedElementOn) {
            startPostponedEnterTransition();
        }
        onLoadUserDetailsFail();
    }

    @Override
    public void onBackPressed(){
        mActionsListener.onBackPressed();
        super.onBackPressed();
    }
}


