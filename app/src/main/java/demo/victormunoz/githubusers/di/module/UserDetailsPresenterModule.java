package demo.victormunoz.githubusers.di.module;

import android.app.Activity;
import android.support.annotation.NonNull;

import dagger.Module;
import dagger.Provides;
import demo.victormunoz.githubusers.di.scope.ActivityScope;
import demo.victormunoz.githubusers.features.userDetails.UserDetailsContract.PresenterListener;
import demo.victormunoz.githubusers.features.userDetails.UserDetailsContract.UserActionsListener;
import demo.victormunoz.githubusers.features.userDetails.UserDetailsPresenter;
import demo.victormunoz.githubusers.model.network.GithubService;

@Module(includes = {GitHubModule.class, PicassoModule.class, CircleTransformationModule.class})
public class UserDetailsPresenterModule {
    @NonNull
    private final PresenterListener mUsersPresenterListener;


    public UserDetailsPresenterModule(@NonNull Activity activity){
        mUsersPresenterListener = (PresenterListener) activity;
    }

    @Provides
    @ActivityScope
    @NonNull
    UserActionsListener providesUserPresenterInterface(GithubService githubService){
        return new UserDetailsPresenter(mUsersPresenterListener, githubService);
    }


}
