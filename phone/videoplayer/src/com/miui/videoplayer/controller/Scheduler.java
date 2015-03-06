/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   Scheduler.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-11-29
 */
package com.miui.videoplayer.controller;

import android.os.Handler;
import android.os.Looper;

/**
 * @author tianli
 *
 */
public class Scheduler{
    
    static Handler  getUIHandler(){
        return new Handler(Looper.getMainLooper());
    }
}
