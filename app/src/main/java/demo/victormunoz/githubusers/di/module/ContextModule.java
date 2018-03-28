package demo.victormunoz.githubusers.di.module;

import android.content.Context;
import android.support.annotation.NonNull;

import dagger.Module;
import dagger.Provides;
import demo.victormunoz.githubusers.di.scope.ActivityScope;

@Module
public class ContextModule {
    private final Context context;

    public ContextModule(Context context){
        this.context = context;
    }

    @Provides
    @ActivityScope
    @NonNull
    Context provideContext(){
        return context;
    }

}
