/**
 *  Copyright(C) 2013 XiaoMi TV Group
 * 
 *  AppSettings.java  
 * 
 *  @author tianli (tianli@xiaomi.com)
 *
 *  @date 2013-12-5
 */
package com.miui.video.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.text.TextUtils;

/**
 * @author tianli
 *
 */
public class AppSettings extends AppSingleton {

	public static final String PEREFERENCE_KEY_OFFLINE_USER_SETTING = "shared_pereference_key_offline_user_setting";
	
	public static final String PEREFERENCE_KEY_OFFLINE_CELL = "use_cellular";
	
	private String mCellularPlayHint = "cellular_play_hint";
	private String mCellularOffDownloadHint = "cellular_off_download_hint";
    public static final String RECEIVE_MIPUSH = "receive_mipush";

    public static final String PEREFERENCE_KEY_ALERT_NETWORK = "alert_network";
    
    public static final String PEREFERENCE_KEY_SEARCH_RECOMMEND = "key_search_recommend";
    public static final String PEREFERENCE_KEY_SEARCH_HISTORY = "key_search_history";

	public boolean isUseCellular() {
    	return PreferenceManager.getDefaultSharedPreferences(mContext).getBoolean(
    			PEREFERENCE_KEY_OFFLINE_CELL, false);
	}	

	public void setUseCellular(boolean isUse) {
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
		Editor editor = prefs.edit();
		editor.putBoolean(PEREFERENCE_KEY_OFFLINE_CELL, isUse);
		editor.apply();
	}
	
	public boolean isUserSetting() {
		return PreferenceManager.getDefaultSharedPreferences(mContext).
				getBoolean(PEREFERENCE_KEY_OFFLINE_USER_SETTING, false);
	}
	
	public void setUserSeting() {
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
		Editor editor = prefs.edit();
		editor.putBoolean(PEREFERENCE_KEY_OFFLINE_USER_SETTING, true);
		editor.apply();
	}
	
	public boolean isMiPushOn() {
	    return PreferenceManager.getDefaultSharedPreferences(mContext).getBoolean(
	            RECEIVE_MIPUSH, true);
	}
	
	public void setMiPushOn(boolean on){
	    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
	    Editor editor = prefs.edit();
	    editor.putBoolean(RECEIVE_MIPUSH, on);
	    editor.apply();
	}
	
	public  boolean isOpenCellularPlayHint(Context context) {
    	return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(mCellularPlayHint, true);
	}
	
	public void setOpenCellularPlayHint(Context context, boolean isUse) {
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = prefs.edit();
		editor.putBoolean(mCellularPlayHint, isUse);
		editor.commit();
	}
	
	public  boolean isOpenCellularOfflineHint(Context context) {
    	return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(mCellularOffDownloadHint, true);
	}
	
	public void setOpenCellularOfflineHint(Context context, boolean isUse) {
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = prefs.edit();
		editor.putBoolean(mCellularOffDownloadHint, isUse);
		editor.commit();
	}
	
    public boolean isAlertNetworkOn(){
        return PreferenceManager.getDefaultSharedPreferences(mContext).getBoolean(
                PEREFERENCE_KEY_ALERT_NETWORK, true);
    }
    public void setAlertNetworkOn(boolean enable) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        Editor editor = prefs.edit();
        editor.putBoolean(PEREFERENCE_KEY_ALERT_NETWORK, enable);
        editor.apply();
    }
    
    public void saveSearchHistory(List<String> list){
        try{
            String value = "";
            if(list != null){
                for(int  i = 0; i < list.size(); i++){
                    value += list.get(i);
                    if(i != list.size() - 1){
                        value += ";;";
                    }
                }
            }
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
            Editor editor = prefs.edit();
            editor.putString(PEREFERENCE_KEY_SEARCH_HISTORY, value);
            editor.apply();
        }catch(Exception e){
        }
    }
    
    public List<String> getSearchHistory(){
        List<String> list = new ArrayList<String>();
        try{
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
            String value = prefs.getString(PEREFERENCE_KEY_SEARCH_HISTORY, null);
            if(!TextUtils.isEmpty(value)){
                String[] array = value.split(";;");
                for(String item : array){
                    list.add(item);
                }
            }
        }catch(Exception e){
        }
        return list;
    }
    
    public void saveSearchRecommend(Set<String> hashSet){
        try{
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
            Editor editor = prefs.edit();
            editor.putStringSet(PEREFERENCE_KEY_SEARCH_RECOMMEND, hashSet);
            editor.apply();
        }catch(Exception e){
        }
    }
    
    public Set<String> getSearchRecommend(){
        try{
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
            return prefs.getStringSet(PEREFERENCE_KEY_SEARCH_RECOMMEND, null);
        }catch(Exception e){
            return null;
        }
    }
    
    public void saveValue(String key, String value){
        try{
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
            Editor editor = prefs.edit();
            editor.putString(key, value);
            editor.apply();
        }catch(Exception e){
        }
    }
    
    public String getValue(String key){
        try{
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
            return prefs.getString(key, null);
        }catch(Exception e){
            return null;
        }
    }
    
    public void saveIntValue(String key, int value){
        try{
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
            Editor editor = prefs.edit();
            editor.putInt(key, value);
            editor.apply();
        }catch(Exception e){
        }
    }
    
    public int getIntValue(String key, int defaultValue){
        try{
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
            return prefs.getInt(key, defaultValue);
        }catch(Exception e){
            return defaultValue;
        }
    }
}
