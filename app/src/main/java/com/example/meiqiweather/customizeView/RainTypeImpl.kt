package com.example.meiqiweather.customizeView

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import com.example.meiqiweather.R

class RainTypeImpl: BaseType {

    constructor(context: Context, dynamicWeatherView: DynamicWeatherView): super(context, dynamicWeatherView){
        init()
    }

    // 背景
    private var mBackground: Drawable? = null
    // 雨滴集合
    private var mRains: ArrayList<RainHolder>? = null
    // 画笔
    private var mPaint: Paint? = null

    private var r: RainHolder? = null

    private fun init() {
        mPaint = Paint()
        mPaint?.isAntiAlias = true
        mPaint?.color = Color.WHITE
        // 这里雨滴的宽度统一为3
        mPaint?.strokeWidth = 3F
        mRains = ArrayList()
    }

    override fun generate() {

        mBackground = ContextCompat.getDrawable(mContext!!, R.drawable.rain_sky_night)
        mBackground?.setBounds(0, 0, mWidth, mHeight)
        for (i in 0..59) {
            val rain = RainHolder(
                getRandom(1, mWidth),
                getRandom(1, mHeight),
                getRandom(dp2px(9f), dp2px(15f)),
                getRandom(dp2px(5f), dp2px(9f)),
                getRandom(20, 100)
            )

            mRains?.add(rain)
        }
    }

    override fun onDraw(canvas: Canvas) {
        clearCanvas(canvas)
        // 画背景
        mBackground?.draw(canvas)
        // 画出集合中的雨点
        for (i in 0 until mRains?.size!!) {
            r = mRains!![i]
            mPaint?.alpha = r!!.a
            canvas.drawLine(r!!.x.toFloat(), r!!.y.toFloat(), r!!.x.toFloat(), (r!!.y + r!!.l).toFloat(), mPaint)
        }
        // 将集合中的点按自己的速度偏移
        for (i in 0 until mRains!!.size) {
            r = mRains!![i]
            r!!.y += r!!.s
            if (r!!.y > mHeight) {
                r!!.y = -r!!.l
            }
        }
    }


    private data class RainHolder(val x :Int, var y: Int, val l: Int, val s: Int, val a: Int)
}