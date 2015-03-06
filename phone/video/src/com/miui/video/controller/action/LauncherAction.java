/**
 *  Copyright(c) 2014 XiaoMi TV Group
 *    
 *  LauncherAction.java
 *
 *  @author tianli(tianli@xiaomi.com)
 *
 *  2014-11-12
 */
package com.miui.video.controller.action;

import android.app.Activity;
import android.content.Intent;

/**
 * @author tianli
 *
 */
public abstract class LauncherAction {

    protected Activity mActivity;
    
    public LauncherAction(Activity activity){
        mActivity = activity;
    }
    
    public abstract Intent getIntent();
    
    public void action(){
        Intent intent = getIntent();
        if(intent != null){
            mActivity.startActivity(intent);
        }
    }
    
}
