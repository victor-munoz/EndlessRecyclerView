package demo.victormunoz.githubusers.ui.di.component;

import dagger.Component;
import demo.victormunoz.githubusers.ui.di.module.GitHubModule;
import demo.victormunoz.githubusers.ui.di.module.NetModule;
import demo.victormunoz.githubusers.ui.di.module.UserDetailPresenterModule;
import demo.victormunoz.githubusers.ui.userdetail.UserDetailActivity;


//@Component(dependencies = {GitHubModule.class, NetModule.class}, modules = UserDetailPresenterModule.class)
public interface UserDetailPresenterComponent {
    void inject(UserDetailActivity activity);
}
