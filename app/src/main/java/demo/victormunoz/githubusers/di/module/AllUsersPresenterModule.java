package demo.victormunoz.githubusers.di.module;

import android.app.Activity;
import android.support.annotation.NonNull;

import dagger.Module;
import dagger.Provides;
import demo.victormunoz.githubusers.di.scope.ActivityScope;
import demo.victormunoz.githubusers.features.allusers.AllUsersContract.PresenterListener;
import demo.victormunoz.githubusers.features.allusers.AllUsersContract.ViewListener;
import demo.victormunoz.githubusers.features.allusers.AllUsersPresenter;
import demo.victormunoz.githubusers.model.network.GithubService;

@Module(includes = {GitHubModule.class})
public class AllUsersPresenterModule {
    @NonNull
    private final PresenterListener mUsersView;

    public AllUsersPresenterModule(@NonNull Activity activity){
        mUsersView = (PresenterListener) activity;
    }

    @Provides
    @ActivityScope
    @NonNull
    ViewListener providesUsersPresenterInterface(GithubService github){
        return new AllUsersPresenter(mUsersView, github);
    }
}
