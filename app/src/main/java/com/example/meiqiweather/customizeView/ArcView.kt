package com.example.meiqiweather.customizeView

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator

class ArcView: View {

    private lateinit var mArcPaint: Paint  //根据数据显示的圆弧Paint

    private lateinit var mTextPaint: Paint  //文字描述的paint

    private var startAngle: Float = 135f  //圆弧开始的角度

    private var mAngle: Float = 270f  //圆弧背景的开始和结束间的夹角大小

    private var mIncludedAngle: Float = 0f  //当前进度夹角大小

    private var mStrokeWith: Float = 10f  //圆弧的画笔的宽度

    private var mDes: String = ""  //中心的文字描述

    private var mAnimatorValue: Int = 0  //动画效果的数据

    //中心点的XY坐标
    private var centerX: Float = 0f
    private var centerY: Float = 0f

    private var colorCode: Int = 0

    constructor(context: Context): this(context, null)

    constructor(context: Context, attrs: AttributeSet?): this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int): super(context, attrs, defStyleAttr)

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        centerX = (width / 2).toFloat()
        centerY = (height / 2).toFloat()
        //初始化paint
        initPaint()
        //绘制弧度
        drawArc(canvas!!)
        //绘制文本
        drawText(canvas!!)
    }

    private fun drawText(canvas: Canvas) {
        val mRect = Rect()
        val mValue = mAnimatorValue.toString()
        //绘制中心的数值
        mTextPaint.getTextBounds(mValue, 0, mValue.length, mRect)
        canvas.drawText(mAnimatorValue.toString(), centerX, centerY + mRect.height(), mTextPaint)

        //绘制中心文字描述
        mTextPaint.color = Color.parseColor("#999999")
        mTextPaint.textSize = dp2px(12f)
        mTextPaint.getTextBounds(mDes, 0, mDes.length, mRect)
        canvas.drawText(mDes, centerX, centerY + (2 * mRect.height()).toFloat() + dp2px(10f), mTextPaint)
    }

    private fun drawArc(canvas: Canvas) {
        //绘制圆弧背景
        val mRectF = RectF(
            mStrokeWith + dp2px(5f),
            mStrokeWith + dp2px(5f),
            width.toFloat() - mStrokeWith - dp2px(5f),
            height - mStrokeWith
        )
        canvas.drawArc(mRectF, startAngle, mAngle, false, mArcPaint)
        //绘制当前数值对应的圆弧
        mArcPaint.color = colorCode
        //根据当前数据绘制对应的圆弧
        canvas.drawArc(mRectF, startAngle, mIncludedAngle, false, mArcPaint)
    }

    private fun setAnimation(startAngle: Float, currentAngle: Float, currentValue: Int, time: Int) {
        //绘制当前数据对应的圆弧的动画效果
        val progressAnimator = ValueAnimator.ofFloat(startAngle, currentAngle)
        progressAnimator.duration = time.toLong()
        progressAnimator.setTarget(mIncludedAngle)
        progressAnimator.addUpdateListener { animation ->
            mIncludedAngle = animation.animatedValue as Float
            //重新绘制，不然不会出现效果
            postInvalidate()
        }
        //开始执行动画
        progressAnimator.start()

        //中心数据的动画效果
        val valueAnimator = ValueAnimator.ofInt(mAnimatorValue, currentValue)
        valueAnimator.duration = 2500
        valueAnimator.interpolator = LinearInterpolator()
        valueAnimator.addUpdateListener { valueAnimator ->
            mAnimatorValue = valueAnimator.animatedValue as Int
            postInvalidate()
        }
        valueAnimator.start()
    }

    fun setValues(maxValue: Int, currentValue: Int, des: String) {
        var currentValue = currentValue
        mDes = des
        //完全覆盖
        if (currentValue > maxValue) {
            currentValue = maxValue
        }
        //计算弧度比重
        val scale = currentValue.toFloat() / maxValue
        //计算弧度
        val currentAngle = scale * mAngle
        //开始执行动画
        setAnimation(0f, currentAngle, currentValue, 2500)

        colorCode = when(currentValue){
            in 0 .. 50 -> Color.parseColor("#73FD73")
            in 51 .. 100 -> Color.parseColor("#FDFD81")
            in 101 .. 150 -> Color.parseColor("#FDB873")
            in 151 .. 200 -> Color.parseColor("#FD7373")
            in 201 .. 300 -> Color.parseColor("#AE7373")
            in 301 .. 500 -> Color.parseColor("#737373")
            in 501 .. 800 -> Color.parseColor("#555555")
            else -> Color.parseColor("#000001")
        }

    }

    private fun initPaint() {
        //圆弧的paint
        mArcPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        //抗锯齿
        mArcPaint.isAntiAlias = true
        mArcPaint.color = Color.parseColor("#666666")
        //设置透明度（数值为0-255）
        mArcPaint.alpha = 100
        //设置画笔的画出的形状
        mArcPaint.strokeJoin = Paint.Join.ROUND
        mArcPaint.strokeCap = Paint.Cap.ROUND
        //设置画笔类型
        mArcPaint.style = Paint.Style.STROKE  //只绘制图形轮廓（描边）
        mArcPaint.strokeWidth = dp2px(mStrokeWith)

        //中心文字的paint
        mTextPaint = Paint()
        mTextPaint.isAntiAlias = true
        mTextPaint.color = colorCode
        //设置文本的对齐方式
        mTextPaint.textAlign = Paint.Align.CENTER
        //mTextPaint.setTextSize(getResources().getDimensionPixelSize(R.dimen.dp_12));
        mTextPaint.textSize = dp2px(25f)

    }

    private fun dp2px(dp: Float): Float {
        val metrics = Resources.getSystem().displayMetrics
        return dp * metrics.density
    }

}