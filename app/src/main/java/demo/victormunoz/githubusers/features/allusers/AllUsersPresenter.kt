package demo.victormunoz.githubusers.features.allusers

import android.view.View
import com.trello.rxlifecycle2.android.ActivityEvent
import com.trello.rxlifecycle2.kotlin.bindUntilEvent
import demo.victormunoz.githubusers.features.allusers.AllUsersContract.PresenterListener
import demo.victormunoz.githubusers.features.allusers.AllUsersContract.ViewListener
import demo.victormunoz.githubusers.model.User
import demo.victormunoz.githubusers.network.api.ApiService
import demo.victormunoz.githubusers.utils.espresso.EspressoIdlingResource
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.CancellationException

class AllUsersPresenter(
        private val mUsersView: PresenterListener,
        private val service: ApiService,
        private val lifecycle: Observable<ActivityEvent>

) : ViewListener {

    private var sinceId = 0

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
        service.getUsers(sinceId)
                .bindUntilEvent(lifecycle, ActivityEvent.DESTROY)
                .doOnSubscribe { EspressoIdlingResource.increment() }
                .doOnSuccess { sinceId = it.last().id }
                .filter { v->!v.isEmpty() }.toSingle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { users ->
                            mUsersView.addUsers(users)
                            EspressoIdlingResource.decrement()
                    }
                    , { e ->
                            if (e !is CancellationException) {
                                mUsersView.showError()
                            }
                            EspressoIdlingResource.decrement()
                    }

                )


    }
}
