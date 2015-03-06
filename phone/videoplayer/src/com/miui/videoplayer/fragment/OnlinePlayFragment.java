/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   OnlinePlayFragment.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-12-2
 */
package com.miui.videoplayer.fragment;

import android.content.Context;
import android.util.Log;
import android.widget.FrameLayout;

import com.miui.videoplayer.common.AndroidUtils;
import com.miui.videoplayer.framework.history.PlayHistoryManager;
import com.miui.videoplayer.framework.history.PlayHistoryManager.PlayHistoryEntry;
import com.miui.videoplayer.model.BaseUri;
import com.miui.videoplayer.model.OnlineUri;
import com.miui.videoplayer.model.PlayHistoryVistor;

/**
 * @author tianli
 *
 */
public abstract class OnlinePlayFragment extends CoreFragment {

    public OnlinePlayFragment(Context context, FrameLayout anchor) {
        super(context, anchor);
    }

    @Override
    protected void onPlay(BaseUri uri) {
        mUri = uri;
        if(uri instanceof OnlineUri){
            if(AndroidUtils.isUseSdk((OnlineUri)uri)){
                mVideoView.setDataSource(((OnlineUri) uri).getSdkInfo());
            }else{
                mVideoView.setDataSource(uri.getUri().toString());
            }
            mVideoView.start();
        }
    }

    @Override
    public void onSavePlayHistory(PlayHistoryManager playMgr) {
        Log.d(TAG, "onSavePlayHistory ");
        BaseUri uri = getUri();
        if(uri instanceof OnlineUri && mVideoView != null){
            playMgr.savePlayPosition((OnlineUri)uri, mVideoView.getUri(), mVideoView.getCurrentPosition(), 
                    mVideoView.getDuration());
            playMgr.save();
        }
    }

    @Override
    public PlayHistoryEntry onLoadPlayHistory(PlayHistoryManager playMgr) {
        return PlayHistoryVistor.create(mUri).visit(playMgr);
    }

}
