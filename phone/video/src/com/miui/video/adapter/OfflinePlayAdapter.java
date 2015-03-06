/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   OfflinePlayAdapter.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-11-22
 */
package com.miui.video.adapter;

import android.content.Context;
import android.view.View.OnClickListener;

import com.miui.video.R;
import com.miui.video.controller.MediaListRowBuilder;
import com.miui.video.controller.OfflinePlayRowBuilder;
import com.miui.video.offline.OfflineMedia;

/**
 * @author tianli
 *
 */
public class OfflinePlayAdapter extends SimpleListRowInfoAdapter<OfflineMedia> {

    public OfflinePlayRowBuilder mRowBuilder;
    
    public OfflinePlayAdapter(Context context) {
        super(context, R.layout.offline_play_view);
        mRowBuilder = new OfflinePlayRowBuilder(mContext);
    }
    
    public void setOnMoreClickListener(OnClickListener listener){
        mRowBuilder.setOnMoreClickListener(listener);
    }

    @Override
    protected MediaListRowBuilder<OfflineMedia> getRowBuilder() {
        return mRowBuilder;
    }
}
