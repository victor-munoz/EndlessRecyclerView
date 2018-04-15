package demo.victormunoz.githubusers.di.module

import android.content.Context

import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation

import dagger.Module
import dagger.Provides
import demo.victormunoz.githubusers.di.scope.ActivityScope
import demo.victormunoz.githubusers.network.image.ImageDownloadService
import demo.victormunoz.githubusers.network.image.PicassoService
import jp.wasabeef.picasso.transformations.CropCircleTransformation

@Module(includes = [(ContextModule::class)])
class PicassoModule {

    @Provides
    @ActivityScope
    internal fun transformationPicasso(): Transformation {
        return CropCircleTransformation()
    }

    @Provides
    @ActivityScope
    internal fun providesPicasso(context: Context): Picasso {
        return Picasso.with(context)
    }

    @Provides
    @ActivityScope
    internal fun providePicassoService(picasso: Picasso, transformation: Transformation): ImageDownloadService {
        return PicassoService(picasso, transformation)
    }

}