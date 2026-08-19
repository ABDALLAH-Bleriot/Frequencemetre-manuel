package com.example.comptegouttes

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
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

        // Fond sombre TRES transparent pour bien voir l'interface
        paint.style = Paint.Style.FILL
        paint.color = Color.parseColor("#4D090E22")
        path.rewind()
        path.fillType = Path.FillType.EVEN_ODD
        path.addRect(0f, 0f, width.toFloat(), height.toFloat(), Path.Direction.CW)
        if (hole != null) path.addRoundRect(hole, 28f, 28f, Path.Direction.CW)
        canvas.drawPath(path, paint)

        if (hole != null) {
            // Cadre cyan lumineux autour du bouton
            paint.style = Paint.Style.STROKE
            paint.color = Color.parseColor("#35DFFF")
            paint.strokeWidth = 6f
            canvas.drawRoundRect(hole, 28f, 28f, paint)

            // Flèche qui montre le bouton
            paint.style = Paint.Style.FILL
            paint.color = Color.parseColor("#35DFFF")
            paint.textSize = 70f
            paint.textAlign = Paint.Align.CENTER
            if (hole.centerY() > height / 2f) {
                canvas.drawText("▲", hole.centerX(), hole.top - 10f, paint)
            } else {
                canvas.drawText("▼", hole.centerX(), hole.bottom + 70f, paint)
            }
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
