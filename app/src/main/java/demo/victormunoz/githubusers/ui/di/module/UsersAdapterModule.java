package demo.victormunoz.githubusers.ui.di.module;


import android.app.Activity;

import com.squareup.picasso.Picasso;

import dagger.Module;
import dagger.Provides;
import demo.victormunoz.githubusers.ui.users.UsersAdapter;



@Module(includes = {PicassoModule.class})
public class UsersAdapterModule  {
    private UsersAdapter.UsersListener mItemListener;

    public UsersAdapterModule(Activity activity) {
        this.mItemListener= (UsersAdapter.UsersListener) activity;
    }

    @Provides
    UsersAdapter provideUsersAdapter(Picasso picasso) {
        return new UsersAdapter(picasso, mItemListener);
    }
}