/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   OfflineLoadingRowBuilder.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-11-20
 */
package com.miui.video.controller;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.miui.video.R;
import com.miui.video.type.BaseMediaInfo;
import com.miui.video.widget.recommend.MediaViewRow;

/**
 * @author tianli
 *
 */
public class SimpleListRowBuilder<T extends BaseMediaInfo> extends MediaListRowBuilder<T> {

    public final static int VIEW_TYPE_LINE = MediaListRowBuilder.VIEW_TYPE_COUNT;
    
    public final static int VIEW_TYPE_COUNT = MediaListRowBuilder.VIEW_TYPE_COUNT + 1;

    public SimpleListRowBuilder(Context context, int layout) {
        super(context, 1, layout);
    }

    @Override
    public int getViewTypeCount() {
        return super.getViewTypeCount() + 1;
    }

    @Override
    protected void buildPosterRows(List<T> posters) {
        int rowCount = mDataList.size();
        for(int i = 0; i < rowCount; i++){
            // add poster
            MediaViewRowInfo row = new MediaViewRowInfo();
            row.mViewType = VIEW_TYPE_POSTER;
            row.mRowIndex = mRows.size();
            row.mRowBuilder = this;
            row.mMediaList.add(mDataList.get(i));
            mRows.add(row);
            // add line
            if(i != rowCount - 1){
                row = new MediaViewRowInfo();
                row.mViewType = VIEW_TYPE_LINE;
                row.mRowBuilder = this;
                mRows.add(row);
            }
        }
    }

    @Override
    protected View getCustomView(MediaViewRowInfo rowInfo, View convertView,
            ViewGroup parent, boolean inEditMode) {
        if(rowInfo.mViewType == VIEW_TYPE_LINE){
            if(convertView != null){
                return convertView;
            }
            return LayoutInflater.from(mContext).inflate(R.layout.list_item_divider_line, parent, false);
        }
        return super.getCustomView(rowInfo, convertView, parent, inEditMode);
    }

    @Override
    public void onPosterViewCreated(MediaViewRowInfo rowInfo, MediaViewRow row) {
        if(rowInfo.mRowIndex == 0){
            if(rowInfo.mRowIndex == mRows.size() - 1){
                row.setContentBackgroudResource(R.drawable.com_bg_white_corner);
            }else{
                row.setContentBackgroudResource(R.drawable.com_bg_white_corner_t);
            }
        }else if(rowInfo.mRowIndex == mRows.size() - 1){
            row.setContentBackgroudResource(R.drawable.com_bg_white_corner_d);
        }else{
            row.setContentBackgroudResource(R.drawable.com_bg_white_corner_v_m);
        }
    }
    
}
