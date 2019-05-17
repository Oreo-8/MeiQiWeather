package com.example.meiqiweather.customizeView

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.example.meiqiweather.data.Line
import java.util.*
import kotlin.collections.ArrayList

class RainView: SurfaceView,SurfaceHolder.Callback,Runnable {

    //用于标注线程是否继续
    private var Flag = false

    //SurfaceHolder
    private var surfaceHolder: SurfaceHolder? = null

    //定义画笔
    private var paint = Paint()

    //雨滴的集合
    private var lines =  ArrayList<Line>()

    //Random对象 用于随机生成雨滴的X轴坐标
    private var random = Random()

    var needPaint = true

    constructor(context: Context):this(context, null)

    constructor(context: Context, attrs: AttributeSet?): super(context, attrs){
        surfaceHolder = holder
        surfaceHolder?.addCallback(this)
        //设置背景透明
        setZOrderOnTop(true)
//        setZOrderMediaOverlay(true)
        surfaceHolder?.setFormat(PixelFormat.TRANSLUCENT)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int): super(context, attrs, defStyleAttr)

    override fun surfaceCreated(holder: SurfaceHolder?) {
        //初始化画笔等
        init()

        Flag = true

        //启动线程绘制雨滴
        Thread(this).start()
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        Flag = false
    }

    override fun run() {
        var canvas: Canvas?
        var line: Line?
        var i = 0
        while (Flag) {
            try {
                canvas = surfaceHolder?.lockCanvas()
                //清空画布
                canvas!!.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
            } catch (e: Exception) {
                break
            }


            //遍历雨滴集合
            for (i in 0 until lines?.size) {
                line = lines[i]
                //绘制雨滴
                canvas!!.drawLine(line.startx.toFloat(), line.starty.toFloat(), line.stopx.toFloat(), line.stopy.toFloat(), paint)

                //绘制雨滴之后 更改雨滴的Y轴坐标 下次绘制时即可更新位置 使雨滴下落
                //取三个随机数 每个随机数代表3种不同的长度以及下落速度
                when(random.nextInt(3)){
                    0 ->{
                        line.starty = (line.starty + 30)
                        line.stopy = (line.starty + 50)
                    }
                    1 ->{
                        line.starty = (line.starty + 40)
                        line.stopy = (line.starty + 60)
                    }
                    2 ->{
                        line.starty = (line.starty + 50)
                        line.stopy = (line.starty + 40)
                    }
                    3 ->{
                        line.starty = (line.starty + 60)
                        line.stopy = (line.starty + 45)
                    }
                }

            }

            //解锁画布
            surfaceHolder?.unlockCanvasAndPost(canvas)


            //添加雨滴
//            if (i++ % 2 == 0){
//                addline()
//            }
            addline()

            //当雨滴大于100条时 删除第一个 让雨滴保持在100条
            if (lines?.size > 40) {
                lines?.removeAt(0)
            }
        }
    }

    private fun init() {
        //设置画笔颜色
        paint.color = Color.WHITE
        //抗锯齿
        paint.isAntiAlias = true
        //设置画笔颜色
        paint.color = Color.WHITE
        //设置画笔模式为填充
        paint.style = Paint.Style.FILL
        //设置画笔宽度为2
        paint.strokeWidth = 2f
    }

    //添加雨滴
    private fun addline() {
        val line = Line()
        //随机生成雨滴的起始X坐标
        line.startx = random.nextInt(width)
        //设置雨滴的起始y坐标为-60  从屏幕外开始运动
        line.starty = -60
        //雨滴偏移3个像素 看起来不会太直
        line.stopx = line.startx + 3
        //雨滴的长度
        line.stopy = line.starty + 60
        //添加到集合
        lines.add(line)
    }

}