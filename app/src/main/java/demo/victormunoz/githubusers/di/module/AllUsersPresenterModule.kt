package demo.victormunoz.githubusers.di.module

import android.app.Activity
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity

import dagger.Module
import dagger.Provides
import demo.victormunoz.githubusers.di.scope.ActivityScope
import demo.victormunoz.githubusers.features.allusers.AllUsersContract.PresenterListener
import demo.victormunoz.githubusers.features.allusers.AllUsersContract.ViewListener
import demo.victormunoz.githubusers.features.allusers.AllUsersPresenter
import demo.victormunoz.githubusers.network.github.GithubService

@Module(includes = [(GitHubModule::class)])
class AllUsersPresenterModule(activity: Activity) {

    private var  mUsersView = activity as PresenterListener
    private var  lifecycle = (activity as RxAppCompatActivity).lifecycle()

    @Provides
    @ActivityScope
    internal fun providesUsersPresenterInterface(github: GithubService): ViewListener {
        return AllUsersPresenter(mUsersView, github, lifecycle)
    }

}
