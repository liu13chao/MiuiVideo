/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   Settings.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-11-29
 */
package com.miui.videoplayer.controller;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.miui.videoplayer.common.AndroidUtils;
import com.miui.videoplayer.common.DuoKanConstants;

/**
 * @author tianli
 *
 */
public class Settings implements LifeCycle {

    private Activity mActivity;
    
    public Settings(Activity activity){
        mActivity = activity;
    }
    
    public void loadSettings() {
        if(mActivity != null){
            SharedPreferences sp = mActivity.getPreferences(Context.MODE_PRIVATE);
            float activityBrightness = sp.getFloat(DuoKanConstants.SHARED_PEREFERENCE_KEY_LAST_BRIGHTNESS, -1f);
            if (activityBrightness > 0) {
                int newValue = (int) (activityBrightness * 255);
                AndroidUtils.setActivityBrightness(mActivity, newValue);
            }
        }
    }
    
    public void saveSettings() {
        if(mActivity != null){
            SharedPreferences sp = mActivity.getPreferences(Context.MODE_PRIVATE);
            float activityBrightness = AndroidUtils.getActivityBrightness(mActivity);
            if (activityBrightness > 0) {
                Editor editor = sp.edit();
                editor.putFloat(DuoKanConstants.SHARED_PEREFERENCE_KEY_LAST_BRIGHTNESS, activityBrightness);
                editor.apply();
            }
        }
    }
    
    @Override
    public void onCreate() {
        loadSettings();
    }
    
    @Override
    public void onStart() {
    }

    @Override
    public void onResume() {
    }

    @Override
    public void onPause() {
    }

    @Override
    public void onStop() {
    }

    @Override
    public void onDestroy() {
        saveSettings();
    }
}
