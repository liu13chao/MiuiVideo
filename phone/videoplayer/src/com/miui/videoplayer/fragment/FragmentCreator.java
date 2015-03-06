/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   Factory.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-6-24
 */

package com.miui.videoplayer.fragment;

import android.content.Context;
import android.content.Intent;
import android.widget.FrameLayout;

import com.miui.videoplayer.common.Constants;

/**
 * @author tianli
 *
 */
public abstract class FragmentCreator {
    
    public static CoreFragment createFragment(Context context, FrameLayout anchor,
            Intent intent){
    	boolean isScreenSaver = intent.getBooleanExtra(Constants.INTENT_KEY_BOOLEAN_SCREENSAVER, 
    			false);
    	int mediaId = intent.getIntExtra(Constants.MEDIA_ID, -1);
    	if(mediaId > 0){
    		OnlineVideoFragment online = new OnlineVideoFragment(context, anchor);
    		online.launch(intent);
    		return online;
    	}else{
    		if(isScreenSaver){
        		GenericPlayFragment generic = new ScreenSaverFragment(context, anchor, 
        		        intent);
        		generic.launch(intent);
        		return generic;
    		}else{
        		GenericPlayFragment generic = new GenericPlayFragment(context, anchor);
                generic.launch(intent);
        		return generic;
    		}
    	}
    }
    
    public abstract CoreFragment create(Context context, FrameLayout anchor, Intent intent);
}
