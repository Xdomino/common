package me.elvis.commonlib.view

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import me.elvis.commonlib.R
import me.elvis.commonlib.itemDecoration.DividerItemDecoration

class ListRecyclerView : RecyclerView {
    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ListRecyclerView)
        val index = typedArray.getInt(R.styleable.ListRecyclerView_android_orientation, 1)
        layoutManager = LinearLayoutManager(context, index, false)
        val drawable = typedArray.getDrawable(R.styleable.ListRecyclerView_android_divider)
        if (drawable != null) {
            val divider = DividerItemDecoration(context, index)
            val dividerHeight = typedArray.getDimensionPixelSize(
                R.styleable.ListRecyclerView_android_dividerHeight, 0
            )
            divider.setDrawable(drawable, dividerHeight)
            addItemDecoration(divider)
        }
        typedArray.recycle()
    }
}
