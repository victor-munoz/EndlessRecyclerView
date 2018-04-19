package demo.victormunoz.githubusers.di.module

import android.content.Context
import dagger.Module
import dagger.Provides
import demo.victormunoz.githubusers.di.scope.ActivityScope
import demo.victormunoz.githubusers.features.allusers.AllUsersAdapter
import demo.victormunoz.githubusers.features.allusers.AllUsersContract.AdapterListener

@Module(includes = [(ContextModule::class)])
class AllUsersAdapterModule {

    @Provides
    @ActivityScope
    internal fun provideUsersAdapter(context: Context): AllUsersAdapter {
        val adapterListener = context as AdapterListener
        return AllUsersAdapter(adapterListener)
    }

}