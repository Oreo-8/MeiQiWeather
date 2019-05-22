package com.example.meiqiweather.customizeView

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PixelFormat
import android.os.Build
import android.os.SystemClock
import android.support.annotation.RequiresApi
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import com.example.meiqiweather.customizeView.DynamicWeatherView.WeatherType



class DynamicWeatherView: SurfaceView, SurfaceHolder.Callback  {

    private var mContext: Context? = null
    private var mDrawThread: DrawThread? = null
    private var mHolder: SurfaceHolder? = null
    var mType: WeatherType? = null
    var mViewWidth: Int = 0
    var mViewHeight: Int = 0


    constructor(context: Context): this(context, null)

    constructor(context: Context, attrs: AttributeSet?): this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int): super(context, attrs, defStyleAttr){
        mContext = context
        mHolder = holder
        mHolder?.addCallback(this)
        mHolder?.setFormat(PixelFormat.TRANSPARENT)
    }

    interface WeatherType {
        fun onDraw(canvas: Canvas)

        fun onSizeChanged(context: Context, w: Int, h: Int)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mViewWidth = w
        mViewHeight = h
        mType?.onSizeChanged(mContext!!, w, h)
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        mDrawThread?.isRunning = false
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        mDrawThread = DrawThread()
        mDrawThread?.isRunning = true
        mDrawThread?.start()

    }

    /**
     * 绘制线程
     */
    private inner class DrawThread : Thread() {

        // 用来停止线程的标记
        var isRunning = false

        override fun run() {
            var canvas: Canvas?
            // 无限循环绘制
            while (isRunning) {
                if (mType != null && mViewWidth != 0 && mViewHeight != 0) {
                    canvas = mHolder?.lockCanvas()
                    if (canvas != null) {
                        mType?.onDraw(canvas)
                        if (isRunning) {
                            mHolder?.unlockCanvasAndPost(canvas)
                        } else {
                            // 停止线程
                            break
                        }
                        // sleep
                        SystemClock.sleep(1)
                    }
                }
            }
        }
    }

}