package demo.victormunoz.githubusers.ui.di.module;

import android.support.annotation.NonNull;

import javax.inject.Inject;
import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import demo.victormunoz.githubusers.api.model.User;
import demo.victormunoz.githubusers.ui.userdetail.UserDetailContract;
import demo.victormunoz.githubusers.utils.espresso.EspressoIdlingResource;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Module
public class UserDetailPresenterModule implements UserDetailContract.UserActionsListener, Callback<User> {
    private final UserDetailContract.View mUsersView;


    GitHubModule.GitHubApiInterface githubUserAPI;
    @Inject @Named("client_id")
    String clientID;
    @Inject @Named("client_secret")
    String clientSecret;

    @Provides
    public UserDetailContract.UserActionsListener providesUserDetailPresenterInterface(GitHubModule.GitHubApiInterface github) {
        githubUserAPI=github;
        return this;
    }


    public UserDetailPresenterModule(@NonNull UserDetailContract.View usersView) {
        mUsersView =  usersView;
    }


    /**
     *  Use the Retrofit library to ask for an user's information to the GitHub API.
     */
    @Override
    public void loadUserDetails(String login) {
        // prepare call in Retrofit 2.0
        Call<User> call = githubUserAPI.getUser(
                login,
                clientID,
                clientSecret);
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
