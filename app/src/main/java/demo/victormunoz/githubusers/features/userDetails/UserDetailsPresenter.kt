package demo.victormunoz.githubusers.features.userDetails

import android.graphics.Bitmap
import com.trello.rxlifecycle2.android.ActivityEvent
import com.trello.rxlifecycle2.kotlin.bindUntilEvent
import demo.victormunoz.githubusers.features.userDetails.UserDetailsContract.ViewListener
import demo.victormunoz.githubusers.model.User
import demo.victormunoz.githubusers.network.api.ApiService
import demo.victormunoz.githubusers.network.image.ImageService
import demo.victormunoz.githubusers.utils.espresso.EspressoIdlingResource
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.CancellationException

class UserDetailsPresenter(
        private var viewListener: ViewListener,
        private val apiService: ApiService,
        private val imageService: ImageService,
        private val lifecycle: Observable<ActivityEvent>

) : UserDetailsContract.PresenterListener {

    override fun onBackPressed() {
        // not needed yet
    }

    private data class UserDetails(var user: User, var bitmap: Bitmap)

    override fun getUserDetails(login: String, url: String) {
        val getUserImage = imageService.getImage(url, ImageService.ImageSize.BIG)
        val getUserData = apiService.getUser(login)
        val convertToUserDetails = BiFunction<User, Bitmap, UserDetails> { u, b ->
            UserDetails(u, b)
        }
        Single.zip(getUserData, getUserImage, convertToUserDetails)
                .bindUntilEvent(lifecycle, ActivityEvent.DESTROY)
                .doOnSubscribe { EspressoIdlingResource.increment() }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { userDetails ->
                            viewListener.displayUserDetails(userDetails.user, userDetails.bitmap)
                            EspressoIdlingResource.decrement()
                        }, { e ->
                            if (e !is CancellationException) {
                               viewListener.showErrorMessage()
                            }
                            EspressoIdlingResource.decrement()
                        }
                )
    }
}
