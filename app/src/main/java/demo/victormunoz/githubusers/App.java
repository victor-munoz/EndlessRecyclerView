package demo.victormunoz.githubusers;

import android.app.Activity;
import android.app.Application;
import android.content.res.Resources;
import android.support.annotation.NonNull;

import com.squareup.leakcanary.LeakCanary;

import demo.victormunoz.githubusers.di.component.AllUsersComponent;
import demo.victormunoz.githubusers.di.component.DaggerAllUsersComponent;
import demo.victormunoz.githubusers.di.component.DaggerUserDetailsComponent;
import demo.victormunoz.githubusers.di.component.UserDetailsComponent;
import demo.victormunoz.githubusers.di.module.AllUsersPresenterModule;
import demo.victormunoz.githubusers.di.module.ContextModule;
import demo.victormunoz.githubusers.di.module.UserDetailsPresenterModule;
import demo.victormunoz.githubusers.di.module.UsersAdapterModule;

public class App extends Application {

    public static Resources mResources;

    @Override
    public void onCreate(){
        super.onCreate();
        setLeakCanary();
        mResources = getResources();
    }

    @NonNull
    public AllUsersComponent getUsersComponent(@NonNull Activity activity){
        return DaggerAllUsersComponent.builder().contextModule(new ContextModule(activity)).usersAdapterModule(new UsersAdapterModule(activity)).allUsersPresenterModule(new AllUsersPresenterModule(activity)).build();
    }

    @NonNull
    public UserDetailsComponent getUserComponent(@NonNull Activity activity){
        return DaggerUserDetailsComponent.builder().contextModule(new ContextModule(activity)).userDetailsPresenterModule(new UserDetailsPresenterModule(activity)).build();
    }

    private void setLeakCanary(){
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);
    }


}