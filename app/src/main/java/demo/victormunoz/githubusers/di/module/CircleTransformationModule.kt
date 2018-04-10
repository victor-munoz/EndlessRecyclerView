package demo.victormunoz.githubusers.di.module

import dagger.Module
import dagger.Provides
import demo.victormunoz.githubusers.di.scope.ActivityScope
import demo.victormunoz.githubusers.utils.picasso.CircleTransformation

@Module
class CircleTransformationModule {

    @Provides
    @ActivityScope
    internal fun transformation(): CircleTransformation {
        return CircleTransformation()
    }

}
