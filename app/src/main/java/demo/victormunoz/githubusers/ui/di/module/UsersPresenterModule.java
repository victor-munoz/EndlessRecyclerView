package demo.victormunoz.githubusers.ui.di.module;

import android.support.annotation.NonNull;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import demo.victormunoz.githubusers.api.model.User;
import demo.victormunoz.githubusers.ui.users.UsersContract;
import demo.victormunoz.githubusers.utils.espresso.EspressoIdlingResource;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Module
public class UsersPresenterModule implements UsersContract.UserActionsListener, Callback<List<User>> {
    private int lastIdDownloaded = 0;
    private final UsersContract.Views mUsersView;

    GitHubModule.GitHubApiInterface githubUserAPI;
    @Inject @Named("client_id")
    String clientID;
    @Inject @Named("client_secret")
    String clientSecret;


    @Provides
    public UsersContract.UserActionsListener providesUsersPresenterInterface(GitHubModule.GitHubApiInterface github) {
        githubUserAPI=github;
        return this;
    }

    public UsersPresenterModule(@NonNull UsersContract.Views usersView) {
        mUsersView =  usersView;
    }

    /**
     *  Use the Retrofit library to ask for 30 users to the GitHub API.
     */
    @Override
    public void loadMoreUsers() {
        Call<List<User>> call = githubUserAPI.getUsers(
                lastIdDownloaded,
                clientID,
                clientSecret);
        //asynchronous call
        call.enqueue( this);
        EspressoIdlingResource.increment();
    }

    @Override
    public void onResponse(Call<List<User>> call, Response<List<User>> response) {
        if (response.isSuccessful()) {
            List<User> users = response.body();
            lastIdDownloaded = users.get(users.size()-1).getId();
            mUsersView.addUsersToAdapter(users);

        } else {
            mUsersView.onLoadMoreUsersFail();
        }
        EspressoIdlingResource.decrement();
    }

    @Override
    public void onFailure(Call<List<User>> call, Throwable t) {
        EspressoIdlingResource.decrement();
        mUsersView.onLoadMoreUsersFail();
    }
}
