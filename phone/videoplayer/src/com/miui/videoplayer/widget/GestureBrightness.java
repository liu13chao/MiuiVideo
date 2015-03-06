/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   GestureBrightness.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-11-29
 */
package com.miui.videoplayer.widget;

import android.app.Activity;
import android.content.Context;
import android.widget.FrameLayout;

import com.miui.video.R;
import com.miui.videoplayer.common.AndroidUtils;
import com.miui.videoplayer.common.DuoKanConstants;

/**
 * @author tianli
 *
 */
public class GestureBrightness extends GestureView {

    public GestureBrightness(Context context) {
        super(context);
    }
    
    public static GestureBrightness create(FrameLayout anchor){
        GestureBrightness view = new GestureBrightness(anchor.getContext());
        view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 
                LayoutParams.MATCH_PARENT));
        anchor.addView(view);
        return view;
    }
    
    public void adjustBrightness(Activity activity, float distanceY) {
        int currentValue = (int) (AndroidUtils.getActivityBrightness(activity) * 255);
        if (currentValue < 0) {
            currentValue = AndroidUtils.getSystemBrightness(activity);
        }
        int newValue = getNewBrightnessValue(distanceY, currentValue);
        AndroidUtils.setActivityBrightness(activity, newValue);
        setPercent(newValue * 100 /255);
        show();
    }
    
    private void setPercent(int percent){
        mText.setText(percent + "%");
    }
    
    private int getNewBrightnessValue(float distanceY, int currentValue) {
        int newValue = 0;
        if (distanceY > 0) {
            newValue = currentValue - DuoKanConstants.BRIGHTNESS_STEP;
        } else if(distanceY < 0){
            newValue = currentValue + DuoKanConstants.BRIGHTNESS_STEP;
        }else{
            newValue = currentValue;
        }
        if (newValue > 255) {
            newValue = 255;
        }
        if (newValue < 2) {
            newValue = 2;
        }
        return newValue;
    }

    @Override
    protected int getIcon() {
        return R.drawable.vp_light_icon_big;
    }

    @Override
    protected int getIconMarginTop() {
        return getContext().getResources().getDimensionPixelSize(R.dimen.
                vp_brightness_icon_margin_top);
    }

    @Override
    protected int getTextMarginTop() {
        return getContext().getResources().getDimensionPixelSize(R.dimen.
                vp_brightness_percent_margin_top);
    }
}
