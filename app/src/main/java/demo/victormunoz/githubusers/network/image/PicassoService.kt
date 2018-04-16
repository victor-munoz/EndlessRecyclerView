package demo.victormunoz.githubusers.network.image

import android.graphics.Bitmap
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation
import io.reactivex.Single

class PicassoService(private val picasso: Picasso, private val transformation: Transformation) : ImageService {

    override fun getImage(url: String, imageSize: ImageService.ImageSize): Single<Bitmap> {
        return Single.create<Bitmap> { emitter ->
            try {
                if (!emitter.isDisposed) {
                    val size = imageSize.getWidth()
                    val bitmap: Bitmap = picasso.load(url)
                            .resize(size, size)
                            .centerCrop()
                            .noFade()
                            .transform(transformation).get()
                    emitter.onSuccess(bitmap)

                }
            } catch (e: Throwable) {
                emitter.onError(e)
            }
        }
    }
}
