package demo.victormunoz.githubusers.ui.di.component;

import javax.inject.Singleton;

import dagger.Component;
import demo.victormunoz.githubusers.ui.di.module.AppModule;
import demo.victormunoz.githubusers.ui.di.module.ContextModule;
import demo.victormunoz.githubusers.ui.di.module.NetModule;
import demo.victormunoz.githubusers.ui.users.UsersActivity;
import retrofit2.Retrofit;

@Singleton
@Component(modules={
         ContextModule.class
        ,AppModule.class
        ,NetModule.class
})
public interface NetComponent {
    //Retrofit retrofit();
    void inject(UsersActivity usersActivity);


}