package demo.victormunoz.githubusers.features.userDetails

import android.graphics.Bitmap
import com.trello.rxlifecycle2.android.ActivityEvent
import com.trello.rxlifecycle2.kotlin.bindUntilEvent
import demo.victormunoz.githubusers.features.userDetails.UserDetailsContract.ActivityListener
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

class UserDetailsActivityPresenter(
        private val activityListener: ActivityListener,
        private val lifecycle: Observable<ActivityEvent>,
        private val apiService: ApiService,
        private val imageService: ImageService

) : UserDetailsContract.ActivityPresenterListener {

    override fun onBackPressed() {
        // not needed yet
    }

    private data class UserDetails(var user: User, var bitmap: Bitmap)

    override fun getUserDetails(login: String, url: String, imageWidth: Int) {
        val getUserImage = imageService.getImage(url, imageWidth)
        val getUserData = apiService.getUser(login)
        val convertToUserDetails = BiFunction(::UserDetails)
        Single.zip(getUserData, getUserImage, convertToUserDetails)
                .bindUntilEvent(lifecycle, ActivityEvent.DESTROY)
                .doOnSubscribe { EspressoIdlingResource.increment() }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { userDetails ->
                            activityListener.displayUserDetails(userDetails.user, userDetails.bitmap)
                            EspressoIdlingResource.decrement()
                        }, { e ->
                            if (e !is CancellationException) {
                               activityListener.showErrorMessage()
                            }
                            EspressoIdlingResource.decrement()
                        }
                )
    }
}
