package demo.victormunoz.githubusers.utils.picasso

import android.widget.ImageView

import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

class ImageLoader(private val picasso: Picasso, private val transformation: CircleTransformation) {
    
    fun load(url: String, view: ImageView, callback: Callback) {
        picasso.load(url).fit().tag(TAG).centerCrop().transform(transformation).into(view, callback)
    }

    fun cancelAll() {
        picasso.cancelTag(TAG)
    }

    fun cancelRequest(holder: ImageView) {
        picasso.cancelRequest(holder)
    }

    companion object {

        private val TAG = ImageLoader::class.java.simpleName
    }
}
