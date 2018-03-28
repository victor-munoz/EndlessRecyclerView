package demo.victormunoz.githubusers.di.module;

import android.support.annotation.NonNull;

import dagger.Module;
import dagger.Provides;
import demo.victormunoz.githubusers.di.scope.ActivityScope;
import demo.victormunoz.githubusers.utils.picasso.CircleTransformation;

@SuppressWarnings("WeakerAccess")
@Module
public class CircleTransformationModule {
    @Provides
    @ActivityScope
    @NonNull
    CircleTransformation transformation(){
        return new CircleTransformation();
    }
}
