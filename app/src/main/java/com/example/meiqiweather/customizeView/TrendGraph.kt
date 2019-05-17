package com.example.meiqiweather.customizeView

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.example.meiqiweather.data.Elements

class TrendGraph : View {

    private var paintUp: Paint? = null

    private var paintDown: Paint? = null

    private var mTextPaint: Paint? = null

    private var circlePaint: Paint? = null

    private val mElements: ArrayList<Elements> = ArrayList()

    private val truth: ArrayList<Elements> = ArrayList()

    private var max_up = 0

    private var min_down = 0

    private var circleRadius = 6f

    constructor(context: Context): this(context, null)

    constructor(context: Context, attrs: AttributeSet?): super(context, attrs){
        //上折线
        paintUp = Paint()
        paintUp?.color = Color.rgb(234, 236, 238)
        paintUp?.strokeWidth = 3.5f
        paintUp?.isAntiAlias = true

        //下折线
        paintDown = Paint()
        paintDown?.color = Color.rgb(187,255,255)
        paintDown?.strokeWidth = 3.5f
        paintDown?.isAntiAlias = true

        //文字画笔
        mTextPaint = Paint()
        mTextPaint?.textSize = 40f
        mTextPaint?.color = Color.BLUE
        mTextPaint?.isAntiAlias = true

        //画圆
        circlePaint = Paint()
        circlePaint?.color = Color.rgb(133, 193, 233)
        circlePaint?.isAntiAlias = true
    }

    override fun onDraw(canvas: Canvas) {

        if (mElements == null || mElements.size === 0) {
            return
        }

        val width = width.toFloat()
        val grap = width / mElements.size
        val textSize = mTextPaint?.textSize
        val textMargin = 5 * circleRadius
        val margin_top = textSize!! + 2 * textMargin

        val height = height - 2 * margin_top

        for (i in 0 until mElements.size -1 ) {
            val startX = i * grap + grap / 2
            val stopX = (i + 1) * grap + grap / 2
            var startY =
                (max_up - mElements[i].up).toFloat() / (max_up - min_down).toFloat() * height + margin_top
            var stopY =
                (max_up - mElements[i + 1].up).toFloat() / (max_up - min_down).toFloat() * height + margin_top

            if(i == 0){
                canvas.drawLine(0f, startY, startX, startY, paintUp)
            }

            canvas.drawLine(startX, startY, stopX, stopY, paintUp)

            canvas.drawCircle(startX, startY, circleRadius, circlePaint)

            canvas.drawText(
                (truth[i].up).toString() + "°",
                startX - textSize,
                startY - textMargin,
                mTextPaint
            )


            if (i == mElements.size - 2) {
                canvas.drawLine( stopX, stopY, width, stopY, paintUp)
                canvas.drawCircle(stopX, stopY, circleRadius, circlePaint)
                canvas.drawText(
                    truth[i + 1].up.toString() + "°",
                    stopX - textSize,
                    stopY - textMargin,
                    mTextPaint
                )
            }

            startY =
                (max_up - mElements[i].down).toFloat() / (max_up - min_down).toFloat() * height + margin_top
            stopY =
                (max_up - mElements[i + 1].down).toFloat() / (max_up - min_down).toFloat() * height + margin_top

            if(i == 0){
                canvas.drawLine(0f, startY, startX, startY, paintDown)
            }

            canvas.drawLine(startX, startY, stopX, stopY, paintDown)

            canvas.drawCircle(startX, startY, circleRadius, circlePaint)

            canvas.drawText(
                (truth[i].down ).toString() + "°",
                startX - textSize,
                startY + textSize + textMargin, mTextPaint
            )

            if (i == mElements.size - 2) {
                canvas.drawLine( stopX, stopY, width, stopY, paintDown)
                canvas.drawCircle(stopX, stopY, circleRadius, circlePaint)
                canvas.drawText(
                    truth[i + 1].down.toString() + "°",
                    stopX - textSize,
                    stopY + textSize + textMargin, mTextPaint
                )
            }
        }
    }

    fun setMElements(up: IntArray, down: IntArray){
        mElements.clear()
        truth.clear()
        for (i in 0 until up.size){
            truth.add(Elements(up[i], down[i]))
        }
        if (down.min()!! < 0){
            var ii = 0 - down.min()!!
            for (i in 0 until down.size){
                mElements.add(Elements(up[i] + ii, down[i] + ii))
            }
            max_up = up.max()!! + ii
            min_down = down.min()!! + ii
        }else{
            for (i in 0 until down.size){
                mElements.add(Elements(up[i], down[i]))
            }
            max_up = up.max()!!
            min_down = down.min()!!
        }
        invalidate()
    }

}

