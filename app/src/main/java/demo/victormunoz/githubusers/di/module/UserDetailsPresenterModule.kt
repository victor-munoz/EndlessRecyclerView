package demo.victormunoz.githubusers.di.module

import android.app.Activity
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity

import dagger.Module
import dagger.Provides
import demo.victormunoz.githubusers.di.scope.ActivityScope
import demo.victormunoz.githubusers.features.userDetails.UserDetailsContract.ViewListener
import demo.victormunoz.githubusers.features.userDetails.UserDetailsContract.PresenterListener
import demo.victormunoz.githubusers.features.userDetails.UserDetailsPresenter
import demo.victormunoz.githubusers.network.api.ApiService
import demo.victormunoz.githubusers.network.image.ImageService

@Module(includes = [(GitHubModule::class), (PicassoModule::class)])
class UserDetailsPresenterModule(activity: Activity) {

    private var viewListener = activity as ViewListener
    private var lifecycle = (activity as RxAppCompatActivity).lifecycle()

    @Provides
    @ActivityScope
    internal fun providesUserPresenterInterface(githubService: ApiService, imageService: ImageService): PresenterListener {
        return UserDetailsPresenter(viewListener, githubService, imageService, lifecycle)
    }

}
