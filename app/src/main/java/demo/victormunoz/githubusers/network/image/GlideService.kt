package demo.victormunoz.githubusers.network.image

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import io.reactivex.Single

class GlideService(private val glide: RequestManager, private val transformation: RequestOptions) : ImageService {

    override fun getImage(url: String, width: Int): Single<Bitmap> {
        return Single.create<Bitmap> { emitter ->
            try {
                if (!emitter.isDisposed) {
                    val bitmapDrawable = glide.load(url)
                            .apply(transformation)
                            .submit(width, width)
                            .get() as BitmapDrawable
                    emitter.onSuccess(bitmapDrawable.bitmap)
                }
            } catch (e: Throwable) {
                emitter.onError(e)
            }
        }
    }

}