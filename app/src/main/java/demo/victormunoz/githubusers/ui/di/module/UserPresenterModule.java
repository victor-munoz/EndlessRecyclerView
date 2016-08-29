package demo.victormunoz.githubusers.ui.di.module;

import android.app.Activity;
import android.support.annotation.NonNull;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import demo.victormunoz.githubusers.ui.userdetail.UserDetailContract;
import demo.victormunoz.githubusers.ui.userdetail.UserDetailPresenter;

@Module (includes = {
        GitHubModule.class,
        PicassoModule.class,
        CircleTransformationModule.class})
public class UserPresenterModule {
    private final UserDetailContract.View mUsersView;



    @Provides @Singleton
    public UserDetailContract.UserActionsListener providesUserPresenterInterface(
        GitHubModule.GitHubApiInterface github) {
        return new UserDetailPresenter(mUsersView,github);
    }


    public UserPresenterModule(@NonNull Activity activity) {
        mUsersView = (UserDetailContract.View) activity;
    }



}
