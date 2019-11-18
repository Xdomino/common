package me.elvis.commonlib.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;

import me.elvis.commonlib.R;


public class SlideValidateView extends View {

    // Validate big img.
    private Bitmap mBg;
    // Validate jigsaw img.
    private Bitmap mPiece;
    // Progress bar button.
    private Drawable mButton;

    private Paint mPaint;
    // Progress bar area.
    private RectF mRectF = new RectF();
    // Progress bar button bounds.
    private Rect mBounds = new Rect();
    private Scroller mScroller;

    // Slide position.
    private float mSlideX;
    // Finger x position of screen.
    private float mFingerX;
    private int mMargin;
    private int mBgWidth, mBgHeight;
    private int mTextColor;
    private int mTextSize;
    private int mProgressBarHeight;
    private int mProgressBarColor;
    private String mText;

    private Data mData;
    private ResultListener mResultListener;
    private boolean isDragging;


    public SlideValidateView(Context context) {
        this(context, null);
    }

    public SlideValidateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        final TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SlideValidateView);
        mButton = ta.getDrawable(R.styleable.SlideValidateView_button);
        final CharSequence text = ta.getText(R.styleable.SlideValidateView_text);
        if (text != null) {
            mText = text.toString();
        }
        mTextSize = ta.getDimensionPixelSize(R.styleable.SlideValidateView_textSize,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 13f, getResources().getDisplayMetrics()));
        mTextColor = ta.getColor(R.styleable.SlideValidateView_textColor, Color.parseColor("#666666"));
        mProgressBarHeight = ta.getDimensionPixelSize(R.styleable.SlideValidateView_progressHeight, 0);
        mProgressBarColor = ta.getColor(R.styleable.SlideValidateView_progressColor, Color.GRAY);
        ta.recycle();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mMargin = dip2px(5f);
        mScroller = new Scroller(context);
    }

    public void setResultListener(ResultListener listener) {
        mResultListener = listener;
    }

    public void setData(Data data) {
        try {
            mData = data;
            byte[] bytes = Base64.decode(mData.bgImage, Base64.DEFAULT);
            mBg = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            byte[] pieceBytes = Base64.decode(mData.sliderImage, Base64.DEFAULT);
            mPiece = BitmapFactory.decodeByteArray(pieceBytes, 0, pieceBytes.length);
            mSlideX = mData.initX;
            requestLayout();
        } catch (Exception ignored) {
        }
    }

    public void initial() {
        mScroller.startScroll((int) mSlideX, 0, (int) (mData.initX - mSlideX), 0, 500);
        invalidate();
    }

    public boolean isDragging() {
        return isDragging;
    }

    public float getSlideX() {
        return mSlideX;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int wm = MeasureSpec.getMode(widthMeasureSpec);
        final int hm = MeasureSpec.getMode(heightMeasureSpec);

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (mBg != null) {
            mBgWidth = mBg.getWidth();
            mBgHeight = mBg.getHeight();
        }
        final int bw = mBgWidth != 0 ? mBgWidth : width;
        final int bh = mBgHeight;

        if ((wm == MeasureSpec.AT_MOST && bw < width)
                || wm == MeasureSpec.UNSPECIFIED) {
            width = bw;
        }

        int nh = bh + mMargin;
        if (mButton != null && mButton.getIntrinsicHeight() > mProgressBarHeight) {
            nh += mButton.getIntrinsicHeight();
        } else {
            nh += mProgressBarHeight;
        }

        if ((hm == MeasureSpec.AT_MOST && nh < height) || wm == MeasureSpec.UNSPECIFIED) {
            height = nh;
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float slideX = getPaddingLeft() + mSlideX;

        int bx = (int) slideX, by = 0;
        int ih = mButton != null ? mButton.getIntrinsicHeight() : 0;
        int iw = mButton != null ? mButton.getIntrinsicWidth() : 0;

        if (mBg != null) {
            canvas.drawBitmap(mBg, 0, 0, null);
            canvas.drawBitmap(mPiece, slideX, mData.initY, null);
            by = mBgHeight;
            bx += (mPiece.getWidth() - iw) / 2;
        }
        by += mMargin;

        int top = by;
        if (mProgressBarHeight != 0) {
            if (mButton != null && ih > mProgressBarHeight) {
                top += ih / 2 - mProgressBarHeight / 2;
            }
            int right = mBgWidth != 0 ? mBgWidth : getWidth();
            mRectF.set(0, top, right, top + mProgressBarHeight);

            mPaint.setColor(mProgressBarColor);
            canvas.drawRoundRect(mRectF, mProgressBarHeight / 2, mProgressBarHeight / 2, mPaint);

            if (mText != null) {
                mPaint.setColor(mTextColor);
                mPaint.setTextSize(mTextSize);
                final float tx = (getWidth() - mPaint.measureText(mText)) / 2;
                final float ty = top + mRectF.height() / 2 - (mPaint.ascent() + mPaint.descent()) / 2;
                canvas.drawText(mText, tx, ty, mPaint);
            }

            if (slideX > iw) {
                right = (int) bx + iw / 2;
                mRectF.set(0, top, right, top + mProgressBarHeight);

                mPaint.setColor(mProgressBarColor);
                canvas.drawRoundRect(mRectF, mProgressBarHeight / 2, mProgressBarHeight / 2, mPaint);
            }
        }


        if (mButton != null) {
            if (ih < mProgressBarHeight) {
                by += mProgressBarHeight / 2 - ih / 2;
            }
            mBounds.set(bx, by, bx + iw, by + ih);
            mButton.setBounds(mBounds);
            mButton.draw(canvas);
        }
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            mSlideX = mScroller.getCurrX();
            invalidate();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final float x = event.getX();
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                if (isInSlider(event)) {
                    isDragging = true;
                    mFingerX = x;
                    return true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (isDragging()) {
                    float delta = x - mFingerX;
                    int max = getWidth() - mButton.getIntrinsicWidth();
                    if (mData != null) {
                        max = mBgWidth - mData.sliderWidth;
                    }
                    mSlideX = Math.max(0, Math.min(max, mSlideX + delta));
                    mFingerX = x;
                    invalidate();
                }
                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (isDragging()) {
                    if (mResultListener != null) mResultListener.onSlideEnd(mSlideX);
                    isDragging = false;
                }
                return true;
        }
        return super.onTouchEvent(event);
    }

    private boolean isInSlider(MotionEvent ev) {
        final float x = ev.getX();
        final float y = ev.getY();

        boolean isIn = false;
        if (mData != null) {
            isIn = !(x < mSlideX || y < mData.initY ||
                    x > mSlideX + mData.sliderWidth ||
                    y > mData.initY + mData.sliderHeight);
        }
        if (!isIn) isIn = mBounds.contains((int) x, (int) y);
        return isIn;
    }

    private int dip2px(float dpValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static class Data {
        private String bgImage;
        private String sliderImage;
        private String gen_key;
        private int initX;
        private int initY;
        private int sliderWidth;
        private int sliderHeight;

        public Data(String bgImage, String sliderImage, int initX, int initY) {
            this.bgImage = bgImage;
            this.sliderImage = sliderImage;
            this.initX = initX;
            this.initY = initY;
        }

        public String getGen_key() {
            return gen_key;
        }

        public void setGen_key(String gen_key) {
            this.gen_key = gen_key;
        }

        public int getSliderWidth() {
            return sliderWidth;
        }

        public void setSliderWidth(int sliderWidth) {
            this.sliderWidth = sliderWidth;
        }

        public int getSliderHeight() {
            return sliderHeight;
        }

        public void setSliderHeight(int sliderHeight) {
            this.sliderHeight = sliderHeight;
        }

        public String getBgImage() {
            return bgImage;
        }

        public void setBgImage(String bgImage) {
            this.bgImage = bgImage;
        }

        public String getSliderImage() {
            return sliderImage;
        }

        public void setSliderImage(String sliderImage) {
            this.sliderImage = sliderImage;
        }

        public int getInitX() {
            return initX;
        }

        public void setInitX(int initX) {
            this.initX = initX;
        }

        public int getInitY() {
            return initY;
        }

        public void setInitY(int initY) {
            this.initY = initY;
        }
    }

    public interface ResultListener {
        void onSlideEnd(float coordX);
    }
}
