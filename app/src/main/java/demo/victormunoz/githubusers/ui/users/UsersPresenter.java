package demo.victormunoz.githubusers.ui.users;

import android.support.annotation.NonNull;

import java.util.List;

import demo.victormunoz.githubusers.api.model.User;
import demo.victormunoz.githubusers.ui.di.module.GitHubModule.GitHubApiInterface;
import demo.victormunoz.githubusers.utils.espresso.EspressoIdlingResource;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class UsersPresenter implements UsersContract.UserActionsListener, Callback<List<User>> {
    private int lastIdDownloaded = 0;
    private final UsersContract.Views mUsersView;
   GitHubApiInterface githubUserAPI;




    public UsersPresenter(@NonNull UsersContract.Views activity, GitHubApiInterface github) {
        mUsersView = activity;
        githubUserAPI=github;
    }

    /**
     *  Use the Retrofit library to ask for 30 users to the GitHub API.
     */
    @Override
    public void loadMoreUsers() {
        Call<List<User>> call = githubUserAPI.getUsers(lastIdDownloaded);
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
