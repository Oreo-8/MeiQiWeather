package com.example.meiqiweather.customizeView

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PorterDuff
import java.util.*


open abstract class BaseType: DynamicWeatherView.WeatherType {

    var mContext: Context? = null
    var mWidth: Int = 0
    var mHeight: Int = 0

    /**
     * 生成元素
     */
    abstract fun generate()

    constructor(context: Context, dynamicWeatherView: DynamicWeatherView) {
        mContext = context
        mWidth = dynamicWeatherView.mViewWidth
        mHeight = dynamicWeatherView.mViewHeight
    }

    override fun onSizeChanged(context: Context, w: Int, h: Int) {
        mWidth = w
        mHeight = h
        // SurfaceView 的大小改变时需要根据宽高重新生成元素(例如雨滴)
        generate()
    }

    /**
     * 清空画布
     *
     * @param canvas
     */
    protected fun clearCanvas(canvas: Canvas) {
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
    }

    /**
     * 获取给定两数之间的一个随机数
     *
     * @param min 最小值
     * @param max 最大值
     * @return 介于最大值和最小值之间的一个随机数
     */
    protected fun getRandom(min: Int, max: Int): Int {
        return if (max < min) {
            1
        } else min + Random().nextInt(max - min)
    }

    fun dp2px(dpValue: Float): Int {
        val scale = mContext!!.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

}