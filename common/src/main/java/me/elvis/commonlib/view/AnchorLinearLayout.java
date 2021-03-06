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
        if (mAnchor != null && mAnchor.getVisibility() != View.GONE) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int widthMode = MeasureSpec.getMode(widthMeasureSpec);
            int height = MeasureSpec.getSize(heightMeasureSpec);
            int heightMode = MeasureSpec.getMode(heightMeasureSpec);

            final boolean horizontal = getOrientation() == HORIZONTAL;
            if ((horizontal && widthMode == MeasureSpec.UNSPECIFIED) ||
                    (!horizontal && heightMode == MeasureSpec.UNSPECIFIED)) {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                return;
            }

            final int paddingVertical = getPaddingTop() + getPaddingBottom();
            final int paddingHorizontal = getPaddingLeft() + getPaddingRight();
            int sum = horizontal ? paddingHorizontal : paddingVertical;

            final int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View child = getChildAt(i);
                if (child.getVisibility() == View.GONE) continue;

                final LayoutParams lp = (LayoutParams) child.getLayoutParams();
                measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
                if (horizontal) {
                    sum += child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
                } else {
                    sum += child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
                }
            }
            int var = horizontal ? width - paddingHorizontal : height - paddingVertical;
            if (sum > var) {
                int used = 0;
                for (int i = 0; i < childCount; i++) {
                    final View child = getChildAt(i);
                    if (child == mAnchor || child.getVisibility() == View.GONE) continue;
                    LayoutParams params = (LayoutParams) child.getLayoutParams();
                    if (horizontal) {
                        used += child.getMeasuredWidth() + params.leftMargin + params.rightMargin;
                    } else {
                        used += child.getMeasuredHeight() + params.topMargin + params.bottomMargin;
                    }
                }

                if (var > used) {
                    LayoutParams lp = (LayoutParams) mAnchor.getLayoutParams();
                    if (horizontal) {
                        lp.width = var - used - lp.leftMargin - lp.rightMargin;
                    } else {
                        lp.height = var - used - lp.topMargin - lp.bottomMargin;
                    }
                }
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
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
