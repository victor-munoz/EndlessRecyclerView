package demo.victormunoz.githubusers.model.network

import demo.victormunoz.githubusers.di.module.GitHubModule.GitHubApiInterface
import demo.victormunoz.githubusers.model.entity.User
import demo.victormunoz.githubusers.utils.espresso.EspressoIdlingResource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class GithubService(private val githubAPI: GitHubApiInterface) {
    private var sinceId = 0

    fun getUsers(callback: UsersCallback) {
        EspressoIdlingResource.increment()
        githubAPI.getUsers(sinceId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { users ->
                                sinceId = users[users.size - 1].id
                                callback.success(users)
                        }
                        ,{ _->
                                callback.fail()
                                EspressoIdlingResource.decrement() }
                        ,{ EspressoIdlingResource.decrement() }
                )
    }

    fun getUser(login: String, callback: UserCallback) {
        EspressoIdlingResource.increment()
        githubAPI.getUser(login)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {user->callback.success(user)}
                        ,{_->
                                callback.fail()
                                EspressoIdlingResource.decrement()}
                        ,{EspressoIdlingResource.decrement()}
                )

    }

    interface UserCallback {

        fun success(user: User)

        fun fail()

    }

    interface UsersCallback {

        fun success(users: List<User>)

        fun fail()

    }


}
