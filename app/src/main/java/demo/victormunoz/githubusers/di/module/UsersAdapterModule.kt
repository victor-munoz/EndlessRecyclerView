package demo.victormunoz.githubusers.di.module

import android.app.Activity

import dagger.Module
import dagger.Provides
import demo.victormunoz.githubusers.di.scope.ActivityScope
import demo.victormunoz.githubusers.features.allusers.AllUsersAdapter
import demo.victormunoz.githubusers.features.allusers.AllUsersContract.AdapterListener
import demo.victormunoz.githubusers.utils.picasso.ImageLoader

@Module(includes = [(PicassoModule::class), (CircleTransformationModule::class)])
class UsersAdapterModule(activity: Activity) {

    private val mAdapterListener: AdapterListener = activity as AdapterListener

    @Provides
    @ActivityScope
    internal fun provideUsersAdapter(picasso: ImageLoader): AllUsersAdapter {
        return AllUsersAdapter(picasso, mAdapterListener)
    }

}