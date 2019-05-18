package com.example.meiqiweather.weatherCondition

import android.annotation.SuppressLint
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
import android.graphics.Shader
import android.graphics.RadialGradient

class CloudyTypeImpl: BaseType {

    // 背景
    private var mBackground: Drawable? = null

    private var mRains: ArrayList<RainyHolder>? = null
    // 画笔
    private var mPaint: Paint? = null
    private var circleP: Paint? = null
    private var paint: Paint? = null

    private var mRecF: RectF? = null
    private var rect: RectF? = null

    constructor(context: Context, dynamicWeatherView: DynamicWeatherView): super(context, dynamicWeatherView){
        init()
    }

    private fun init(){
        mPaint = Paint()
        mPaint?.isAntiAlias = true
        mPaint?.color = Color.WHITE
        mRecF = RectF()
        mRains = ArrayList()

        circleP = Paint()
        circleP?.isAntiAlias = true
        circleP?.color = Color.parseColor("#FFFF00")
        circleP?.alpha = 200

        paint = Paint()
        paint?.strokeWidth = 20f
        paint?.style = Paint.Style.STROKE
        paint?.alpha = 50
        paint?.isAntiAlias = true
        paint?.color = Color.parseColor("#FFE500")
        rect = RectF()
    }

    override fun generate() {
        mBackground = ContextCompat.getDrawable(mContext!!, R.drawable.rain_sky_night)
        mBackground?.setBounds(0, 0, mWidth, mHeight)
        for (i in 0..5) {
            mRains?.add(
                RainyHolder(
                    -280 + 210 * i,
                    420 + 210 * i,
                    getRandom(280, 340),
                    getRandom(60, 100)
                )
            )
        }
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        clearCanvas(canvas)
        // 画背景
        mBackground?.draw(canvas)
        canvas.drawCircle(mWidth.toFloat() - 260, 260f, 130f, circleP)
        for (i in mRains!!) {
            mPaint?.alpha = i.a
            mRecF?.set(i.l.toFloat(), -340f, i.r.toFloat(), i.b.toFloat() )
            canvas.drawArc(mRecF, 0f, 180F, true, mPaint)
        }

//        rect?.set(mWidth.toFloat() - 400f, 120f , mWidth.toFloat() - 260 + 140f, 400f)
//        canvas.drawArc(rect, 0f, 360f, false, paint)


    }

    /**
     * @param l 矩形左侧的X坐标
     * @param r 矩形右侧的X坐标
     * @param b 矩形底部的Y坐标
     * @param a 透明度
     */
    data class RainyHolder(var l: Int = 0, var r: Int = 0, var b: Int = 0, var a: Int = 0)

}