package demo.victormunoz.githubusers.di.module

import android.app.Activity

import dagger.Module
import dagger.Provides
import demo.victormunoz.githubusers.di.scope.ActivityScope
import demo.victormunoz.githubusers.features.allusers.AllUsersContract.PresenterListener
import demo.victormunoz.githubusers.features.allusers.AllUsersContract.ViewListener
import demo.victormunoz.githubusers.features.allusers.AllUsersPresenter
import demo.victormunoz.githubusers.model.network.GithubService

@Module(includes = [(GitHubModule::class)])
class AllUsersPresenterModule(activity: Activity) {

    private var  mUsersView: PresenterListener = activity as PresenterListener

    @Provides
    @ActivityScope
    internal fun providesUsersPresenterInterface(github: GithubService): ViewListener {
        return AllUsersPresenter(mUsersView, github)
    }

}
