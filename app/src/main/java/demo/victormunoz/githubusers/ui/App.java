package demo.victormunoz.githubusers.ui;

import android.app.Activity;
import android.app.Application;


import demo.victormunoz.githubusers.ui.di.component.DaggerUserComponent;
import demo.victormunoz.githubusers.ui.di.component.DaggerUsersComponent;
import demo.victormunoz.githubusers.ui.di.component.UserComponent;
import demo.victormunoz.githubusers.ui.di.component.UsersComponent;
import demo.victormunoz.githubusers.ui.di.module.ContextModule;
import demo.victormunoz.githubusers.ui.di.module.UserPresenterModule;
import demo.victormunoz.githubusers.ui.di.module.UsersAdapterModule;
import demo.victormunoz.githubusers.ui.di.module.UsersPresenterModule;

public class App extends Application {



    public UsersComponent getUsersComponent(Activity activity) {
        return DaggerUsersComponent.builder()
                .contextModule(new ContextModule(activity))
                .usersAdapterModule(new UsersAdapterModule(activity))
                .usersPresenterModule(new UsersPresenterModule(activity))
                .build();
    }

    public UserComponent getUserComponent(Activity activity) {
        return  DaggerUserComponent.builder()
                .contextModule(new ContextModule(activity))
                .userPresenterModule(new UserPresenterModule(activity))
                .build();
    }



}