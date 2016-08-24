package demo.victormunoz.githubusers.ui.di.component;
import javax.inject.Singleton;

import dagger.Component;
import demo.victormunoz.githubusers.ui.di.module.GitHubModule;
@Singleton
@Component(modules= GitHubModule.class)
public interface GitHubComponent {

    GitHubModule.GitHubApiInterface providesGitHubInterface();

}
