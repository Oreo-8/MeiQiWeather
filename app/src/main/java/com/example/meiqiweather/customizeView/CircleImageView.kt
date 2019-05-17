package com.example.meiqiweather.customizeView

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.support.v7.widget.AppCompatImageView
import android.util.AttributeSet

class CircleImageView: AppCompatImageView {

    private var width: Float = 0.toFloat()
    private var height: Float = 0.toFloat()
    private var radius: Float = 0.toFloat()
    private var paint: Paint? = null
    private var mx: Matrix? = null

    constructor(context: Context): this(context, null)

    constructor(context: Context, attrs: AttributeSet?): this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int): super(context, attrs, defStyleAttr){
        paint = Paint()
        paint?.isAntiAlias = true   //设置抗锯齿
        mx = Matrix()      //初始化缩放矩阵
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        width = measuredWidth.toFloat()
        height = measuredHeight.toFloat()
        radius = Math.min(width, height) / 2
    }

    override fun onDraw(canvas: Canvas?) {
        val drawable = drawable
        if (drawable == null) {
            super.onDraw(canvas)
            return
        }
        if (drawable is BitmapDrawable) {
            paint?.shader = initBitmapShader(drawable)//将着色器设置给画笔
            paint?.style = Paint.Style.FILL_AND_STROKE
            canvas?.drawCircle(width / 2, height / 2, radius, paint)//使用画笔在画布上画圆
            return
        }
        super.onDraw(canvas)
    }

    /**
     * 获取ImageView中资源图片的Bitmap，利用Bitmap初始化图片着色器,通过缩放矩阵将原资源图片缩放到铺满整个绘制区域，避免边界填充
     */
    private fun initBitmapShader(drawable: BitmapDrawable): BitmapShader {
        val bitmap = drawable.bitmap
        val bitmapShader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        val scale = Math.max(width / bitmap.width, height / bitmap.height)
        mx?.setScale(scale, scale)//将图片宽高等比例缩放，避免拉伸
        bitmapShader.setLocalMatrix(mx)
        return bitmapShader
    }
}