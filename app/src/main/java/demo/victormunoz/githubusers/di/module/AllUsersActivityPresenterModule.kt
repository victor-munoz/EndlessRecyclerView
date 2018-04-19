package demo.victormunoz.githubusers.di.module

import android.content.Context
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity

import dagger.Module
import dagger.Provides
import demo.victormunoz.githubusers.di.scope.ActivityScope
import demo.victormunoz.githubusers.features.allusers.AllUsersContract.ActivityListener
import demo.victormunoz.githubusers.features.allusers.AllUsersContract.ActivityPresenterListener
import demo.victormunoz.githubusers.features.allusers.AllUsersActivityPresenter
import demo.victormunoz.githubusers.network.api.ApiService

@Module(includes = [(ContextModule::class),(GitHubModule::class)])
class AllUsersActivityPresenterModule {

    @Provides
    @ActivityScope
    internal fun providesAllUsersActivityPresenterInterface(context: Context, apiService: ApiService): ActivityPresenterListener {
        val activityListener = context as ActivityListener
        val lifecycle = (context as RxAppCompatActivity).lifecycle()
        return AllUsersActivityPresenter(activityListener, apiService, lifecycle)
    }

}
