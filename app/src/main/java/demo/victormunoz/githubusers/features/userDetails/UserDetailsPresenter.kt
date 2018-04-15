package demo.victormunoz.githubusers.features.userDetails

import android.graphics.Bitmap
import com.trello.rxlifecycle2.RxLifecycle
import com.trello.rxlifecycle2.android.ActivityEvent
import demo.victormunoz.githubusers.features.userDetails.UserDetailsContract.PresenterListener
import demo.victormunoz.githubusers.model.entity.User
import demo.victormunoz.githubusers.network.github.GithubService
import demo.victormunoz.githubusers.network.image.ImageDownloadService
import demo.victormunoz.githubusers.utils.espresso.EspressoIdlingResource
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.CancellationException

class UserDetailsPresenter(
        private var mUsersPresenterListener: PresenterListener,
        private val githubService: GithubService,
        private val imageService: ImageDownloadService,
        private val lifecycle: Observable<ActivityEvent>

) : UserDetailsContract.UserActionsListener {

    override fun onBackPressed() {
    }

    private data class UserDetails(var user: User, var bitmap: Bitmap)

    override fun getUserDetails(login: String, url: String) {
        EspressoIdlingResource.increment()

        val singleBitmap = imageService.getImage(url, ImageDownloadService.ImageSize.BIG)
        val getUser = githubService.getUser(login)
        val convertToUserDetails = BiFunction<User, Bitmap, UserDetails> { u, b ->
            UserDetails(u, b)
        }


        Single.zip(getUser, singleBitmap, convertToUserDetails)
                .compose(RxLifecycle.bindUntilEvent(lifecycle, ActivityEvent.DESTROY))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { userDetails ->
                            mUsersPresenterListener.displayUserDetails(userDetails.user, userDetails.bitmap)
                        }, { e ->
                            if (e !is CancellationException) mUsersPresenterListener.showErrorMessage() //todo: ugly
                            EspressoIdlingResource.decrement()
                        }
                )
    }
}
