/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   MediaRowInfoAdapter.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-11-22
 */
package com.miui.video.adapter;

import java.util.List;

import android.content.Context;

import com.miui.video.controller.BaseMediaRowBuilder;
import com.miui.video.controller.MediaListRowBuilder;
import com.miui.video.controller.MediaViewRowInfo;
import com.miui.video.type.BaseMediaInfo;

/**
 * @author tianli
 *
 */
public class MediaRowInfoAdapter<T extends BaseMediaInfo> extends BaseRowBuilderAdapter<T> {
    
    private MediaListRowBuilder<T> mRowBuilder;
    
    public MediaRowInfoAdapter(Context context, int column, int layout, int space) {
        super(context);
        mRowBuilder = new MediaListRowBuilder<T>(context, column, layout, space);
    }
    
    public MediaRowInfoAdapter(Context context, int column, int layout) {
        super(context);
        mRowBuilder = new MediaListRowBuilder<T>(context, column, layout);
    }
    
    @Override
    protected BaseMediaRowBuilder getRowBuilder() {
        return mRowBuilder;
    }

    @SuppressWarnings({ "unchecked" })
    private void builderChecker(){
        mRowBuilder = (MediaListRowBuilder<T>)getRowBuilder();
    }
    
    public void setDataList(List<T> list){
        if(list != null){
            builderChecker();
            mRowBuilder.setDataList(list);
            List<MediaViewRowInfo> rows = mRowBuilder.build();
            setGroup(rows);
        }
    }

    public void setDataList(T[] list){
        if(list != null){
            builderChecker();
            mRowBuilder.setDataList(list);
            List<MediaViewRowInfo> rows = mRowBuilder.build();
            setGroup(rows);
        }
    }

}
