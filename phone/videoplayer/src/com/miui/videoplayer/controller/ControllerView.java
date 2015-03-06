/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   ControllerView.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-6-20
 */

package com.miui.videoplayer.controller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.miui.videoplayer.common.DisplayInformationFetcher;

/**
 * @author tianli
 *
 */
public class ControllerView extends FrameLayout {
    public final static String TAG = "ControllerView";

    /* Touch Event Handler */
    
    private float mTouchStartY = -1;
    
    private float mX = 0;
    private float mY = 0;
    private boolean mMoved = false;
    private static float Y_TOLERANCE = 0;
    private static float X_TOLERANCE = 0;
    
    
    /* Touch  */
    private boolean mMovedLeft = false;
    private boolean mMovedRight = false;
    private boolean mMovedCenter = false;
    private DisplayMetrics mDisplayMetrics; 
    
    private boolean mDownRightRegion = false;
    private boolean mDownLeftRegion = false;

    /* Widgets */

//  private MediaController mMediaController;

    private DisplayInformationFetcher mDisplayInformationFetcher;
    
    private OnControlEventListener mGestureListener;
    
    /* Airkan */
//  private AirkanManagerNew mAirkanManager;
//  boolean mDirectAirkan;

    public ControllerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public ControllerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ControllerView(Context context) {
        super(context);
        init();
    }
    
    private void init(){
        mDisplayInformationFetcher = DisplayInformationFetcher.getInstance(
                getContext().getApplicationContext());
        mDisplayMetrics = mDisplayInformationFetcher.getDisplayMetrics();
//        int screenWidth = mDisplayInformationFetcher.getScreenWidth();
//        mAdjustPositionStep = screenWidth / 120f;
//        mTriggerAdjustPositionTolerance = mDisplayMetrics.densityDpi / (10 * mDisplayMetrics.density);
        Y_TOLERANCE = 10 * mDisplayMetrics.density;
        X_TOLERANCE = 10 * mDisplayMetrics.density;
        setFocusable(true);
        setFocusableInTouchMode(true);
    }

    public void setGestureListener(OnControlEventListener gestureListener) {
        this.mGestureListener = gestureListener;
    }

//    public float getAdjustPositionStep(){
//        int screenWidth = mDisplayInformationFetcher.getScreenWidth();
//        mAdjustPositionStep = screenWidth / 120f;
//        return mAdjustPositionStep;
//    }
    
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mTouchStartY = event.getY();
            touchStart(event.getX(), event.getY());
        }else{
            int statusBarTolerance = 10;
            if(mTouchStartY >= 0 && mTouchStartY < statusBarTolerance){
                // drag status bar.
                return false;
            }
            if (event.getAction() == MotionEvent.ACTION_MOVE) {
                mTouchStartY = -1;
                touchMove(event.getX(), event.getY());
            }else if (event.getAction() == MotionEvent.ACTION_UP || 
                    event.getAction() == MotionEvent.ACTION_CANCEL) {
                touchUp(event.getX(), event.getY());
                mTouchStartY = -1;
            }
        }
        return true;
    }
    
    private void touchStart(float x, float y) {
        int screenWidth = mDisplayInformationFetcher.getScreenWidth();
        mX = x;
        mY = y;
        mMovedLeft = false;
        mMovedRight = false;
        mMovedCenter = false;
        if (mX <= screenWidth / 2) {
            mDownLeftRegion = true;
        } else if (mX >= screenWidth - screenWidth/ 2) {
            mDownRightRegion = true;
        }
    }

    private void touchMove(float x, float y) {
        float distanceX = x - mX;
        float distanceY = y - mY;
        float dx = Math.abs(distanceX);
        float dy = Math.abs(distanceY);
        if(dy > Y_TOLERANCE && (dy > dx || mMovedLeft || mMovedRight) && !mMovedCenter){
            if (mDownLeftRegion) {
                mMovedLeft = true;
                if(mGestureListener != null){
                    mGestureListener.onTouchMove(OnControlEventListener.REGION_LEFT, distanceX, distanceY);
                }
                mMoved = true;
                mX = x;
                mY = y;
            }
            if (mDownRightRegion) {
                mMovedRight = true;
                if(mGestureListener != null){
                    mGestureListener.onTouchMove(OnControlEventListener.REGION_RIGHT, distanceX, distanceY);
                }
                mMoved = true;
                mX = x;
                mY = y;
            }
        }else if(dx > X_TOLERANCE && (dx >= dy  || mMovedCenter) && !mMovedLeft && !mMovedRight ){
            mMovedCenter = true;
            if(mGestureListener != null){
                mGestureListener.onTouchMove(OnControlEventListener.REGION_CENTER, distanceX, distanceY);
            }
            mMoved = true;
            mX = x;
            mY = y;
        }
    }

    private void touchUp(float x, float y) {
        if (!mMoved) {
            if(mGestureListener != null){
//                if (mDownLeftRegion) {
//                    mGestureListener.onTap(OnControlEventListener.REGION_LEFT);
//                }else if (mDownRightRegion) {
//                    mGestureListener.onTap(OnControlEventListener.REGION_RIGHT);
//                }else{
                    mGestureListener.onTap(OnControlEventListener.REGION_CENTER);
//                }
            }
        } else {
            if(mGestureListener != null){
                if(mMovedLeft){
                    mGestureListener.onTouchUp(OnControlEventListener.REGION_LEFT);
                }else if(mMovedRight){
                    mGestureListener.onTouchUp(OnControlEventListener.REGION_RIGHT);
                }else if(mMovedCenter){
                    mGestureListener.onTouchUp(OnControlEventListener.REGION_CENTER);
                }
            }
        }
        mMoved = false;
        mDownLeftRegion = false;
        mDownRightRegion = false;
    }
    
//  @Override
//  public boolean onKeyDown(int keyCode, KeyEvent event) {
//      boolean handled = false;
//      if(mGestureListener != null){
//          handled = mGestureListener.onKeyDown(event);
//          if(handled){
//              return true;
//          }
//      }
//      return super.onKeyDown(keyCode, event);
//  }

    public interface OnControlEventListener{
        
        public static int REGION_LEFT = 0;
        public static int REGION_RIGHT = 1;
        public static int REGION_CENTER = 2;
        public void onTouchMove(int region, float movementX, float movementY);
        public void onTouchUp(int region);
        public void onTap(int region);
    }
}
