package demo.victormunoz.githubusers.di.module;

import android.app.Activity;
import android.support.annotation.NonNull;

import dagger.Module;
import dagger.Provides;
import demo.victormunoz.githubusers.di.scope.ActivityScope;
import demo.victormunoz.githubusers.features.allusers.AllUsersAdapter;
import demo.victormunoz.githubusers.features.allusers.AllUsersContract.AdapterListener;
import demo.victormunoz.githubusers.utils.picasso.ImageLoader;

@Module(includes = {PicassoModule.class, CircleTransformationModule.class})
public class UsersAdapterModule {
    @NonNull
    private final AdapterListener mAdapterListener;

    public UsersAdapterModule(Activity activity){
        this.mAdapterListener = (AdapterListener) activity;
    }

    @Provides
    @ActivityScope
    @NonNull
    AllUsersAdapter provideUsersAdapter(ImageLoader picasso){
        return new AllUsersAdapter(picasso, mAdapterListener);
    }
}