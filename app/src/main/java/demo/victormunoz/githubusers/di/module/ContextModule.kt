package demo.victormunoz.githubusers.di.module

import android.content.Context

import dagger.Module
import dagger.Provides
import demo.victormunoz.githubusers.di.scope.ActivityScope

@Module
class ContextModule(private val context: Context) {

    @Provides
    @ActivityScope
    internal fun provideContext(): Context {
        return context
    }

}
