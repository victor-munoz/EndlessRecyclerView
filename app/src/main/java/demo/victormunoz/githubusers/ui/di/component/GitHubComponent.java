package demo.victormunoz.githubusers.ui.di.component;

import dagger.Component;
import demo.victormunoz.githubusers.ui.di.module.GitHubModule;
import demo.victormunoz.githubusers.ui.users.UsersActivity;

//@Component(dependencies = NetComponent.class, modules= GitHubModule.class)
public interface GitHubComponent {
    //GitHubModule.GitHubApiInterface providesGitHubInterface();
    void inject(UsersActivity activity);
}