package demo.victormunoz.githubusers.ui.di.module;

import android.content.Context;

import com.squareup.picasso.Picasso;

import dagger.Module;
import dagger.Provides;

@Module(includes = {ContextModule.class})
public class PicassoModule {
    @Provides
    public Picasso providesPicasso(Context context) {
        return Picasso.with(context);
    }
}