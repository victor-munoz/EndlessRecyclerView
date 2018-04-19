package demo.victormunoz.githubusers.features.allusers

import com.trello.rxlifecycle2.android.ActivityEvent
import com.trello.rxlifecycle2.kotlin.bindUntilEvent
import demo.victormunoz.githubusers.features.allusers.AllUsersContract.ActivityListener
import demo.victormunoz.githubusers.features.allusers.AllUsersContract.ActivityPresenterListener
import demo.victormunoz.githubusers.network.api.ApiService
import demo.victormunoz.githubusers.utils.espresso.EspressoIdlingResource
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.CancellationException

class AllUsersActivityPresenter(
        private val activityListener: ActivityListener,
        private val apiService: ApiService,
        private val lifecycle: Observable<ActivityEvent>

) : ActivityPresenterListener {


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

    private fun loadMore() {
        apiService.getUsers(sinceId)
                .bindUntilEvent(lifecycle, ActivityEvent.DESTROY)
                .doOnSubscribe { EspressoIdlingResource.increment() }
                .doOnSuccess { sinceId = it.last().id }
                .filter { v->!v.isEmpty() }.toSingle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { users ->
                            activityListener.showUsers(users)
                            EspressoIdlingResource.decrement()
                    }
                    , { e ->
                            if (e !is CancellationException) {
                                activityListener.showError()
                            }
                            EspressoIdlingResource.decrement()
                    }

                )
    }
}
