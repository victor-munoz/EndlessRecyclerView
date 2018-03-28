package demo.victormunoz.githubusers.di.module;

import android.content.Context;
import android.support.annotation.NonNull;

import com.squareup.picasso.Picasso;

import dagger.Module;
import dagger.Provides;
import demo.victormunoz.githubusers.di.scope.ActivityScope;
import demo.victormunoz.githubusers.utils.picasso.CircleTransformation;
import demo.victormunoz.githubusers.utils.picasso.ImageLoader;

@SuppressWarnings("WeakerAccess")
@Module(includes = {ContextModule.class})
public class PicassoModule {

    @Provides
    @ActivityScope
    @NonNull
    Picasso providesPicasso(Context context){
        return Picasso.with(context);
    }

    @Provides
    @ActivityScope
    @NonNull
    ImageLoader provideImageLoader(Picasso picasso, CircleTransformation transformation){
        return new ImageLoader(picasso, transformation);
    }
}