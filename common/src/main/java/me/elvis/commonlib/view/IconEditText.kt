package me.elvis.commonlib.view

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.EditText

class IconEditText : EditText {
    val LEFT = 0
    val TOP = 1
    val RIGHT = 2
    val BOTTOM = 3

    private var mTouchIcon: Int? = null
    private var mIconClickListener: OnIconClickListener? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    fun setOnIconClickListener(listener: OnIconClickListener) {
        mIconClickListener = listener
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                mTouchIcon = checkTouch(event)
                if (mTouchIcon != null) return true
            }
            MotionEvent.ACTION_UP -> {
                if (mTouchIcon != null) {
                    mIconClickListener?.onClick(mTouchIcon!!)
                    return true
                }
            }
        }
        return super.onTouchEvent(event)
    }

    private fun checkTouch(event: MotionEvent): Int? {
        val drawables = compoundDrawables
        val rect = Rect()
        val left = drawables[LEFT]
        if (left != null) {
            val lw = left.intrinsicWidth
            val lh = left.intrinsicHeight
            val l = paddingLeft
            val t = (height - lh) / 2
            val r = l + lw
            val b = t + lh
            rect.set(l, t, r, b)
            if (rect.contains(event.x.toInt(), event.y.toInt()))
                return LEFT
        }
        val top = drawables[TOP]
        if (top != null) {
            val lw = top.intrinsicWidth
            val lh = top.intrinsicHeight
            val l = (width - lw) / 2
            val t = paddingTop
            val r = l + lw
            val b = t + lh
            rect.set(l, t, r, b)
            if (rect.contains(event.x.toInt(), event.y.toInt()))
                return TOP
        }
        val right = drawables[RIGHT]
        if (right != null) {
            val lw = right.intrinsicWidth
            val lh = right.intrinsicHeight
            val l = width - paddingRight - lw
            val t = (height - lh) / 2
            val r = l + lw
            val b = t + lh
            rect.set(l, t, r, b)
            if (rect.contains(event.x.toInt(), event.y.toInt()))
                return RIGHT
        }
        val bottom = drawables[BOTTOM]
        if (bottom != null) {
            val lw = bottom.intrinsicWidth
            val lh = bottom.intrinsicHeight
            val l = (width - lw) / 2
            val t = height - paddingBottom - lh
            val r = l + lw
            val b = t + lh
            rect.set(l, t, r, b)
            if (rect.contains(event.x.toInt(), event.y.toInt()))
                return BOTTOM
        }
        return null
    }

    interface OnIconClickListener {
        fun onClick(coordinate: Int)
    }
}