package demo.victormunoz.githubusers.network.image

import android.graphics.Bitmap
import android.widget.ImageView
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation
import io.reactivex.Single

class PicassoService(private val picasso: Picasso, private val transformation: Transformation): ImageDownloadService {

    override fun getImage(url: String, imageSize: ImageDownloadService.ImageSize): Single<Bitmap> {
        return Single.create<Bitmap> { emitter ->
            try {
                if (!emitter.isDisposed) {
                    val size = imageSize.getWidth()
                    val bitmap: Bitmap = picasso.load(url)
                            .tag(TAG)
                            .resize(size,size)
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

    fun cancelAll() {
        picasso.cancelTag(TAG) //todo  shoul i remove this tags?
    }

    fun cancelRequest(holder: ImageView) {
        picasso.cancelRequest(holder) //todo
    }

    companion object {

        private val TAG = PicassoService::class.java.simpleName
    }
}
