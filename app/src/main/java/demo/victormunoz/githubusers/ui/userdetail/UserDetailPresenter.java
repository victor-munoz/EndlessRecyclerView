package demo.victormunoz.githubusers.ui.userdetail;
import android.support.annotation.NonNull;
import demo.victormunoz.githubusers.api.model.User;
import demo.victormunoz.githubusers.ui.di.module.GitHubModule.GitHubApiInterface;
import demo.victormunoz.githubusers.utils.espresso.EspressoIdlingResource;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserDetailPresenter implements UserDetailContract.UserActionsListener, Callback<User> {
    private final UserDetailContract.View mUsersView;
   GitHubApiInterface githubUserAPI;


    public UserDetailPresenter(@NonNull UserDetailContract.View activity, GitHubApiInterface github) {
        mUsersView =  activity;
        githubUserAPI=github;
    }


    /**
     *  Use the Retrofit library to ask for an user's information to the GitHub API.
     */
    @Override
    public void loadUserDetails(String login) {
        // prepare call in Retrofit 2.0
        Call<User> call = githubUserAPI.getUser(login);
        //asynchronous call
        call.enqueue(this);
        EspressoIdlingResource.increment();

    }

    @Override
    public void onResponse(Call<User> call, Response<User> response) {
        EspressoIdlingResource.decrement();
        if (response.isSuccessful()) {
            User user = response.body();
            mUsersView.displayUserDetails(user);
        } else {
            mUsersView.onLoadUserDetailsFail();
        }
    }

    @Override
    public void onFailure(Call<User> call, Throwable t) {
        EspressoIdlingResource.decrement();
        mUsersView.onLoadUserDetailsFail();

    }

}
