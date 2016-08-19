package demo.victormunoz.githubusers.ui.userdetail;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import demo.victormunoz.githubusers.api.GitHubApi;
import demo.victormunoz.githubusers.model.User;
import demo.victormunoz.githubusers.utils.espresso.EspressoIdlingResource;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.google.common.base.Preconditions.checkNotNull;

public class UserDetailPresenter implements UserDetailContract.UserActionsListener, Callback<User> {

    private final UserDetailContract.View mUsersView;

    public UserDetailPresenter(@NonNull UserDetailContract.View usersView) {
        mUsersView = checkNotNull(usersView, "usersView cannot be null!");
    }
    /**
     *  Use the Retrofit library to ask for an user's information to the GitHub API.
     */
    @Override
    public void loadUserDetails(String login) {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GitHubApi.ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        // prepare call in Retrofit 2.0
        GitHubApi githubUserAPI = retrofit.create(GitHubApi.class);
        Call<User> call = githubUserAPI.getUser(
                login,
                GitHubApi.CLIENT_ID,
                GitHubApi.CLIENT_SECRET);
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
