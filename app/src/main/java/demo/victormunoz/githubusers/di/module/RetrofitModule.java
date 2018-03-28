package demo.victormunoz.githubusers.di.module;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import dagger.Module;
import dagger.Provides;
import demo.victormunoz.githubusers.di.scope.ActivityScope;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@SuppressWarnings("WeakerAccess")
@Module
public class RetrofitModule {
    private final static String BASE_URL = "https://api.github.com";

    @Provides
    @ActivityScope
    @NonNull
    Gson provideGson(){
        return new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
    }

    @Provides
    @ActivityScope
    @NonNull
    Retrofit provideRetrofit(@NonNull Gson gson){
        return new Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }


}