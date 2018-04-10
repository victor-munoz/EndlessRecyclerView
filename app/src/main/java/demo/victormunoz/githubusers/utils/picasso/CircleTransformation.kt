package demo.victormunoz.githubusers.utils.picasso

import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Shader

import com.squareup.picasso.Transformation

/**
 * transformation to be used with picasso library to change the shape of a bitmap to a circle.
 */
class CircleTransformation : Transformation {
    /**
     * transform a bitmap into a circle
     *
     * @param source bitmap to be transform
     * @return circle image bitmap
     */
    override fun transform(source: Bitmap?): Bitmap? {
        if (source == null || source.isRecycled) {
            return null
        }

        val width = source.width
        val height = source.height

        val canvasBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val shader = BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        val paint = Paint()
        paint.isAntiAlias = true
        paint.shader = shader

        val canvas = Canvas(canvasBitmap)
        val radius = if (width > height) height.toFloat() / 2f else width.toFloat() / 2f
        canvas.drawCircle((width / 2).toFloat(), (height / 2).toFloat(), radius, paint)

        if (canvasBitmap != source) {
            source.recycle()
        }

        return canvasBitmap
    }

    /**
     * @return a unique key for the transformation, used for caching purposes.
     */
    override fun key(): String {
        return "imageToCircle"
    }
}