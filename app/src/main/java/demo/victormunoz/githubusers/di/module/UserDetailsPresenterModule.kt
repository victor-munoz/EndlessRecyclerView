package demo.victormunoz.githubusers.di.module

import android.app.Activity

import dagger.Module
import dagger.Provides
import demo.victormunoz.githubusers.di.scope.ActivityScope
import demo.victormunoz.githubusers.features.userDetails.UserDetailsContract.PresenterListener
import demo.victormunoz.githubusers.features.userDetails.UserDetailsContract.UserActionsListener
import demo.victormunoz.githubusers.features.userDetails.UserDetailsPresenter
import demo.victormunoz.githubusers.model.network.GithubService

@Module(includes = [(GitHubModule::class), (PicassoModule::class), (CircleTransformationModule::class)])
class UserDetailsPresenterModule(activity: Activity) {

    private var  mUsersPresenterListener: PresenterListener = activity as PresenterListener

    @Provides
    @ActivityScope
    internal fun providesUserPresenterInterface(githubService: GithubService): UserActionsListener {
        return UserDetailsPresenter(mUsersPresenterListener, githubService)
    }

}
