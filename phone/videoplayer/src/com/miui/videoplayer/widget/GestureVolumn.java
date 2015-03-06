/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   GestureVolumn.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-11-29
 */
package com.miui.videoplayer.widget;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.widget.FrameLayout;

import com.miui.video.R;

/**
 * @author tianli
 *
 */
public class GestureVolumn extends GestureView {

    public GestureVolumn(Context context) {
        super(context);
    }
    
    public static GestureVolumn create(FrameLayout anchor){
        GestureVolumn view = new GestureVolumn(anchor.getContext());
        view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 
                LayoutParams.MATCH_PARENT));
        anchor.addView(view);
        return view;
    }
    
    public void adjustVolume(Activity activity, float distanceY) {
        AudioManager audioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
        final int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        final int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int newValue = getNewVolumeValue(distanceY, maxVolume, currentVolume);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newValue, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        if(maxVolume != 0){
            setPercent(newValue * 100 / maxVolume);
        }
        show();
    }
    
    private void setPercent(int percent){
        mText.setText(percent + "%");
    }

    private int getNewVolumeValue(float distanceY, int maxVolume, int currentVolume) {
        int newValue = currentVolume;
        if (distanceY > 0) {
            newValue = currentVolume - 1;
        } else if(distanceY < 0){
            newValue = currentVolume + 1;
        }
        if (newValue > maxVolume) {
            newValue = maxVolume;
        }
        if (newValue < 0) {
            newValue = 0;
        }
        return newValue;
    }

    @Override
    protected int getIcon() {
        return R.drawable.vp_vol_icon_big;
    }

    @Override
    protected int getIconMarginTop() {
        return getResources().getDimensionPixelSize(R.dimen.vp_volumn_icon_margin_top);
    }

    @Override
    protected int getTextMarginTop() {
        return getResources().getDimensionPixelSize(R.dimen.vp_volumn_percent_margin_top);
    }

}
