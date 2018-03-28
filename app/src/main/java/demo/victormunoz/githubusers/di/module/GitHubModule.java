package demo.victormunoz.githubusers.di.module;

import android.support.annotation.NonNull;

import java.util.List;

import dagger.Module;
import dagger.Provides;
import demo.victormunoz.githubusers.di.scope.ActivityScope;
import demo.victormunoz.githubusers.model.entity.User;
import demo.victormunoz.githubusers.model.network.GithubService;
import io.reactivex.Observable;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

@Module(includes = {RetrofitModule.class})
public class GitHubModule {
    private final static String CLIENT_ID = "8cef3aad2dd0b1d8f007";
    private final static String CLIENT_SECRET = "5d67502bd7813f6797b349b16cae929cbba23611";

    @Provides
    @ActivityScope
    @NonNull
    GitHubApiInterface providesGitHubInterface(@NonNull Retrofit retrofit){
        return retrofit.create(GitHubApiInterface.class);
    }

    @Provides
    @ActivityScope
    @NonNull
    GithubService providesGithubService(GitHubApiInterface gitHubApiInterface){
        return new GithubService(gitHubApiInterface);
    }

    public interface GitHubApiInterface {
        /**
         * get the 30 next users with id bigger that sinceId.
         *
         * @param sinceId id of the last user downloaded
         * @return an Observable with a list of users
         */
        @NonNull
        @GET("/users?&client_id=" + CLIENT_ID + "&client_secret=" + CLIENT_SECRET)
        Observable<List<User>> getUsers(@Query("since") int sinceId);

        /**
         * get the information of an user
         *
         * @param name username of the user in GitHub
         * @return an Observable with the required user
         */
        @NonNull
        @GET("users/{user}?&client_id=" + CLIENT_ID + "&client_secret=" + CLIENT_SECRET)
        Observable<User> getUser(@Path("user") String name);
    }
}