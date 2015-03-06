/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   ChannelEntryHandler.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-11-17
 */
package com.miui.video.controller;

import android.app.Activity;
import android.view.View;

import com.miui.video.controller.action.MoreBtnAction;
import com.miui.video.type.ChannelRecommendation;

/**
 * @author tianli
 *
 */
public class ChannelEntryHandler {
    
    Activity mActivity;
   
    public ChannelEntryHandler(Activity activity){
        mActivity = activity;
    }
    
    public void onEnterChannel(View view, ChannelRecommendation recommendation){
        if(recommendation != null){
            new MoreBtnAction(mActivity, recommendation).action();
        }
    }
    
}
