package demo.victormunoz.githubusers.network.image

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import io.reactivex.Single

class GlideImageService(private val glide: RequestManager, private val transformation: RequestOptions): ImageDownloadService {

    override fun getImage(url: String,  imageSize: ImageDownloadService.ImageSize): Single<Bitmap> {
        return Single.create<Bitmap> { emitter ->
            try {
                if (!emitter.isDisposed) {
                    val size = imageSize.getWidth()
                    val bitmap = glide.load(url).apply(transformation).submit(size,size).get().toBitmap()
                    emitter.onSuccess(bitmap)

                }
            } catch (e: Throwable) {
                emitter.onError(e)
            }
        }
    }

}

fun Drawable.toBitmap(): Bitmap {
    if (this is BitmapDrawable) {
        return bitmap
    }

    val width = if (bounds.isEmpty) intrinsicWidth else bounds.width()
    val height = if (bounds.isEmpty) intrinsicHeight else bounds.height()

    return Bitmap.createBitmap(width.nonZero(), height.nonZero(), Bitmap.Config.ARGB_8888).also {
        val canvas = Canvas(it)
        setBounds(0, 0, canvas.width, canvas.height)
        draw(canvas)
    }
}

private fun Int.nonZero() = if (this <= 0) 1 else this