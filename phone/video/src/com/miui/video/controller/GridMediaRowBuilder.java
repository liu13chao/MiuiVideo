/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   FeatureViewRowBuilder.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-11-14
 */
package com.miui.video.controller;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.miui.video.R;
import com.miui.video.type.BaseMediaInfo;
import com.miui.video.widget.recommend.MediaViewRow;

/**
 * @author tianli
 *
 */
public class GridMediaRowBuilder<T extends BaseMediaInfo> extends MediaListRowBuilder<T> {

    public final static int VIEW_TYPE_HEADER = MediaListRowBuilder.VIEW_TYPE_COUNT;
    public final static int VIEW_TYPE_FOOTER = MediaListRowBuilder.VIEW_TYPE_COUNT + 1;
    public final static int VIEW_TYPE_COUNT = MediaListRowBuilder.VIEW_TYPE_COUNT + 2;
    
    public GridMediaRowBuilder(Context context, int column, int layout, int space) {
        super(context, column, layout, space);
    }
    
    public GridMediaRowBuilder(Context context, int column, int layout) {
        super(context, column, layout);
    }
    
    private void addHeaderRow(){
        MediaViewRowInfo row = new MediaViewRowInfo();
        row.mViewType = VIEW_TYPE_HEADER;
        row.mRowIndex = mRows.size();
        row.mRowBuilder = this;
        mRows.add(row);
    }

    private void addFooterRow(){
        MediaViewRowInfo row = new MediaViewRowInfo();
        row.mViewType = VIEW_TYPE_FOOTER;
        row.mRowIndex = mRows.size();
        row.mRowBuilder = this;
        mRows.add(row);
    }
    
    @Override
    protected void onBuildRows() {
        if(mDataList != null && mDataList.size() > 0){
            addHeaderRow();
            buildPosterRows(mDataList);
            addFooterRow();
        }
    }
    
    protected View getCustomView(MediaViewRowInfo rowInfo, final View convertView,
            final ViewGroup parent, boolean inEditMode){
        if(rowInfo.mViewType == VIEW_TYPE_HEADER){
            return MediaPagerHeaderBuilder.getBuilder().getView(rowInfo, convertView, parent);
        }else if(rowInfo.mViewType == VIEW_TYPE_FOOTER){
            return MediaPagerFooterBuilder.getBuilder().getView(rowInfo, convertView, parent);
        }
        return getCustomView(rowInfo, convertView, parent, inEditMode);
    }
    
    protected int getMediaViewRowBackgroud(){
        return R.drawable.com_bg_white_corner_v_m;
    }
    
    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

    @Override
    public void onPosterViewCreated(MediaViewRowInfo rowInfo, MediaViewRow row) {
      row.setBackgroundResource(R.drawable.com_bg_white_corner_v_m_n);
    }
}
