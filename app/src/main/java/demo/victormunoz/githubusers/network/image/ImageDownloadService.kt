package demo.victormunoz.githubusers.network.image

import android.graphics.Bitmap
import demo.victormunoz.githubusers.App
import demo.victormunoz.githubusers.R
import io.reactivex.Single



interface ImageDownloadService {

    enum class ImageSize {
        BIG, SMALL
    }

    fun ImageSize.getWidth(): Int{
        return when (this){
            ImageDownloadService.ImageSize.BIG -> {
                App.mResources.getDimensionPixelSize(R.dimen.image_circle_large)
            }
            ImageDownloadService.ImageSize.SMALL -> {
                App.mResources.getDimensionPixelSize(R.dimen.image_circle_normal)
            }
        }
    }

    fun getImage(url: String, imageSize: ImageSize): Single<Bitmap>
}