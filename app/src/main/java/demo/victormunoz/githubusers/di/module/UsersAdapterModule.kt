package demo.victormunoz.githubusers.di.module

import android.app.Activity

import dagger.Module
import dagger.Provides
import demo.victormunoz.githubusers.di.scope.ActivityScope
import demo.victormunoz.githubusers.features.allusers.AllUsersAdapter
import demo.victormunoz.githubusers.features.allusers.AllUsersContract.AdapterListener
import demo.victormunoz.githubusers.network.image.ImageService

@Module(includes = [(PicassoModule::class)])
class UsersAdapterModule(activity: Activity) {

    private val mAdapterListener: AdapterListener = activity as AdapterListener

    @Provides
    @ActivityScope
    internal fun provideUsersAdapter(imageService: ImageService): AllUsersAdapter {
        return AllUsersAdapter(imageService, mAdapterListener)
    }

}