package demo.victormunoz.githubusers.network.api

import demo.victormunoz.githubusers.di.module.GitHubModule.GitHubApiInterface
import demo.victormunoz.githubusers.model.User
import io.reactivex.Observable
import io.reactivex.Single

class GithubService(private val githubAPI: GitHubApiInterface) : ApiService {

    override fun getUsers(sinceId: Int): Observable<List<User>> {
        return githubAPI.getUsers(sinceId)
    }

    override fun getUser(id: String): Single<User> {
        return githubAPI.getUser(id)


    }


}
