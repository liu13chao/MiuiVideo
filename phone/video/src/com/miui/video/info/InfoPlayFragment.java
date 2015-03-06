/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   InfoFragment.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-12-2
 */
package com.miui.video.info;

import android.content.Context;
import android.widget.FrameLayout;

import com.miui.videoplayer.fragment.OnlinePlayFragment;
import com.miui.videoplayer.framework.history.PlayHistoryManager;
import com.miui.videoplayer.framework.history.PlayHistoryManager.PlayHistoryEntry;
import com.miui.videoplayer.model.BaseUri;
import com.miui.videoplayer.model.UriLoader;

/**
 * @author tianli
 *
 */
public class InfoPlayFragment extends OnlinePlayFragment {

    private InfoChannelDataManager mInfoDataManager;
    
    public InfoPlayFragment(Context context, FrameLayout anchor, BaseUri uri,
            InfoChannelDataManager mgr) {
        super(context, anchor);
        mUri = uri;
        mInfoDataManager = mgr;
    }
    
    @Override
    public UriLoader getUriLoader() {
        return mInfoDataManager;
    }

    @Override
    public CharSequence getVideoTitle() {
        if(mInfoDataManager != null){
            return mInfoDataManager.getTitle();
        }
        return "";
    }

    @Override
    public CharSequence getVideoSubtitle() {
        return "";
    }

    @Override
    public void onSavePlayHistory(PlayHistoryManager playMgr) {
        super.onSavePlayHistory(playMgr);
    }

    @Override
    public PlayHistoryEntry onLoadPlayHistory(PlayHistoryManager playMgr) {
        return null;
    }
    
    

}
