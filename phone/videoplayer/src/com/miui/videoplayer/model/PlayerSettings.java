/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   PlayerSettings.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-12-3
 */
package com.miui.videoplayer.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.miui.video.model.AppSingleton;

/**
 * @author tianli
 *
 */
public class PlayerSettings extends AppSingleton {

    public static final String PREF_KEY_AUDIO_EFFECT = "key_audio_effect";
    public static final String PREF_KEY_FORCE_FULL_SCREEN = "key_force_full_screen";
    
    public static final int AUDIO_EFFECT_ON = 0;
    public static final int AUDIO_EFFECT_OFF = 1;
    
    public static final int FORCE_FULL_SCREEN_ON = 0;
    public static final int FORCE_FULL_SCREEN_OFF = 1;
    
    public boolean isAudioEffectOn(){
        return getIntValue(PREF_KEY_AUDIO_EFFECT, AUDIO_EFFECT_ON) == AUDIO_EFFECT_ON;
    }
    
    public void setAudioEffect(boolean on){
        saveIntValue(PREF_KEY_AUDIO_EFFECT, on ? AUDIO_EFFECT_ON : AUDIO_EFFECT_OFF );
    }
    
    public boolean isForceFullScreen(){
        return getIntValue(PREF_KEY_FORCE_FULL_SCREEN, FORCE_FULL_SCREEN_OFF)
                == FORCE_FULL_SCREEN_ON;
    }
    
    public void setForeceFullScreen(boolean on){
        saveIntValue(PREF_KEY_FORCE_FULL_SCREEN, on ? FORCE_FULL_SCREEN_ON : FORCE_FULL_SCREEN_OFF );
    }
    
    SharedPreferences getSharedPreference(){
        return mContext.getSharedPreferences("video_player", Context.MODE_PRIVATE);
    }

    private void saveIntValue(String key, int value){
        try{
            SharedPreferences prefs = getSharedPreference();
            Editor editor = prefs.edit();
            editor.putInt(key, value);
            editor.apply();
        }catch(Exception e){
        }
    }
    
    private int getIntValue(String key, int defaultValue){
        try{
            SharedPreferences prefs = getSharedPreference();
            return prefs.getInt(key, defaultValue);
        }catch(Exception e){
            return defaultValue;
        }
    }
    
}
