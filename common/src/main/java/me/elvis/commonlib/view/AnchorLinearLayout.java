package me.elvis.commonlib.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import me.elvis.commonlib.R;


public class AnchorLinearLayout extends LinearLayout {
    private static final String TAG = "AnchorLinearLayout";

    private View mAnchor;

    public AnchorLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AnchorLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        int anchor = 0;
        for (int i = 0; i < getChildCount(); i++) {
            final View child = getChildAt(i);
            LayoutParams params = (LayoutParams) child.getLayoutParams();
            if (params.anchor) {
                mAnchor = child;
                anchor++;
            }
        }
        if (anchor > 1) {
            throw new RuntimeException("One anchor view only.");
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mAnchor != null) {
            int sum = 0;
            boolean needRemeasure;
            final int orientation = getOrientation();
            final int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View child = getChildAt(i);
                final LayoutParams lp = (LayoutParams) child.getLayoutParams();
                if (orientation == VERTICAL) {
                    measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
                    sum += child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
                } else {
                    measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
                    sum += child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
                }
            }
            if (orientation == VERTICAL) {
                needRemeasure = sum > getMeasuredHeight();
            } else {
                needRemeasure = sum > getMeasuredWidth();
            }
            if (needRemeasure) {
                int used = 0;
                int remaining;
                for (int i = 0; i < childCount; i++) {
                    final View child = getChildAt(i);
                    LayoutParams params = (LayoutParams) child.getLayoutParams();
                    if (child != mAnchor) {
                        if (orientation == VERTICAL) {
                            used += child.getMeasuredHeight() + params.topMargin + params.bottomMargin;
                        } else {
                            used += child.getMeasuredWidth() + params.leftMargin + params.rightMargin;
                        }
                    }
                }
                if (orientation == VERTICAL) {
                    remaining = getMeasuredHeight() - used;
                } else {
                    remaining = getMeasuredWidth() - used;
                }
                if (remaining > 0) {
                    if (orientation == VERTICAL) {
                        measureChildWithMargins(mAnchor, widthMeasureSpec,
                                0,
                                heightMeasureSpec, getMeasuredHeight() - remaining);
                    } else {
                        measureChildWithMargins(mAnchor, widthMeasureSpec,
                                getMeasuredWidth() - remaining,
                                heightMeasureSpec, 0);
                    }
                } else {
                    // No room left. Follow default.
                    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                }
            }
        }
    }

    @Override
    protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams lp) {
        return new LayoutParams(lp);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        if (getOrientation() == HORIZONTAL) {
            return new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        } else {
            return new LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        }
    }

    public static class LayoutParams extends LinearLayout.LayoutParams {

        private boolean anchor;

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.LayoutParams p) {
            super(p);
        }

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.AnchorLinearLayout_Layout);
            anchor = a.getBoolean(R.styleable.AnchorLinearLayout_Layout_layout_cAnchor, false);
            a.recycle();
        }
    }
}
