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
 * 下雪
 */
class SnowTypeImpl: BaseType {

    // 背景
    private var mBackground: Drawable? = null
    // 雪滴集合
    private var mRains: ArrayList<SnowHolder>? = null
    // 画笔
    private var mPaint: Paint? = null

    constructor(context: Context, dynamicWeatherView: DynamicWeatherView): super(context, dynamicWeatherView){
        init()
    }

    private fun init() {
        mPaint = Paint()
        mPaint?.isAntiAlias = true
        mPaint?.color = Color.WHITE
        mRains = ArrayList()
    }

    override fun generate() {
        mBackground = ContextCompat.getDrawable(mContext!!, R.drawable.snow_sky)
        mBackground?.setBounds(0, 0, mWidth, mHeight)
        for (i in 0..149) {
            val rain = SnowHolder(
                getRandom(1, mWidth),
                getRandom(1, mHeight),
                getRandom(5, 10),
                getRandom(2, 5),
                getRandom(90, 100)
            )
            mRains?.add(rain)
        }
    }

    override fun onDraw(canvas: Canvas) {
        clearCanvas(canvas)
        // 画背景
        mBackground?.draw(canvas)
        // 画出集合中的雪点
        for (i in mRains!!) {
            mPaint?.alpha = i.a
            canvas.drawCircle(i.x.toFloat(), i.y.toFloat(), i.r.toFloat(), mPaint)
        }
        for (i in mRains!!) {
            i.y += i.s
            if (i.y > mHeight) {
                i.y = -i.s
            }
        }
    }

    /**
     * @param x 雪点 x 轴坐标
     * @param y 雪点 y 轴坐标
     * @param s 雪点移动速度
     * @param a 雪点透明度
     */
    data class SnowHolder(val x :Int, var y: Int, var r: Int, val s: Int, val a: Int)
}