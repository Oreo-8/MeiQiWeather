package com.example.meiqiweather.weatherCondition

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import com.example.meiqiweather.R
import com.example.meiqiweather.customizeView.BaseType
import com.example.meiqiweather.customizeView.DynamicWeatherView
import android.graphics.Bitmap

/**
 * 晴天
 */
class ClearTypeImpl: BaseType{

    // 背景
    private var mBackground: Drawable? = null

    private var mPaint: Paint? = null

    private var b: Bitmap? = null

    private var i = 0

    constructor(context: Context, dynamicWeatherView: DynamicWeatherView): super(context, dynamicWeatherView){
        mPaint = Paint()
        mPaint?.isAntiAlias = true
        mPaint?.alpha = 200
        b = BitmapFactory.decodeResource(mContext?.resources, R.drawable.sun)
    }

    override fun generate() {
        mBackground = ContextCompat.getDrawable(mContext!!, R.drawable.rain_sky_night)
        mBackground?.setBounds(0, 0, mWidth, mHeight)
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        clearCanvas(canvas)
        // 画背景
        mBackground?.draw(canvas)

        val matrix = Matrix()
        matrix.postRotate(i++.toFloat())
        val newBmp = Bitmap.createBitmap(b, 0, 0, b!!.width, b!!.height, matrix, true)
        var k = newBmp.width - b!!.width
        val bmp = Bitmap.createBitmap(newBmp, k / 2, k / 2, b!!.width, b!!.width)
        canvas.drawBitmap(bmp, mWidth.toFloat() - 480f,30f, mPaint)
    }
}