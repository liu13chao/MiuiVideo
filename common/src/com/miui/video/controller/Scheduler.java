/**
 * 
 */
package com.miui.video.controller;

import android.os.Handler;
import android.os.Looper;

/**
 * @author tianli
 *
 */
public class Scheduler {
    
    public static Handler mUIHandler = new Handler(Looper.getMainLooper());
    
    public static Handler getUIHandler(){
        return mUIHandler;
    }
}
