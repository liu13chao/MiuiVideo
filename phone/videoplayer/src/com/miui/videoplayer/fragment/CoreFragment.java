/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   CoreFragment.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-6-24
 */

package com.miui.videoplayer.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.widget.FrameLayout;

import com.miui.videoplayer.controller.IVideoLifeCycle;
import com.miui.videoplayer.framework.airkan.AirkanManager;
import com.miui.videoplayer.framework.history.PlayHistoryManager;
import com.miui.videoplayer.framework.history.PlayHistoryManager.PlayHistoryEntry;
import com.miui.videoplayer.media.MediaPlayerControl;
import com.miui.videoplayer.menu.MenuActionListener;
import com.miui.videoplayer.menu.MenuFactory;
import com.miui.videoplayer.menu.MenuIds;
import com.miui.videoplayer.menu.MenuItem;
import com.miui.videoplayer.menu.popup.BaseMenuPopup;
import com.miui.videoplayer.menu.popup.DevicesPopup;
import com.miui.videoplayer.menu.popup.SettingsPopup;
import com.miui.videoplayer.model.BaseUri;
import com.miui.videoplayer.model.UriLoader;
import com.miui.videoplayer.videoview.IVideoView;

/**
 * @author tianli
 *
 */
public abstract class CoreFragment implements IVideoLifeCycle, MenuActionListener{

    public final static String TAG = "CoreFragment";

    protected IVideoView mVideoView = null;
    protected BaseUri mUri = null;

    protected AirkanManager mAirkanManager;
    //	private OnPauseOrStartListener mOnPauseOrStartListener;

    protected MenuActionListener mMenuActionListener;

    protected UriLoader mUriLoader = null;
    protected Context mContext;
    protected FrameLayout mAnchor;
//    protected Intent mLaunchIntent;
    protected VideoProxy mVideoProxy;

    public CoreFragment(Context context, FrameLayout anchor){
        mContext = context;
        mAnchor = anchor;
    }
    
    public void launch(Intent intent){
    }

    final public void attachAirkanManager(AirkanManager airkanManager){
        mAirkanManager = airkanManager;
    }

    final public void attachVideoProxy(VideoProxy proxy){
        mVideoProxy = proxy;
    }

    final public void play(IVideoView player, BaseUri uri){
        mVideoView = player;
        mUri = uri;
        if(getUriLoader() != null){
            getUriLoader().setPlayingUri(mUri);
        }
        onPlay(uri);
    }

    public MilinkFragment newMilinkFragment(){
        MilinkFragment fragment = new MilinkFragment(mContext, mAnchor);
        fragment.attachLocalFragment(this);
        fragment.attachAirkanManager(mAirkanManager);
        return fragment;
    }
    
    protected abstract void onPlay(BaseUri uri);

    public abstract void onSavePlayHistory(PlayHistoryManager playMgr);
    public abstract PlayHistoryEntry onLoadPlayHistory(PlayHistoryManager playMgr);

    public abstract UriLoader getUriLoader();

    public abstract CharSequence getVideoTitle();

    public abstract CharSequence getVideoSubtitle();

    public List<MenuItem> getMenu(){
        List<MenuItem> items = new ArrayList<MenuItem>();
        items.add(MenuFactory.createSetting());
        if(mAirkanManager != null && mAirkanManager.queryAirkanDevices().size() > 0
                && mVideoView != null && mVideoView.isAirkanEnable() && mVideoView.getUri() != null){
            items.add(MenuFactory.createMilink());
        }
        return items;
    }

    @Override
    public void onMenuClick(MenuItem menuItem) {
        if(menuItem.getId() == MenuIds.MENU_ID_COMMON_SETTING){
            SettingsPopup window = new SettingsPopup(mContext, mVideoView);
            window.setShowHideListener(mMenuShowHideListener);
            window.show(mAnchor);
        }else if(menuItem.getId() == MenuIds.MENU_ID_COMMON_MILINK){
            DevicesPopup window = new DevicesPopup(mContext, mAirkanManager);
            window.setShowHideListener(mMenuShowHideListener);
            window.show(mAnchor);
        }
    }

    final public BaseUri getUri(){
        return mUri;
    }
    
    public MediaPlayerControl getPlayer(){
        return mVideoView;
    }

    //	public void setOnPauseOrStartListener(OnPauseOrStartListener listener){
    //		mOnPauseOrStartListener = listener;
    //	}

    protected void onBufferingUpdate(int percent){
    }

    @Override
    public void onCompletion(IVideoView videoView) {
    }

    @Override
    public void onPrepared(IVideoView videoView) {
    }

    @Override
    public void onBufferingStart(IVideoView videoView) {
    }

    @Override
    public void onBufferingEnd(IVideoView videoView) {
    }

    @Override
    public void onVideoLoadingStart(IVideoView videoView) {
    }

    @Override
    public void onEpLoadingStart() {
    }

    @Override
    public void onBufferingPercent(IVideoView videoView, int percent) {
    }

    protected OnShowHideListener<BaseMenuPopup> mMenuShowHideListener = 
            new OnShowHideListener<BaseMenuPopup>() {
        @Override
        public void onShow(BaseMenuPopup popup) {
            if(mVideoProxy != null){
                mVideoProxy.hideController();
            }
            if(popup != null && popup.needPauseVideo()){
                if(mVideoView != null){
                    mVideoView.pause();
                }
            }
        }
        @Override
        public void onHide(BaseMenuPopup popup) {
            if(popup != null && popup.needPauseVideo()){
                if(mVideoView != null){
                    mVideoView.start();
                }
            }
        }
    };

}
