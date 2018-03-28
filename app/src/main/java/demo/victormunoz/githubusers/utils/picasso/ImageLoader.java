package demo.victormunoz.githubusers.utils.picasso;

import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class ImageLoader {

    private final static String TAG = ImageLoader.class.getSimpleName();
    private final CircleTransformation transformation;
    private final Picasso picasso;

    public ImageLoader(Picasso picasso, CircleTransformation transformation){
        this.transformation = transformation;
        this.picasso = picasso;
    }

    public void load(@NonNull String url, @NonNull ImageView view){
        picasso.load(url).fit().tag(TAG).centerCrop().transform(transformation).into(view);
    }

    public void load(@NonNull String url, @NonNull ImageView view, @NonNull Callback callback){
        picasso.load(url).fit().tag(TAG).centerCrop().transform(transformation).into(view, callback);
    }

    public void cancelAll(){
        picasso.cancelTag(TAG);
    }

    public void cancelRequest(ImageView holder){
        picasso.cancelRequest(holder);
    }
}
