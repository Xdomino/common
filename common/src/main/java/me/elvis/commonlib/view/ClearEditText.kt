package me.elvis.commonlib.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import me.elvis.commonlib.R

class ClearEditText : IconEditText, IconEditText.OnIconClickListener {

    private var mDrawable: Drawable? = null

    private val mWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable) {
            val drawables = compoundDrawables
            if (s.isNotEmpty()) {
                setCompoundDrawablesWithIntrinsicBounds(
                    drawables[LEFT],
                    drawables[TOP],
                    mDrawable,
                    drawables[BOTTOM]
                )
            } else {
                setCompoundDrawablesWithIntrinsicBounds(
                    drawables[LEFT],
                    drawables[TOP],
                    null,
                    drawables[BOTTOM]
                )
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }
    }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ClearEditText)
        mDrawable = typedArray.getDrawable(R.styleable.ClearEditText_android_drawableRight)
        if (mDrawable == null) {
            mDrawable = ContextCompat.getDrawable(context, R.drawable.ic_delete)
        }
        typedArray.recycle()
        addTextChangedListener(mWatcher)
        setOnIconClickListener(this)
    }


    override fun onClick(coordinate: Int) {
        if (coordinate == RIGHT) {
            setText("")
        }
    }

}