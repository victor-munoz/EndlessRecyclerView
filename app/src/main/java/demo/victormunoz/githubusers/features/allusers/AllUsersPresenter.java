package demo.victormunoz.githubusers.features.allusers;

import android.support.annotation.NonNull;
import android.view.View;

import java.util.List;

import demo.victormunoz.githubusers.features.allusers.AllUsersContract.PresenterListener;
import demo.victormunoz.githubusers.features.allusers.AllUsersContract.ViewListener;
import demo.victormunoz.githubusers.model.entity.User;
import demo.victormunoz.githubusers.model.network.GithubService;

public class AllUsersPresenter implements ViewListener {

    @NonNull
    private final PresenterListener mUsersView;
    private final GithubService service;


    public AllUsersPresenter(@NonNull PresenterListener activity, GithubService service){
        mUsersView = activity;
        this.service = service;
        loadMore();
    }

    @Override
    public void onEndOfTheList(){
        loadMore();
    }

    @Override
    public void onRetry(){
        loadMore();
    }

    @Override
    public void onItemClick(View view, @NonNull User user){
        mUsersView.goToUserDetails(view, user.getLoginName(), user.getAvatarUrl());
    }

    private void loadMore(){
        service.getUsers(new GithubService.UsersCallback() {
            @Override
            public void success(List<User> users){
                mUsersView.addUsers(users);
            }

            @Override
            public void fail(){
                mUsersView.showError();
            }
        });
    }
}
