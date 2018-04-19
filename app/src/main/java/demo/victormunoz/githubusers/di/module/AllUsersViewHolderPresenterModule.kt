package demo.victormunoz.githubusers.di.module

import android.content.Context
import android.support.v7.widget.RecyclerView
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity

import dagger.Module
import dagger.Provides
import demo.victormunoz.githubusers.di.scope.ActivityScope
import demo.victormunoz.githubusers.features.allusers.AllUsersContract
import demo.victormunoz.githubusers.features.allusers.AllUsersViewHolderPresenter
import demo.victormunoz.githubusers.network.image.ImageService

@Module(includes = [(PicassoModule::class)])
class AllUsersViewHolderPresenterModule(viewHolder: RecyclerView.ViewHolder) {

    private var viewHolderListener = viewHolder as AllUsersContract.ViewHolderListener

    @Provides
    @ActivityScope
    internal fun providesUsersViewHolderPresenterInterface(context: Context, imageService: ImageService): AllUsersContract.ViewHolderPresenterListener {
        val activityListener = context as AllUsersContract.ActivityNavigationListener
        val lifecycle = (context as RxAppCompatActivity).lifecycle()
        return AllUsersViewHolderPresenter(viewHolderListener, activityListener, imageService, lifecycle)
    }

}
