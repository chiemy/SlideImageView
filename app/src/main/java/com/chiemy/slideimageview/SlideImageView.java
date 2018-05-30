package com.chiemy.slideimageview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

public class SlideImageView extends android.support.v7.widget.AppCompatImageView {
    private int[] mImages;
    private int mShowIndex = -1;
    private int mLengthOfStep;

    private float mDownX;
    private int mDownIndex;
    private float mTouchSlop;
    private boolean mCallSuper;

    private float mImageChangeFactor = 1;
    private int mAutoChangeInterval;
    private boolean mTouching;
    private boolean mAutoChange;
    private boolean mAutoChangeReverse;

    private ImageChangeListener mImageChangeListener;

    public SlideImageView(Context context) {
        this(context, null);
    }

    public SlideImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    public void setImages(int[] images) {
        mImages = images;
        mLengthOfStep = 0;
        setShowIndex(0);
        if (mAutoChange) {
            startAutoChange(mAutoChangeInterval, mAutoChangeReverse);
        }
    }

    public void setShowIndex(int showIndex) {
        setShowIndex(showIndex, false);
    }

    private void setShowIndex(int showIndex, boolean fromUser) {
        if (showIndex != mShowIndex && mImages != null) {
            mShowIndex = (mImages.length + showIndex) % mImages.length;
            int image = mImages[mShowIndex];
            setImageResource(image);
            if (mImageChangeListener != null) {
                mImageChangeListener.onImageChange(mShowIndex, image, fromUser);
            }
        }
    }

    public int getShowIndex() {
        return mShowIndex;
    }

    /**
     * 滑动时图片切换的系数，越大切换的越快，默认为 1（滑动一个控件的宽度图片正好切换完一轮）<br/>
     * 举例：如果为 2，则图片切换一轮所需要的滑动距离 = 控件宽度 / 2
     */
    public void setImageChangeFactor(float imageChangeFactor) {
        mImageChangeFactor = imageChangeFactor;
    }

    public float getImageChangeFactor() {
        return mImageChangeFactor;
    }

    public void setImageChangeListener(ImageChangeListener imageChangeListener) {
        mImageChangeListener = imageChangeListener;
    }

    /**
     * 开始自动切换图片
     * @param interval 时间间隔(ms)
     */
    public void startAutoChange(int interval) {
        startAutoChange(interval, false);
    }

    /**
     * 开始自动切换图片
     * @param interval 时间间隔(ms)
     */
    public void startAutoChange(int interval, boolean reverse) {
        if (interval > 0) {
            stopAutoChange();
            mAutoChangeReverse = reverse;
            mAutoChangeInterval = interval;
            mAutoChange = true;
            if (mImages != null) {
                post(mChangeRunnable);
            }
        }
    }

    public void setAutoChangeReverse(boolean reverse) {
        mAutoChangeReverse = reverse;
    }

    public void stopAutoChange() {
        mAutoChange = false;
        removeCallbacks(mChangeRunnable);
    }

    public boolean isAutoChange() {
        return mAutoChange;
    }

    public boolean isAutoChangeReverse() {
        return mAutoChangeReverse;
    }

    private Runnable mChangeRunnable = new Runnable() {
        @Override
        public void run() {
            if (!mTouching) {
                int step = mAutoChangeReverse ? - 1 : 1;
                setShowIndex(mShowIndex + step, false);
            }
            postDelayed(this, mAutoChangeInterval);
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        float x = event.getX();
        if (action == MotionEvent.ACTION_DOWN) {
            mTouching = true;
            mCallSuper = true;
            mDownX = x;
            mDownIndex = mShowIndex;
            if (mLengthOfStep == 0) {
                if (mImages != null) {
                    mLengthOfStep = getWidth() / mImages.length;
                }
            }
        } else if (action == MotionEvent.ACTION_MOVE) {
            float distance = x - mDownX;
            int deltaSteps = (int)(distance / mLengthOfStep * mImageChangeFactor);
            setShowIndex(mDownIndex + deltaSteps, true);
            if (Math.abs(distance) > mTouchSlop) {
                mCallSuper = false;
            }
        } else {
            mTouching = false;
        }
        if (mCallSuper) {
            super.onTouchEvent(event);
        }
        return true;
    }

    public interface ImageChangeListener {
        void onImageChange(int index, int drawable, boolean fromUser);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopAutoChange();
    }
}