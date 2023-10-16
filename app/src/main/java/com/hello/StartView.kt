package com.hello

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Random


class TouchImageView : AppCompatImageView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        scope.launch(Dispatchers.Default) {
            while (true) {
                delay(100)
                invalidate()
            }
        }
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)


    var drawLineTo = true
    private val path = Path()
    private val paint = Paint().apply {
        isAntiAlias = true
        strokeWidth = 80f
        style = Paint.Style.STROKE
        color = Color.parseColor("#88FF0000")
    }

    private val prev = PointF()

    private val line = Path()
    private val linePaint = Paint().apply {
        isAntiAlias = true
        strokeWidth = 40f
        style = Paint.Style.STROKE
        color = Color.parseColor("#AA0000FF")
    }

    private val points = arrayListOf<PointF>()

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                path.moveTo(event.x, event.y)
                prev.set(event.x, event.y)
                line.moveTo(event.x, event.y)
            }

            MotionEvent.ACTION_MOVE -> {
                path.quadTo(prev.x, prev.y, (event.x + prev.x) / 2f, (event.y + prev.y) / 2f)
                prev.set(event.x, event.y)
                line.lineTo(event.x, event.y)
                points.add(PointF(event.x, event.y))
            }

            else -> {
//                path.reset()
//                line.reset()
//                points.clear()
//                invalidate()
            }
        }
        return true
    }

    private val m = Matrix()
    private val stars by lazy {
        val opt = BitmapFactory.Options().apply {
            inSampleSize = 20
        }
        BitmapFactory.decodeResource(resources, R.drawable.stars, opt)
    }
    private val r = Random()
    private val scaleLevels = floatArrayOf(
        0.5f,
        0.75f,
        1f,
        1.25f,
        1.5f,
        2f
    )

    private val translateXLevel = floatArrayOf(
        -25f,
        0f,
        25f,

        )

    private val prevPoint = PointF()
    private var count = 0
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawPath(path, paint)
        if (drawLineTo)
            canvas?.drawPath(line, linePaint)
        count = 0
        points.forEachIndexed { index, _ ->
            val p = points[points.size - 1 - index]
            if (index == 0) prevPoint.set(p)
            val canDraw = if (index == 0 || index == points.size - 1) {
                true
            } else {
                val distance = Math.sqrt(Math.pow((p.x - prevPoint.x).toDouble(), 2.0) + Math.pow((p.y - prevPoint.y).toDouble(), 2.0))
                val b = (distance > 30.0)
                if (b) {
                    prevPoint.set(p)
                }
                b

            }
            count += if (canDraw) 1 else 0
            if (canDraw) {
                m.reset()
                val scale = Math.max(1.5f - 0.1f * index, 0f)
//                val scale = scaleLevels[r.nextInt(scaleLevels.size)]
                m.postScale(scale, scale)
                m.postTranslate(p.x + translateXLevel[r.nextInt(translateXLevel.size)], p.y)
                m.postRotate(r.nextFloat())
                canvas.drawBitmap(stars, m, paint)
            }
        }
        println("points.size = ${points.size}, $count")

        points.takeIf { it.isNotEmpty() }?.removeFirst()
    }
}
