package demo.victormunoz.githubusers.features.userDetails;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import demo.victormunoz.githubusers.features.userDetails.UserDetailsContract.PresenterListener;
import demo.victormunoz.githubusers.model.entity.User;
import demo.victormunoz.githubusers.model.network.GithubService;

public class UserDetailsPresenter implements UserDetailsContract.UserActionsListener {
    private final GithubService githubService;
    @Nullable
    private PresenterListener mUsersPresenterListener;


    public UserDetailsPresenter(@NonNull PresenterListener activity, GithubService service){
        mUsersPresenterListener = activity;
        this.githubService = service;
    }

    public void onBackPressed(){
        mUsersPresenterListener = null;
    }

    @Override
    public void getUserDetails(String login){
        githubService.getUser(login, new GithubService.UserCallback() {
            @Override
            public void success(User user){
                if (mUsersPresenterListener != null) {
                    mUsersPresenterListener.displayUserDetails(user);
                }
            }

            @Override
            public void fail(){
                if (mUsersPresenterListener != null) {
                    mUsersPresenterListener.onLoadUserDetailsFail();
                }
            }
        });

    }
}
