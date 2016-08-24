package demo.victormunoz.githubusers.ui.di.component;

import javax.inject.Singleton;

import dagger.Component;
import demo.victormunoz.githubusers.ui.di.module.RetrofitModule;
import retrofit2.Retrofit;

@Singleton
@Component(modules={RetrofitModule.class})
public interface RetrofitComponent {
    Retrofit retrofit();
}
