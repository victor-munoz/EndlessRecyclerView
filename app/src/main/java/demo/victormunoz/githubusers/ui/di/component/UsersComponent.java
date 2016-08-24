package demo.victormunoz.githubusers.ui.di.component;




import javax.inject.Singleton;

import dagger.Component;
import demo.victormunoz.githubusers.ui.di.module.UsersAdapterModule;
import demo.victormunoz.githubusers.ui.di.module.UsersPresenterModule;
import demo.victormunoz.githubusers.ui.users.UsersActivity;
@Singleton
@Component(
        modules= {
                UsersPresenterModule.class,
                UsersAdapterModule.class})
public interface UsersComponent {
    void inject(UsersActivity usersActivity);
}
