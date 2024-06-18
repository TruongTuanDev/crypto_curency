package com.example.testapi.config

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

class CircleImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    private val paint = Paint().apply {
        isAntiAlias = true
    }
    private var shader: BitmapShader? = null
    private var radius = 0f
    private var cx = 0f
    private var cy = 0f

    init {
        scaleType = ScaleType.CENTER_CROP
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        radius = minOf(w, h) / 2.0f
        cx = w / 2.0f
        cy = h / 2.0f
        updateShader()
    }

    override fun onDraw(canvas: Canvas) {
        if (shader == null) {
            super.onDraw(canvas)
            return
        }
        canvas.drawCircle(cx, cy, radius, paint)
    }

    private fun updateShader() {
        val bitmap = drawableToBitmap(drawable) ?: return
        shader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP).apply {
            paint.shader = this
        }
    }

    private fun drawableToBitmap(drawable: Drawable?): Bitmap? {
        if (drawable == null) return null
        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }

        val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }
}
