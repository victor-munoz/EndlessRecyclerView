package demo.victormunoz.githubusers.di.module

import android.app.Activity
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity

import dagger.Module
import dagger.Provides
import demo.victormunoz.githubusers.di.scope.ActivityScope
import demo.victormunoz.githubusers.features.allusers.AllUsersContract.ViewListener
import demo.victormunoz.githubusers.features.allusers.AllUsersContract.PresenterListener
import demo.victormunoz.githubusers.features.allusers.AllUsersPresenter
import demo.victormunoz.githubusers.network.api.ApiService

@Module(includes = [(GitHubModule::class)])
class AllUsersPresenterModule(activity: Activity) {

    private var viewListener = activity as ViewListener
    private var lifecycle = (activity as RxAppCompatActivity).lifecycle()

    @Provides
    @ActivityScope
    internal fun providesUsersPresenterInterface(github: ApiService): PresenterListener {
        return AllUsersPresenter(viewListener, github, lifecycle)
    }

}
