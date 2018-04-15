package demo.victormunoz.githubusers.features.allusers

import android.view.View
import com.trello.rxlifecycle2.RxLifecycle
import com.trello.rxlifecycle2.android.ActivityEvent
import demo.victormunoz.githubusers.features.allusers.AllUsersContract.PresenterListener
import demo.victormunoz.githubusers.features.allusers.AllUsersContract.ViewListener
import demo.victormunoz.githubusers.model.entity.User
import demo.victormunoz.githubusers.network.github.GithubService
import demo.victormunoz.githubusers.utils.espresso.EspressoIdlingResource
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class AllUsersPresenter(
        private val mUsersView: PresenterListener,
        private val service: GithubService,
        private val lifecycle: Observable<ActivityEvent>

) : ViewListener {

    init {
        loadMore()
    }

    override fun onEndOfTheList() {
        loadMore()
    }

    override fun onRetry() {
        loadMore()
    }

    override fun onItemClick(view: View, user: User) {
        mUsersView.goToUserDetails(view, user)
    }

    private fun loadMore() {
        EspressoIdlingResource.increment()
        service.getUsers()
                .compose(RxLifecycle.bindUntilEvent(lifecycle, ActivityEvent.DESTROY))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { users ->
                            mUsersView.addUsers(users)
                    }
                    , { _ ->
                            mUsersView.showError()
                            EspressoIdlingResource.decrement()
                    }
                    , {
                    EspressoIdlingResource.decrement()
                }
                )


    }
}
