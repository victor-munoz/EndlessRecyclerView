package demo.victormunoz.githubusers.model.network

import demo.victormunoz.githubusers.di.module.GitHubModule.GitHubApiInterface
import demo.victormunoz.githubusers.model.entity.User
import demo.victormunoz.githubusers.utils.espresso.EspressoIdlingResource
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class GithubService(private val githubAPI: GitHubApiInterface) {
    private var sinceId = 0

    fun getUsers(callback: UsersCallback) {
        EspressoIdlingResource.increment()
        githubAPI.getUsers(sinceId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<List<User>> {
                    override fun onError(e: Throwable) {
                        callback.fail()
                        EspressoIdlingResource.decrement()
                    }

                    override fun onComplete() {
                        EspressoIdlingResource.decrement()
                    }

                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onNext(users: List<User>) {
                        sinceId = users[users.size - 1].id
                        callback.success(users)
                    }
                })

    }

    fun getUser(login: String, callback: UserCallback) {
        EspressoIdlingResource.increment()
        githubAPI.getUser(login)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<User> {
                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onNext(user: User) {
                        callback.success(user)
                    }

                    override fun onError(e: Throwable) {
                        callback.fail()
                        EspressoIdlingResource.decrement()
                    }

                    override fun onComplete() {
                        EspressoIdlingResource.decrement()
                    }
                })
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
