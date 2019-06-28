package com.example.meiqiweather.weatherCondition

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import com.example.meiqiweather.R
import com.example.meiqiweather.customizeView.BaseType
import com.example.meiqiweather.customizeView.DynamicWeatherView

/**
 * 下雨
 */
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

    private fun init() {
        mPaint = Paint()
        mPaint?.isAntiAlias = true
        mPaint?.color = Color.WHITE
        // 这里雨滴的宽度统一为3
        mPaint?.strokeWidth = 4F
        mRains = ArrayList()
    }

    override fun generate() {
        mBackground = ContextCompat.getDrawable(mContext!!, R.drawable.rain_sky)
        mBackground?.setBounds(0, 0, mWidth, mHeight)
        for (i in 0..99) {
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
        for (i in mRains!!) {
            mPaint?.alpha = i.a
            canvas.drawLine(i.x.toFloat(), i.y.toFloat(), i.x.toFloat(), (i.y + i.l).toFloat(), mPaint)
        }
        // 将集合中的点按自己的速度偏移
        for (i in mRains!!) {
            i.y += i.s
            if (i.y > mHeight) {
                i.y = -i.l
            }
        }
    }

    /**
     * @param x 雨点 x 轴坐标
     * @param y 雨点 y 轴坐标
     * @param l 雨点长度
     * @param s 雨点移动速度
     * @param a 雨点透明度
     */
    private data class RainHolder(val x :Int, var y: Int, val l: Int, val s: Int, val a: Int)
}