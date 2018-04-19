package demo.victormunoz.githubusers.network.image

import android.graphics.Bitmap
import io.reactivex.Single


interface ImageService {

    fun getImage(url: String, width: Int): Single<Bitmap>
}