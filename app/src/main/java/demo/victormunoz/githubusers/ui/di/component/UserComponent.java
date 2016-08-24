package demo.victormunoz.githubusers.ui.di.component;

import javax.inject.Singleton;

import dagger.Component;
import demo.victormunoz.githubusers.ui.di.module.UserPresenterModule;
import demo.victormunoz.githubusers.ui.userdetail.UserDetailActivity;
@Singleton
@Component(
        modules= UserPresenterModule.class)
public interface UserComponent {
    void inject(UserDetailActivity activity);
}
