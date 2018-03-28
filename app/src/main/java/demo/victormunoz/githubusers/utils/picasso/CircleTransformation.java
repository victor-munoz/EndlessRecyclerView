package demo.victormunoz.githubusers.utils.picasso;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.squareup.picasso.Transformation;

/**
 * transformation to be used with picasso library to change the shape of a bitmap to a circle.
 */
public class CircleTransformation implements Transformation {
    /**
     * transform a bitmap into a circle
     *
     * @param source bitmap to be transform
     * @return circle image bitmap
     */
    @Nullable
    @Override
    public Bitmap transform(@Nullable Bitmap source){
        if (source == null || source.isRecycled()) {
            return null;
        }

        final int width = source.getWidth();
        final int height = source.getHeight();

        Bitmap canvasBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        BitmapShader shader = new BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(shader);

        Canvas canvas = new Canvas(canvasBitmap);
        float radius = width > height ? ((float) height) / 2f : ((float) width) / 2f;
        canvas.drawCircle(width / 2, height / 2, radius, paint);

        if (canvasBitmap != source) {
            source.recycle();
        }

        return canvasBitmap;
    }

    /**
     * @return a unique key for the transformation, used for caching purposes.
     */
    @NonNull
    @Override
    public String key(){
        return "imageToCircle";
    }
}