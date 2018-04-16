package demo.victormunoz.githubusers

import android.graphics.Bitmap
import com.trello.rxlifecycle2.android.ActivityEvent
import demo.victormunoz.githubusers.features.userDetails.UserDetailsContract
import demo.victormunoz.githubusers.features.userDetails.UserDetailsPresenter
import demo.victormunoz.githubusers.model.User
import demo.victormunoz.githubusers.network.api.ApiService
import demo.victormunoz.githubusers.network.image.ImageService
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import java.util.concurrent.CancellationException

class UserDetailsPresenterTest {
    @Mock
    private lateinit var mUsersPresenterListener: UserDetailsContract.PresenterListener
    @Mock
    private lateinit var lifecycle: Observable<ActivityEvent>
    @Mock
    private
    lateinit var imageService: ImageService
    @Mock
    private lateinit var githubService: ApiService
    @Mock
    private lateinit var bitmap: Bitmap

    @InjectMocks
    private lateinit var presenter: UserDetailsPresenter

    private val user = getUser()

    @Before
    @Throws(Exception::class)
    fun init() {
        RxAndroidPlugins.reset()
        RxJavaPlugins.reset()
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        MockitoAnnotations.initMocks(this)
    }

    private fun getUser(): User {
        return User(1, "a", "a", "q", true, "victor", "www", "q"
                , null, null, 0, 0, null, 1, 1)
    }

    @Test
    fun getUserDetails_success() {
        /* Given */
        `when`(githubService.getUser(user.loginName)).thenReturn(Single.just(user))
        `when`(imageService.getImage(user.avatarUrl, ImageService.ImageSize.BIG)).thenReturn(Single.just(bitmap))
        /* When */
        presenter.getUserDetails(user.loginName, user.avatarUrl)
        /* Then */
        verify(mUsersPresenterListener, times(1)).displayUserDetails(user, bitmap)
        verifyNoMoreInteractions(mUsersPresenterListener)

    }

    @Test
    fun getUserDetails_getUserFail() {
        /* Given */
        `when`(githubService.getUser(user.loginName)).thenReturn(Single.error(Throwable("fail")))
        `when`(imageService.getImage(user.avatarUrl, ImageService.ImageSize.BIG)).thenReturn(Single.just(bitmap))
        /* When */
        presenter.getUserDetails(user.loginName, user.avatarUrl)
        /* Then */
        verify(mUsersPresenterListener, times(1)).showErrorMessage()
        verifyNoMoreInteractions(mUsersPresenterListener)
    }

    @Test
    fun getUserDetails_getImageFail() {
        /* Given */
        `when`(githubService.getUser(user.loginName)).thenReturn(Single.just(user))
        `when`(imageService.getImage(user.avatarUrl, ImageService.ImageSize.BIG)).thenReturn(Single.error(Throwable("fail")))
        /* When */
        presenter.getUserDetails(user.loginName, user.avatarUrl)
        /* Then */
        verify(mUsersPresenterListener, times(1)).showErrorMessage()
        verifyNoMoreInteractions(mUsersPresenterListener)
    }

    @Test
    fun getUserDetails_cancelled() {
        /* Given */
        `when`(githubService.getUser(user.loginName)).thenReturn(Single.error(CancellationException()))
        `when`(imageService.getImage(user.avatarUrl, ImageService.ImageSize.BIG)).thenReturn(Single.just(bitmap))
        /* When */
        presenter.getUserDetails(user.loginName, user.avatarUrl)
        /* Then */
        verifyNoMoreInteractions(mUsersPresenterListener)
    }


}