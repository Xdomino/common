package me.elvis.commonlib.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.support.annotation.DrawableRes
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import me.elvis.commonlib.R
import me.elvis.commonlib.utils.UiUtils

class StarView : View {

    private var mSrc: Drawable?
    private var mOpposite: Drawable?
    private var mGap = 0
    private var mCount = 0
    private var mSelection = 0
    private var mImgWidth = 0
    private var mImgHeight = 0
    private val mRect = Rect()

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.StarView)
        mSrc = ta.getDrawable(R.styleable.StarView_src)
        mOpposite = ta.getDrawable(R.styleable.StarView_opposite)
        mGap = ta.getDimensionPixelSize(R.styleable.StarView_gap, UiUtils.dip2px(context, 5f))
        mImgWidth = ta.getDimensionPixelSize(R.styleable.StarView_imgWidth, mSrc?.intrinsicWidth ?: 0)
        mImgHeight = ta.getDimensionPixelSize(R.styleable.StarView_imgHeight, mSrc?.intrinsicHeight ?: 0)
        mCount = ta.getInt(R.styleable.StarView_count, 0)
        mSelection = ta.getInt(R.styleable.StarView_selection, 0)
        if (mSelection > mCount) mSelection = mCount
        ta.recycle()
    }

    fun setCount(count: Int) {
        mCount = count
        invalidate()
    }

    fun getCount() = mCount

    fun setSelection(selection: Int) {
        mSelection = selection
        if (mSelection > mCount) mSelection = mCount
        invalidate()
    }

    fun getSelection() = mSelection

    fun setSrcResource(@DrawableRes resId: Int) {
        setSrcDrawable(ContextCompat.getDrawable(context, resId))
    }

    fun setSrcDrawable(src: Drawable?) {
        mSrc = src
        invalidate()
    }

    fun setOppositeDrawable(@DrawableRes resId: Int) {
        setOppositeDrawable(ContextCompat.getDrawable(context, resId))
    }

    fun setOppositeDrawable(src: Drawable?) {
        mOpposite = src
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (mSrc == null || mOpposite == null) return
        val wMode = MeasureSpec.getMode(widthMeasureSpec)
        var width = MeasureSpec.getSize(widthMeasureSpec)
        val hMode = MeasureSpec.getMode(heightMeasureSpec)
        var height = MeasureSpec.getSize(heightMeasureSpec)
        val gap = (mCount - 1) * mGap
        if (wMode == MeasureSpec.AT_MOST || wMode == MeasureSpec.UNSPECIFIED) {
            width = mImgWidth * mCount + gap + paddingLeft + paddingRight
        } else {
            val surplus = width - paddingLeft - paddingRight - gap
            if (surplus < mImgWidth * mCount) {
                mImgWidth = surplus / mCount
            }
        }
        if (hMode == MeasureSpec.AT_MOST || hMode == MeasureSpec.UNSPECIFIED) {
            height = mImgHeight + paddingTop + paddingBottom
        } else {
            val surplus = width - paddingTop - paddingBottom
            if (surplus < mImgHeight) {
                mImgHeight = surplus
            }
        }
        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (mSrc == null || mOpposite == null) return
        val src = mSrc!!
        val opposite = mOpposite!!
        val oppositeCount = mCount - mSelection
        var left = (width - mImgWidth * mCount - mGap * (mCount - 1)) / 2
        val top = (height - mImgHeight) / 2
        for (i in 1..mCount) {
            val d = if (i <= mSelection) src else opposite
            d.setBounds(left, top, left + mImgWidth, top + mImgHeight)
            d.draw(canvas)
            left += mImgWidth + mGap
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isEnabled) return super.onTouchEvent(event)
        when (event.actionMasked) {
            MotionEvent.ACTION_UP, MotionEvent.ACTION_MOVE -> {
                var left = paddingLeft
                val top = (height - mImgHeight) / 2
                for (i in 1..mCount) {
                    mRect.set(left, top, left + mImgWidth, top + mImgHeight)
                    if (mRect.contains(event.x.toInt(), event.y.toInt())) {
                        mSelection = i
                        invalidate()
                        return true
                    }
                    left += mImgWidth + mGap
                }
            }
        }
        return true
    }
}