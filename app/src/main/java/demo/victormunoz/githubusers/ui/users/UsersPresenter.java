package demo.victormunoz.githubusers.ui.users;
import android.support.annotation.NonNull;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.List;
import demo.victormunoz.githubusers.api.GitHubApi;
import demo.victormunoz.githubusers.model.User;
import demo.victormunoz.githubusers.utils.espresso.EspressoIdlingResource;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UsersPresenter implements UsersContract.UserActionsListener, Callback<List<User>> {
    private final UsersContract.Views mUsersView;
    private int lastIdDownloaded = 0;

    public UsersPresenter(@NonNull UsersContract.Views usersView) {
        mUsersView = usersView;
    }

    /**
     *  Use the Retrofit library to ask for 30 users to the GitHub API.
     */
    @Override
    public void loadMoreUsers() {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GitHubApi.ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        // prepare call in Retrofit 2.0
        GitHubApi githubUserAPI = retrofit.create(GitHubApi.class);
        Call<List<User>> call = githubUserAPI.getUsers(
                lastIdDownloaded,
                GitHubApi.CLIENT_ID,
                GitHubApi.CLIENT_SECRET);
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
