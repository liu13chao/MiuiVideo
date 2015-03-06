/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   BaseRowInfoListAdapter.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-11-14
 */
package com.miui.video.adapter;

import android.content.Context;

import com.miui.video.controller.BaseMediaRowBuilder;
import com.miui.video.controller.GridMediaRowBuilder;
import com.miui.video.type.BaseMediaInfo;

/**
 * @author tianli
 *
 */
public class GridRowInfoAdapter<T extends BaseMediaInfo> extends 
    MediaRowInfoAdapter<T> {

    private GridMediaRowBuilder<T> mRowBuilder;
    
    public GridRowInfoAdapter(Context context, int column, int layout) {
        super(context, column, layout);
        mRowBuilder = new GridMediaRowBuilder<T>(mContext, column, layout);
    }

    @Override
    protected BaseMediaRowBuilder getRowBuilder() {
        return mRowBuilder;
    }
    
}
