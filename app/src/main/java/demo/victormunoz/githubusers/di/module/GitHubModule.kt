package demo.victormunoz.githubusers.di.module

import dagger.Module
import dagger.Provides
import demo.victormunoz.githubusers.di.scope.ActivityScope
import demo.victormunoz.githubusers.model.User
import demo.victormunoz.githubusers.network.github.GithubService
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

@Module(includes = [(RetrofitModule::class)])
class GitHubModule {

    companion object {
        private const val CLIENT_ID = "8cef3aad2dd0b1d8f007"
        private const val CLIENT_SECRET = "5d67502bd7813f6797b349b16cae929cbba23611"
    }

    @Provides
    @ActivityScope
    internal fun providesGitHubInterface(retrofit: Retrofit): GitHubApiInterface {
        return retrofit.create(GitHubApiInterface::class.java)
    }

    @Provides
    @ActivityScope
    internal fun providesGithubService(gitHubApiInterface: GitHubApiInterface): GithubService {
        return GithubService(gitHubApiInterface)
    }

    interface GitHubApiInterface {
        /**
         * get the 30 next users with id bigger that sinceId.
         *
         * @param sinceId id of the last user downloaded
         * @return an Observable with a list of users
         */
        @GET("/users?&client_id=$CLIENT_ID&client_secret=$CLIENT_SECRET")
        fun getUsers(@Query("since") sinceId: Int): Observable<List<User>>



        /**
         * get the information of an user
         *
         * @param name username of the user in GitHub
         * @return an Observable with the required user
         */
        @GET("users/{user}?&client_id=$CLIENT_ID&client_secret=$CLIENT_SECRET")
        fun getUser(@Path("user") name: String): Single<User>
    }
}