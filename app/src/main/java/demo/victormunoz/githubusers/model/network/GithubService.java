package demo.victormunoz.githubusers.model.network;

import android.support.annotation.NonNull;

import java.util.List;

import demo.victormunoz.githubusers.di.module.GitHubModule.GitHubApiInterface;
import demo.victormunoz.githubusers.model.entity.User;
import demo.victormunoz.githubusers.utils.espresso.EspressoIdlingResource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
public class GithubService {

    private final GitHubApiInterface githubAPI;
    private int sinceId = 0;

    public GithubService(GitHubApiInterface github){
        githubAPI = github;
    }

    public void getUsers(@NonNull final UsersCallback callback){
        EspressoIdlingResource.increment();
        githubAPI.getUsers(sinceId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<User>>() {
                    @Override
                    public void onError(Throwable e) {
                        callback.fail();
                        EspressoIdlingResource.decrement();

                    }

                    @Override
                    public void onComplete(){
                        EspressoIdlingResource.decrement();
                    }

                    @Override
                    public void onSubscribe(Disposable d){

                    }

                    @Override
                    public void onNext(@NonNull List<User> users) {
                        sinceId = users.get(users.size()-1).getId();
                        callback.success(users);
                    }
                });

    }

    public void getUser(String login, @NonNull final UserCallback callback){
        EspressoIdlingResource.increment();
        githubAPI.getUser(login)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<User>() {
                    @Override
                    public void onSubscribe(Disposable d){

                    }

                    @Override
                    public void onNext(User user){
                        callback.success(user);
                    }

                    @Override
                    public void onError(Throwable e){
                        callback.fail();
                        EspressoIdlingResource.decrement();
                    }

                    @Override
                    public void onComplete(){
                        EspressoIdlingResource.decrement();
                    }
                });
    }

    public interface UserCallback {

        void success(User user);

        void fail();

    }

    public interface UsersCallback {

        void success(List<User> users);

        void fail();

    }


}
