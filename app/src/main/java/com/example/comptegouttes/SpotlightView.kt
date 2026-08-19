package com.example.comptegouttes

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.view.View

class SpotlightView(context: Context, private val target: View?) : View(context) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val path = Path()

    init {
        post { invalidate() }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val hole = targetRect()

        // Fond sombre léger (on voit bien l'interface derrière)
        paint.style = Paint.Style.FILL
        paint.color = Color.parseColor("#4D090E22")
        paint.pathEffect = null
        path.rewind()
        path.fillType = Path.FillType.EVEN_ODD
        path.addRect(0f, 0f, width.toFloat(), height.toFloat(), Path.Direction.CW)
        if (hole != null) path.addRoundRect(hole, 24f, 24f, Path.Direction.CW)
        canvas.drawPath(path, paint)

        if (hole != null) {
            // Bordure BLANCHE en pointillés (tiré-tiré) autour du bouton
            paint.style = Paint.Style.STROKE
            paint.color = Color.WHITE
            paint.strokeWidth = 7f
            paint.pathEffect = DashPathEffect(floatArrayOf(24f, 14f), 0f)
            canvas.drawRoundRect(hole, 24f, 24f, paint)
            paint.pathEffect = null
        }
    }

    private fun targetRect(): RectF? {
        val t = target ?: return null
        if (t.width == 0 || t.height == 0) return null
        val a = IntArray(2)
        val b = IntArray(2)
        t.getLocationOnScreen(a)
        getLocationOnScreen(b)
        val pad = 14f
        return RectF(
            (a[0] - b[0]) - pad,
            (a[1] - b[1]) - pad,
            (a[0] - b[0]) + t.width + pad,
            (a[1] - b[1]) + t.height + pad
        )
    }
}
