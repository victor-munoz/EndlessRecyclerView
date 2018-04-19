package demo.victormunoz.githubusers.di.module

import android.content.Context
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity

import dagger.Module
import dagger.Provides
import demo.victormunoz.githubusers.di.scope.ActivityScope
import demo.victormunoz.githubusers.features.userDetails.UserDetailsContract.ActivityListener
import demo.victormunoz.githubusers.features.userDetails.UserDetailsContract.ActivityPresenterListener
import demo.victormunoz.githubusers.features.userDetails.UserDetailsActivityPresenter
import demo.victormunoz.githubusers.network.api.ApiService
import demo.victormunoz.githubusers.network.image.ImageService

@Module(includes = [(GitHubModule::class), (PicassoModule::class)])
class UserDetailsActivityPresenterModule {

    @Provides
    @ActivityScope
    internal fun providesUserPresenterInterface(context: Context, apiService: ApiService, imageService: ImageService): ActivityPresenterListener {
        val viewListener = context as ActivityListener
        val lifecycle = (context as RxAppCompatActivity).lifecycle()
        return UserDetailsActivityPresenter(viewListener, lifecycle, apiService, imageService)
    }

}
