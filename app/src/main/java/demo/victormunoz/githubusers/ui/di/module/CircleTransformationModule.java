package demo.victormunoz.githubusers.ui.di.module;

import dagger.Module;
import dagger.Provides;
import demo.victormunoz.githubusers.utils.picasso.ImageToCircleTransformation;

@Module
public class CircleTransformationModule {
    @Provides
    public ImageToCircleTransformation transformation(){
        return new ImageToCircleTransformation();
    }
}
