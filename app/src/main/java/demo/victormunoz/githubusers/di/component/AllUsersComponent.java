package demo.victormunoz.githubusers.di.component;

import dagger.Component;
import demo.victormunoz.githubusers.di.module.AllUsersPresenterModule;
import demo.victormunoz.githubusers.di.module.UsersAdapterModule;
import demo.victormunoz.githubusers.di.scope.ActivityScope;
import demo.victormunoz.githubusers.features.allusers.AllUsersActivity;

@ActivityScope
@Component(modules = {AllUsersPresenterModule.class, UsersAdapterModule.class})
public interface AllUsersComponent {
    void inject(AllUsersActivity allUsersActivity);
}
