package demo.victormunoz.githubusers.di.module

import android.content.Context

import com.squareup.picasso.Picasso

import dagger.Module
import dagger.Provides
import demo.victormunoz.githubusers.di.scope.ActivityScope
import demo.victormunoz.githubusers.utils.picasso.CircleTransformation
import demo.victormunoz.githubusers.utils.picasso.ImageLoader

@Module(includes = [(ContextModule::class)])
class PicassoModule {

    @Provides
    @ActivityScope
    internal fun providesPicasso(context: Context): Picasso {
        return Picasso.with(context)
    }

    @Provides
    @ActivityScope
    internal fun provideImageLoader(picasso: Picasso, transformation: CircleTransformation): ImageLoader {
        return ImageLoader(picasso, transformation)
    }

}