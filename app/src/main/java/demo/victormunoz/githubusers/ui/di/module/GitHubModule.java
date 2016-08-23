package demo.victormunoz.githubusers.ui.di.module;

import java.util.List;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import demo.victormunoz.githubusers.api.model.User;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

@Module
public class GitHubModule {
    String CLIENT_ID =  "8cef3aad2dd0b1d8f007";
    String CLIENT_SECRET="5d67502bd7813f6797b349b16cae929cbba23611";

    public interface GitHubApiInterface {
        /**
         * get the 30 next users with id bigger that sinceId.
         * @param sinceId id of the last user downloaded
         * @param xxxx client_id password
         * @param yyyy client_secret passwords
         * @return retrofit callback with a list of users
         */
        @GET("/users")
        Call<List<User>> getUsers(
                @Query("since") int sinceId
                ,@Query("client_id") String xxxx
                ,@Query("client_secret") String yyyy);
        /**
         * get the information of an user
         * @param name username of the user in GitHub
         * @param xxxx client_id password
         * @param yyyy client_secret passwords
         * @return retrofit callback with the required user
         */
        @GET("users/{user}")
        Call<User> getUser(@Path("user") String name, @Query("client_id") String xxxx
                , @Query("client_secret") String yyyy);
    }

    @Provides
    public GitHubApiInterface providesGitHubInterface(Retrofit retrofit) {
        return retrofit.create(GitHubApiInterface.class);
    }
    @Provides @Named("client_id")
    public String providedClientID(){
        return CLIENT_ID;
    }
    @Provides @Named("client_secret")
    public String providedClientSecret(){
        return CLIENT_SECRET;
    }
}