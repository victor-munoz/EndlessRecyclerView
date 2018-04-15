package demo.victormunoz.githubusers.di.module

import android.app.Activity

import dagger.Module
import dagger.Provides
import demo.victormunoz.githubusers.di.scope.ActivityScope
import demo.victormunoz.githubusers.features.allusers.AllUsersAdapter
import demo.victormunoz.githubusers.features.allusers.AllUsersContract.AdapterListener
import demo.victormunoz.githubusers.network.image.ImageDownloadService

@Module(includes = [(GlideModule::class)])
class UsersAdapterModule(activity: Activity) {

    private val mAdapterListener: AdapterListener = activity as AdapterListener

    @Provides
    @ActivityScope
    internal fun provideUsersAdapter(picasso: ImageDownloadService): AllUsersAdapter {
        return AllUsersAdapter(picasso, mAdapterListener)
    }

}