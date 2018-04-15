package demo.victormunoz.githubusers.network.github

import demo.victormunoz.githubusers.di.module.GitHubModule.GitHubApiInterface
import demo.victormunoz.githubusers.model.User
import io.reactivex.Observable
import io.reactivex.Single

class GithubService(private val githubAPI: GitHubApiInterface) {
    private var sinceId = 0

    fun getUsers():Observable<List<User>> {
        return githubAPI.getUsers(sinceId)
                .doOnNext { users->
                    sinceId = users[users.size - 1].id
                }
    }

    fun getUser(login: String): Single<User> {
        return githubAPI.getUser(login)


    }


}
