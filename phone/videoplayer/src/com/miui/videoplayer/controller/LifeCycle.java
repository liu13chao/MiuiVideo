/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   LifeCycle.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-11-27
 */
package com.miui.videoplayer.controller;

/**
 * @author tianli
 *
 */
public interface LifeCycle {

    public void onCreate();
    
    public void onStart();

    public void onResume();

    public void onPause();
    
    public void onStop();
    
    public void onDestroy();
}
