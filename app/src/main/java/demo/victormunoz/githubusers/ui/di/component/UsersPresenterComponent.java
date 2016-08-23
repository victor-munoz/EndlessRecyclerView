package demo.victormunoz.githubusers.ui.di.component;

import dagger.Component;
import demo.victormunoz.githubusers.ui.di.module.GitHubModule;
import demo.victormunoz.githubusers.ui.di.module.NetModule;
import demo.victormunoz.githubusers.ui.di.module.UsersPresenterModule;
import demo.victormunoz.githubusers.ui.users.UsersActivity;


//@UserScope
//@Component(dependencies = {GitHubModule.class,NetModule.class},modules = UsersPresenterModule.class)
public interface UsersPresenterComponent {
    //void inject(UsersActivity activity);

}
