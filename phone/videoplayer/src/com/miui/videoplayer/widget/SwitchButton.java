package com.miui.videoplayer.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.CheckBox;

import com.miui.video.R;

public class SwitchButton extends CheckBox {
	private static final int FULL_ALPHA = 255;

    private static final int ANIMATION_DURATION = 180;

    private static final int SCALE_ANIMATION_DELAY = 100;

    private Drawable mFrameOn, mFrameOff;

    private Drawable mSliderOn;

    private int mSliderOnAlpha;

    private Drawable mSliderOff;

    private Bitmap mBarOff;

    private Bitmap mBarOn;

    private Bitmap mMask;

    private int mWidth;

    private int mHeight;

    private int mSliderWidth;
    
    private int mSliderHeight;

    private int mSliderPositionStart;

    private int mSliderPositionEnd;

    private int mSliderOffset;

    private int mLastX;

    private int mOriginalTouchPointX;

    private boolean mTracking;

    private boolean mSliderMoved;

    private int mTapThreshold;

    private OnCheckedChangeListener mOnPerformCheckedChangeListener;

    private Rect mTmpRect = new Rect();

    private Animator mAnimator;

    private Animator.AnimatorListener mAnimatorListener = new AnimatorListenerAdapter() {
        private boolean mCanceled;

        @Override
        public void onAnimationStart(Animator animation) {
            mCanceled = false;
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            mCanceled = true;
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            if (mCanceled) {
                return;
            }
            mAnimator = null;
            final boolean isChecked = mSliderOffset >= mSliderPositionEnd;
            if (isChecked != isChecked()) {
                setChecked(isChecked);
                if (mOnPerformCheckedChangeListener != null) {
                    post(new Runnable() {
                        @Override
                        public void run() {
                            if (mOnPerformCheckedChangeListener != null) {
                                mOnPerformCheckedChangeListener.onCheckedChanged(SwitchButton.this, isChecked);
                            }
                        }
                    });
                }
            }
        }
    };

    public SwitchButton(Context context) {
        this(context, null);
    }

    public SwitchButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwitchButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        setDrawingCacheEnabled(false);
        mTapThreshold = ViewConfiguration.get(context).getScaledTouchSlop() / 2;

        Resources res = context.getResources();
        mFrameOn = res.getDrawable(R.drawable.switch_open_bg);
        mFrameOff = res.getDrawable(R.drawable.switch_off_bg);
        mSliderOn = res.getDrawable(R.drawable.switch_open_round);
        mSliderOff = res.getDrawable(R.drawable.switch_off_round);
        
        mWidth = mFrameOn.getIntrinsicWidth();
        mHeight = mFrameOn.getIntrinsicHeight();

        mSliderWidth = Math.min(mWidth, mSliderOn.getIntrinsicWidth());
        mSliderHeight = Math.min(mHeight, mSliderOn.getIntrinsicHeight());
        int slideMargin = (mHeight - mSliderHeight) / 2;
        mSliderPositionStart = slideMargin;
        mSliderPositionEnd = mWidth - mSliderWidth - slideMargin;
        mSliderOffset = mSliderPositionStart;
        mFrameOn.setBounds(0, 0, mWidth, mHeight);
        mFrameOff.setBounds(0, 0, mWidth, mHeight);
        Drawable maskDrawable = res.getDrawable(R.drawable.switch_mask_bg);
        maskDrawable.setBounds(0, 0, mWidth, mHeight);
        mMask = convertToAlphaMask(maskDrawable);
    }

    /*
     *@hide
     */
    public void setOnPerformCheckedChangeListener(OnCheckedChangeListener listener) {
        mOnPerformCheckedChangeListener = listener;
    }

    private Bitmap convertToAlphaMask(Drawable drawable) {
        final Rect rect = drawable.getBounds();
        Bitmap mask = Bitmap.createBitmap(rect.width(), rect.height(), Bitmap.Config.ALPHA_8);
        Canvas canvas = new Canvas(mask);
        drawable.draw(canvas);
        return mask;
    }

    @Override
    public void setChecked(boolean checked) {
        boolean oldState = isChecked();

        if (oldState != checked) {
            super.setChecked(checked);
            mSliderOffset = checked ? mSliderPositionEnd : mSliderPositionStart;
            mSliderOnAlpha = checked ? FULL_ALPHA : 0;
            invalidate();
        }
    }

    @Override
    public void setButtonDrawable(Drawable d) {
        // do nothing
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        mSliderOn.setState(getDrawableState());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }

        int action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();
        final Rect sliderFrame = mTmpRect;
        sliderFrame.set(mSliderOffset, 0, mSliderOffset + mSliderWidth, mHeight);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (sliderFrame.contains(x, y)) {
                    mTracking = true;
                    setPressed(true);
                } else {
                    mTracking = false;
                }
                mLastX = x;
                mOriginalTouchPointX = x;
                mSliderMoved = false;
                break;

            case MotionEvent.ACTION_MOVE:
                if (mTracking) {
                    moveSlider(x - mLastX);
                    mLastX = x;
                    if (Math.abs(x - mOriginalTouchPointX) >= mTapThreshold) {
                        mSliderMoved = true;
                        getParent().requestDisallowInterceptTouchEvent(true);
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
                if (mTracking) {
                    if (!mSliderMoved) {
                        animateToggle();
                    } else {
                        animateToState(mSliderOffset >= mSliderPositionEnd / 2);
                    }
                } else {
                    animateToggle();
                }
                mTracking = false;
                mSliderMoved = false;
                setPressed(false);
                break;

            case MotionEvent.ACTION_CANCEL:
                mTracking = false;
                mSliderMoved = false;
                setPressed(false);
                animateToState(mSliderOffset >= mSliderPositionEnd / 2);
                break;
        }

        return true;
    }

    @Override
    public void setPressed(boolean pressed) {
        super.setPressed(pressed);
        invalidate();
    }

    private void animateToggle() {
        animateToState(!isChecked());
    }

    private void animateToState(boolean isChecked) {
        if (mAnimator != null) {
            mAnimator.cancel();
            mAnimator = null;
        }
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator slidingAnimator = ObjectAnimator
                .ofInt(this, "SliderOffset", isChecked ? mSliderPositionEnd : mSliderPositionStart);
        ObjectAnimator scaleAnimator = ObjectAnimator.ofInt(this, "SliderOnAlpha", isChecked ? FULL_ALPHA : 0);
        scaleAnimator.setDuration(ANIMATION_DURATION);
        slidingAnimator.setDuration(ANIMATION_DURATION);
        animatorSet.play(scaleAnimator).after(slidingAnimator).after(SCALE_ANIMATION_DELAY);
        mAnimator = animatorSet;
        mAnimator.addListener(mAnimatorListener);
        mAnimator.start();
    }

    private void moveSlider(int offsetX) {
        // check the edge condition
        mSliderOffset += offsetX;
        if (mSliderOffset < mSliderPositionStart) {
            mSliderOffset = mSliderPositionStart;
        } else if (mSliderOffset > mSliderPositionEnd) {
            mSliderOffset = mSliderPositionEnd;
        }
        setSliderOffset(mSliderOffset);
    }

    /**
     * @hide
     */
    public int getSliderOffset() {
        return mSliderOffset;
    }

    /**
     * @hide
     */
    public void setSliderOffset(int sliderOffset) {
        mSliderOffset = sliderOffset;
        invalidate();
    }

    /**
     * @hide
     */
    public int getSliderOnAlpha() {
        return mSliderOnAlpha;
    }

    /**
     * @hide
     */
    public void setSliderOnAlpha(int sliderOnAlpha) {
        mSliderOnAlpha = sliderOnAlpha;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int alpha = isEnabled() ? FULL_ALPHA : FULL_ALPHA / 2;
        canvas.saveLayerAlpha(0, 0, mMask.getWidth(), mMask.getHeight(), alpha, Canvas.ALL_SAVE_FLAG);
        // draw mask
        canvas.drawBitmap(mMask, 0, 0, null);
        // draw the frame
        if (mSliderOnAlpha <= FULL_ALPHA) {
        	mFrameOff.draw(canvas);
        }
        mFrameOn.setAlpha(mSliderOnAlpha);
        mFrameOn.draw(canvas);
        // draw the slider
        if (mSliderOnAlpha <= FULL_ALPHA) {
            mSliderOff.setBounds(mSliderOffset, (mHeight - mSliderHeight) / 2, 
            		mSliderWidth + mSliderOffset, (mHeight + mSliderHeight) / 2);
            mSliderOff.draw(canvas);
        }

        mSliderOn.setAlpha(mSliderOnAlpha);
        mSliderOn.setBounds(mSliderOffset, (mHeight - mSliderHeight) / 2,
        		mSliderWidth + mSliderOffset, (mHeight + mSliderHeight) / 2);
        mSliderOn.draw(canvas);
        
        canvas.restore();
    }

}
