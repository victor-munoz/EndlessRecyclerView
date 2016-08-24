package demo.victormunoz.githubusers.ui.di.module;

import java.util.List;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import demo.victormunoz.githubusers.api.model.User;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

@Module(includes = {RetrofitModule.class})
public class GitHubModule {
    final static String CLIENT_ID =  "8cef3aad2dd0b1d8f007";
    final static String CLIENT_SECRET="5d67502bd7813f6797b349b16cae929cbba23611";

    public interface GitHubApiInterface {
        /**
         * get the 30 next users with id bigger that sinceId.
         * @param sinceId id of the last user downloaded
         * @return retrofit callback with a list of users
         */
        @GET("/users?&client_id="+CLIENT_ID+"&client_secret="+CLIENT_SECRET)
        Call<List<User>> getUsers(@Query("since") int sinceId );
        /**
         * get the information of an user
         * @param name username of the user in GitHub
         * @return retrofit callback with the required user
         */
        @GET("users/{user}?&client_id="+CLIENT_ID+"&client_secret="+CLIENT_SECRET)
        Call<User> getUser(@Path("user") String name);
    }

    @Provides
    @Singleton
    public GitHubApiInterface providesGitHubInterface(Retrofit retrofit) {
        return retrofit.create(GitHubApiInterface.class);
    }
}