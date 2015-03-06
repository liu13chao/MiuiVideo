/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   OrientationUpdater.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-11-27
 */
package com.miui.videoplayer.controller;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.provider.Settings;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.Surface;

/**
 * @author tianli
 *
 */
public class OrientationUpdater implements LifeCycle{
    
    public static final String TAG = "OrientationUpdater";
    
    private Activity mActivity;

    private int mActivityOrientation;

    private int mRequestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR;

    private OrientationEventListener mOrientationEventListener;
    private OnOrientationListener mOnOrientationListener;
    
    public OrientationUpdater(Activity activity){
        mActivity = activity;
        mActivityOrientation = mActivity.getResources().getConfiguration().orientation;
        initListener();
    }
    
    private void initListener(){
        mOrientationEventListener = new OrientationEventListener(mActivity){
            @Override
            public void onOrientationChanged(int rotation) {
                Log.d(TAG, "rotation = " + rotation);
                if(rotation != -1){
                    handleOrientationChange(rotation);
                }
            }
        };
    }
    
    private void handleOrientationChange(int rotation){
        int systemRotationSetting = 0;
        try {
            systemRotationSetting = Settings.System.getInt(mActivity.getContentResolver(), 
                    Settings.System.ACCELEROMETER_ROTATION);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(systemRotationSetting == 0){
            // rotation is turned off by user.
            return;
        }
        updateOrientation();
        if(isSensorOrientation()){
            return;  //ignore
        }else{
            int requestOrientation = mActivity.getRequestedOrientation();
            if(requestOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT ||
                    requestOrientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT){
                if(rotation > 330 && rotation <= 360 || rotation >= 0 && rotation <= 60 && 
                        rotation >= 150 && rotation <= 240){
                    mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                }
            }
        }
    }
    
    private boolean isSensorOrientation(){
        return mActivity.getRequestedOrientation() == 
                ActivityInfo.SCREEN_ORIENTATION_SENSOR;
    }
    
    public void onConfigurationChanged(Configuration newConfig) {
        updateOrientation();
    }

    private void updateOrientation(){
        int orientation = mActivity.getResources().getConfiguration().orientation;
        if(mActivityOrientation != orientation){
            mActivityOrientation = orientation;
            onOrientationChanged();
            if(mOnOrientationListener != null){
                mOnOrientationListener.onOrientationChanged(mActivityOrientation);
            }
        }
    }
    
    private int getScreenOrientation(){
        int rotation = Surface.ROTATION_0;
        if(mActivity != null){
            rotation = mActivity.getWindowManager().getDefaultDisplay().getRotation();
        }
        if(mActivityOrientation == Configuration.ORIENTATION_PORTRAIT){
            if(rotation == Surface.ROTATION_0){
                return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
            }else{
                return ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
            }
        }else{
            if(rotation == Surface.ROTATION_90){
                return ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
            }else{
                return ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
            }
        }
    }
    
    private void onOrientationChanged(){
    }
    
    public void requestLandscape(){
        mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
    }
    
    public void requestPortrait(){
        mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
    
    public void disableRotation(){
        mRequestedOrientation = mActivity.getRequestedOrientation();
        mActivity.setRequestedOrientation(getScreenOrientation());
    }

    public void enableRotation(){
        mActivity.setRequestedOrientation(mRequestedOrientation);
    }
    
    @Override
    public void onCreate() {
    }
    
    @Override
    public void onStart() {
        mOrientationEventListener.enable();
    }

    @Override
    public void onResume() {
    }

    @Override
    public void onPause() {
    }

    @Override
    public void onStop() {
        mOrientationEventListener.disable();
    }

    @Override
    public void onDestroy() {
    }
    
    public void setOnOrientationListener(
            OnOrientationListener onOrientationListener) {
        this.mOnOrientationListener = onOrientationListener;
    }

    public static interface OnOrientationListener{
        public void onOrientationChanged(int orientation);
    }
    
}
