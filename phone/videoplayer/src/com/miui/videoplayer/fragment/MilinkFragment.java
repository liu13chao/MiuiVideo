/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   MilinkFragment.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-12-1
 */
package com.miui.videoplayer.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.widget.FrameLayout;

import com.miui.videoplayer.framework.history.PlayHistoryManager;
import com.miui.videoplayer.framework.history.PlayHistoryManager.PlayHistoryEntry;
import com.miui.videoplayer.media.MediaPlayerControl;
import com.miui.videoplayer.menu.MenuFactory;
import com.miui.videoplayer.menu.MenuItem;
import com.miui.videoplayer.model.BaseUri;
import com.miui.videoplayer.model.UriLoader;

/**
 * @author tianli
 *
 */
public class MilinkFragment extends CoreFragment {

    CoreFragment mLocalFragment;
    
    public MilinkFragment(Context context, FrameLayout anchor) {
        super(context, anchor);
    }
    
    public void attachLocalFragment(CoreFragment  fragment){
        mLocalFragment = fragment;
    }
    
    public CoreFragment getLocalFragment(){
        return mLocalFragment;
    }
    
    public MediaPlayerControl getLocalPlayer(){
        if(mLocalFragment != null){
            return mLocalFragment.getPlayer();
        }
        return null;
    }
    
    @Override
    protected void onPlay(BaseUri uri) {
    }

    @Override
    public void onSavePlayHistory(PlayHistoryManager playMgr) {
    }

    @Override
    public List<MenuItem> getMenu() {
        List<MenuItem> item = new ArrayList<MenuItem>();
        item.add(MenuFactory.createMilink());
        return item;
    }

    @Override
    public PlayHistoryEntry onLoadPlayHistory(PlayHistoryManager playMgr) {
        return null;
    }

    @Override
    public UriLoader getUriLoader() {
        if(mLocalFragment != null){
            return mLocalFragment.getUriLoader();
        }
        return null;
    }

    @Override
    public CharSequence getVideoTitle() {
        if(mLocalFragment != null){
            return mLocalFragment.getVideoTitle();
        }
        return "";
    }

    @Override
    public CharSequence getVideoSubtitle() {
        return "";
    }

}
