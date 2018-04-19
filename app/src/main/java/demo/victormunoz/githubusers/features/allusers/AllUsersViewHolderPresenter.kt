package demo.victormunoz.githubusers.features.allusers

import android.view.View
import com.trello.rxlifecycle2.android.ActivityEvent
import com.trello.rxlifecycle2.kotlin.bindUntilEvent
import demo.victormunoz.githubusers.model.User
import demo.victormunoz.githubusers.network.image.ImageService
import demo.victormunoz.githubusers.utils.espresso.EspressoIdlingResource
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.CancellationException

class AllUsersViewHolderPresenter(
        private val viewHolderListener: AllUsersContract.ViewHolderListener,
        private val activityListener: AllUsersContract.ActivityNavigationListener,
        private val imageService: ImageService,
        private val lifecycle: Observable<ActivityEvent>

) : AllUsersContract.ViewHolderPresenterListener {


    override fun onItemClick(view: View, user: User) {
        activityListener.goToUserDetails(view, user)
    }

    override fun onImageRequest(url: String, size: Int) {
        imageService.getImage(url,  size)
                .bindUntilEvent(lifecycle, ActivityEvent.DESTROY)
                .doOnSubscribe { EspressoIdlingResource.increment() }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { bitmap ->
                            viewHolderListener.showImage(bitmap)
                            EspressoIdlingResource.decrement()
                        },
                        { e ->
                            if (e !is CancellationException) {
                                viewHolderListener.showError()
                            }
                            EspressoIdlingResource.decrement()
                        })
    }
}
