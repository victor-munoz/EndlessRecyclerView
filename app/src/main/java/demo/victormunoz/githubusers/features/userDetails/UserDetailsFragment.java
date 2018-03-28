package demo.victormunoz.githubusers.features.userDetails;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import demo.victormunoz.githubusers.R;
import demo.victormunoz.githubusers.model.entity.User;


@SuppressWarnings("WeakerAccess")
public class UserDetailsFragment extends Fragment {
    @BindView(R.id.nestedScroll)
    NestedScrollView nestedScrollView;
    @BindView(R.id.user_bio)
    TextView biography;
    @BindView(R.id.user_created_at)
    TextView joined;
    @BindView(R.id.user_company)
    TextView company;
    @BindView(R.id.user_followers)
    TextView followers;
    @BindView(R.id.user_following)
    TextView following;
    @BindView(R.id.user_gists)
    TextView gists;
    @BindView(R.id.user_repos)
    TextView repos;
    @BindView(R.id.user_hireable)
    TextView hireable;
    @BindView(R.id.user_location)
    TextView location;

    public UserDetailsFragment(){
    }

    public static UserDetailsFragment newInstance(){
        return new UserDetailsFragment();
    }

    @Override
    public android.view.View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        android.view.View root = inflater.inflate(R.layout.fragment_user_detail, container, false);
        ButterKnife.bind(this, root);
        return root;
    }

    public void displayUserInfo(@NonNull final User user){
        nestedScrollView.setVisibility(View.VISIBLE);
        followers.setText(user.getFollowers());
        following.setText(user.getFollowing());
        gists.setText(user.getTotalGists());
        repos.setText(user.getTotalRepos());
        joined.setText(getResources().getString(R.string.joined_on, user.getJoinedIn()));
        if (user.getBiography() != null) {
            biography.setVisibility(View.VISIBLE);
            biography.setText(user.getBiography());
        }
        if (user.getCompanyName() != null) {
            company.setVisibility(View.VISIBLE);
            company.setText(user.getCompanyName());
        }
        if (user.isHireable()) {
            hireable.setVisibility(View.VISIBLE);
            hireable.setText(R.string.available_for_hire);
        }
        if (user.getLocation() != null) {
            location.setVisibility(View.VISIBLE);
            location.setText(user.getLocation());
        }
    }

}
