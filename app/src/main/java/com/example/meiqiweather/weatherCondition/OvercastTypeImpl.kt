package com.example.meiqiweather.weatherCondition

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import com.example.meiqiweather.R
import com.example.meiqiweather.customizeView.BaseType
import com.example.meiqiweather.customizeView.DynamicWeatherView

/**
 * 阴天
 */
class OvercastTypeImpl: BaseType {

    // 背景
    private var mBackground: Drawable? = null

    private var mRains: ArrayList<CloudyTypeImpl.RainyHolder>? = null
    // 画笔
    private var mPaint: Paint? = null

    private var mRecF: RectF? = null

    constructor(context: Context, dynamicWeatherView: DynamicWeatherView): super(context, dynamicWeatherView){
        init()
    }

    private fun init() {
        mPaint = Paint()
        mPaint?.isAntiAlias = true
        mPaint?.color = Color.WHITE
        mRecF = RectF()
        mRains = ArrayList()
    }

    override fun generate() {
        mBackground = ContextCompat.getDrawable(mContext!!, R.drawable.rain_sky_night)
        mBackground?.setBounds(0, 0, mWidth, mHeight)
        for (i in 0..5) {
            mRains?.add(
                CloudyTypeImpl.RainyHolder(
                    -280 + 210 * i,
                    420 + 210 * i,
                    getRandom(280, 340),
                    getRandom(60, 100)
                )
            )
        }
    }

    override fun onDraw(canvas: Canvas) {
        clearCanvas(canvas)
        // 画背景
        mBackground?.draw(canvas)

        for (i in mRains!!) {
            mPaint?.alpha = i.a
            mRecF?.set(i.l.toFloat(), -340f, i.r.toFloat(), i.b.toFloat() )
            canvas.drawArc(mRecF, 0f, 180F, true, mPaint)
        }
    }

}