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
import android.view.View;
import android.view.ViewGroup;

import com.miui.video.controller.BaseMediaRowBuilder;
import com.miui.video.controller.MediaViewClickListener;
import com.miui.video.controller.MediaViewRowInfo;
import com.miui.video.controller.content.MediaContentBuilder;
import com.miui.video.type.BaseMediaInfo;

/**
 * @author tianli
 *
 */
public abstract class BaseRowBuilderAdapter<T extends BaseMediaInfo> extends 
    BaseMediaListAdapter<MediaViewRowInfo> {

    private BaseMediaRowBuilder mRowBuilder;

    public BaseRowBuilderAdapter(Context context) {
        super(context);
    }
    
    protected abstract BaseMediaRowBuilder getRowBuilder();
    
    protected void initRowBuilder(){
        if(mRowBuilder == null){
            mRowBuilder = getRowBuilder();
        }
    }
    
    public void setMediaContentBuilder(MediaContentBuilder contentBuilder){
        initRowBuilder();
        mRowBuilder.setMediaContentBuilder(contentBuilder);
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        initRowBuilder();
        MediaViewRowInfo row = getItem(position);
        return mRowBuilder.getView(mContext, convertView, parent, row, mIsInEditMode);
    }

    @Override
    public void setMediaViewClickListener(MediaViewClickListener listener) {
        super.setMediaViewClickListener(listener);
        initRowBuilder();
        mRowBuilder.setMediaViewClickListener(listener);
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).mViewType;
    }

    @Override
    public int getViewTypeCount() {
        initRowBuilder();
        return mRowBuilder.getViewTypeCount();
    }
    
}
