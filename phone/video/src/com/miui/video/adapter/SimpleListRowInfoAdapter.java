/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   OfflineLoadingListAdapter.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-11-20
 */
package com.miui.video.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.miui.video.controller.MediaListRowBuilder;
import com.miui.video.controller.SimpleListRowBuilder;
import com.miui.video.type.BaseMediaInfo;

/**
 * @author tianli
 *
 */
public class SimpleListRowInfoAdapter<T extends BaseMediaInfo> extends MediaRowInfoAdapter<T> {

    SimpleListRowBuilder<T> mRowBuilder;
    
    public SimpleListRowInfoAdapter(Context context, int layout) {
        super(context, 1, layout);
        mRowBuilder = new SimpleListRowBuilder<T>(mContext, layout);
    }

    @Override
    protected MediaListRowBuilder<T> getRowBuilder() {
        return mRowBuilder;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }
    
    
}
