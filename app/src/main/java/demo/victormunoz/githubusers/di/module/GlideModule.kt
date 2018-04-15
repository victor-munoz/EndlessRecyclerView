package demo.victormunoz.githubusers.di.module

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions

import dagger.Module
import dagger.Provides
import demo.victormunoz.githubusers.di.scope.ActivityScope
import demo.victormunoz.githubusers.network.image.GlideImageService
import demo.victormunoz.githubusers.network.image.ImageDownloadService

@Module(includes = [(ContextModule::class)])
class GlideModule {

    @Provides
    @ActivityScope
    internal fun providesGlide(context: Context): RequestManager {
        return Glide.with(context)
    }
    @Provides
    @ActivityScope
    internal fun provideTransformation(): RequestOptions {
        return RequestOptions.centerCropTransform().circleCrop()
    }

    @Provides
    @ActivityScope
    internal fun provideGlideService(glide: RequestManager, requestOptions: RequestOptions): ImageDownloadService {
        return GlideImageService(glide, requestOptions)
    }

}