package demo.victormunoz.githubusers.ui.di.component;

import com.squareup.picasso.Picasso;

import dagger.Component;
import demo.victormunoz.githubusers.ui.di.module.PicassoModule;

@Component(modules= PicassoModule.class)
public interface PicassoComponent {
    Picasso providesPicasso();
}
