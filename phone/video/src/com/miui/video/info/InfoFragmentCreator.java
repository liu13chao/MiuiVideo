/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   InfoFragmentCreator.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-12-2
 */
package com.miui.video.info;

import android.content.Context;
import android.content.Intent;
import android.widget.FrameLayout;

import com.miui.videoplayer.fragment.CoreFragment;
import com.miui.videoplayer.fragment.FragmentCreator;
import com.miui.videoplayer.model.BaseUri;

/**
 * @author tianli
 *
 */
public class InfoFragmentCreator extends FragmentCreator {

    private InfoChannelDataManager mInfoDataManager;
    private BaseUri mInitUri;
    
    public InfoFragmentCreator(InfoChannelDataManager mgr, BaseUri uri){
        mInitUri = uri;
        mInfoDataManager = mgr;
    }
    
    @Override
    public CoreFragment create(Context context, FrameLayout anchor,
            Intent intent) {
        InfoPlayFragment infoFragment = new InfoPlayFragment(context, anchor, mInitUri,
                mInfoDataManager);
        return infoFragment;
    }

}
