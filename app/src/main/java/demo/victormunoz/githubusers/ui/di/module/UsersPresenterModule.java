package demo.victormunoz.githubusers.ui.di.module;
import android.app.Activity;

import android.support.annotation.NonNull;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import demo.victormunoz.githubusers.ui.users.UsersContract.UserActionsListener;
import demo.victormunoz.githubusers.ui.users.UsersContract.Views;
import demo.victormunoz.githubusers.ui.users.UsersPresenter;
import demo.victormunoz.githubusers.ui.di.module.GitHubModule.GitHubApiInterface;


@Module(includes = {GitHubModule.class})
public class UsersPresenterModule {
    private final Views mUsersView;
    @Provides @Singleton
    public UserActionsListener providesUsersPresenterInterface(
          GitHubApiInterface github) {
        return new UsersPresenter(mUsersView,github);
    }
    public UsersPresenterModule(@NonNull Activity activity) {
        mUsersView = (Views) activity;
    }
}
