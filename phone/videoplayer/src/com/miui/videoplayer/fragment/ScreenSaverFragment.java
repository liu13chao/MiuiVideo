/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   ScreenSaverFragment.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-6-24
 */

package com.miui.videoplayer.fragment;

import android.content.Context;
import android.content.Intent;
import android.widget.FrameLayout;

import com.miui.videoplayer.framework.history.PlayHistoryManager;
import com.miui.videoplayer.framework.history.PlayHistoryManager.PlayHistoryEntry;

/**
 * @author tianli
 *
 */
public class ScreenSaverFragment extends GenericPlayFragment {


    public ScreenSaverFragment(Context context, FrameLayout anchor, Intent intent) {
        super(context, anchor);
    }

    public static final String TAG = "ScreenSaverFragment";
	
	@Override
	public void onSavePlayHistory(PlayHistoryManager playMgr) {
	}

	@Override
	public PlayHistoryEntry onLoadPlayHistory(PlayHistoryManager playMgr) {
		return null;
	}

	@Override
	public boolean isRepeated() {
		return true;
	}
	
}
