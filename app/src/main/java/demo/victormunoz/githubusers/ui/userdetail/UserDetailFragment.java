package demo.victormunoz.githubusers.ui.userdetail;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import demo.victormunoz.githubusers.R;
import demo.victormunoz.githubusers.model.User;


public class UserDetailFragment extends Fragment {
    @BindView(R.id.nestedScroll)
    NestedScrollView nestedScrollView;
    @BindView(R.id.user_bio)
    TextView bio;
    @BindView(R.id.user_blog)
    Button blog;
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
    @BindView(R.id.user_github)
    Button github;
    @BindView(R.id.user_repos)
    TextView repos;
    @BindView(R.id.user_hireable)
    TextView hireable;
    @BindView(R.id.user_location)
    TextView location;

    public UserDetailFragment() {
    }

    @Override
    public android.view.View onCreateView(LayoutInflater inflater, ViewGroup container,
                                          Bundle savedInstanceState) {
        android.view.View root = inflater.inflate(R.layout.fragment_user_detail, container, false);
        ButterKnife.bind(this, root);
        return root;
    }


    public static UserDetailFragment newInstance() {
        return new UserDetailFragment();
    }

    public void displayUserInfo(final User user) {
        nestedScrollView.setVisibility(View.VISIBLE);
        followers.setText(user.getFollowers());
        following.setText(user.getFollowing());
        gists.setText(user.getGists());
        repos.setText(user.getRepos());
        joined.setText(getResources().getString(R.string.joined_on,user.getJoinedOn()));
        if(user.getBiography()!=null) {
            bio.setVisibility(View.VISIBLE);
            bio.setText(user.getBiography());
        }
        if(user.getBlog()!=null) {
            blog.setVisibility(View.VISIBLE);
            blog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent browserIntent = new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(user.getBlog()));
                    startActivity(browserIntent);
                }
            });
        }
        if(user.getCompany()!=null) {
            company.setVisibility(View.VISIBLE);
            company.setText(user.getCompany());
        }
        if(user.isHireable()){
            hireable.setVisibility(View.VISIBLE);
            hireable.setText(R.string.available_for_hire);
        }
        if(user.getLocation()!=null) {
            location.setVisibility(View.VISIBLE);
            location.setText(user.getLocation());
        }
        github.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(user.getGitHubProfileUrl())
                );
                startActivity(browserIntent);
            }
        });



    }

}
