package demo.victormunoz.githubusers.api;
import java.util.List;
import demo.victormunoz.githubusers.model.User;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * To avoid the request limit of 60 requests per hour is necessary authenticate the
 * application with a client_id and a client_secret passwords
 * @see <a href="https://developer.github.com/v3/#rate-limiting</a>
 */
public interface GitHubApi  {
    String  ENDPOINT = "https://api.github.com";
    String CLIENT_ID =  "8cef3aad2dd0b1d8f007";
    String CLIENT_SECRET="5d67502bd7813f6797b349b16cae929cbba23611";

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
