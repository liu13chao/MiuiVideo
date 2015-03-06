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

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.miui.video.controller.content.MediaContentBuilder;
import com.miui.video.type.BaseMediaInfo;
import com.miui.video.widget.recommend.MediaViewRow;

/**
 * @author tianli
 *
 */
public class MediaListRowBuilder<T extends BaseMediaInfo> extends BaseMediaRowBuilder {

    public final static int VIEW_TYPE_POSTER = 0;
    public final static int VIEW_TYPE_COUNT = 1;
    
    protected List<T> mDataList = new ArrayList<T>();
    
    private int mLayout;
    private int mColumn;
    private int mSpace = -1;
    
    private MediaContentBuilder mMediaContentBuilder;
    
    public MediaListRowBuilder(Context context, int column, int layout, int space) {
        super(context);
        mColumn = column;
        mLayout = layout;
        mSpace = space;
    }
    
    public MediaListRowBuilder(Context context, int column, int layout) {
        super(context);
        mColumn = column;
        mLayout = layout;
    }
    
    public void setMediaContentBuilder(MediaContentBuilder contentBuilder){
        mMediaContentBuilder = contentBuilder;
    }
    
    public void setDataList(List<T> list){
        if(list == mDataList){
            return;
        }
        mDataList.clear();
        if(list != null){
            mDataList.addAll(list);
        }
    }
    
    public void setDataList(T[] list){
        mDataList.clear();
        if(list != null){
            for(T item : list){
                mDataList.add(item);
            }
        }
    }
    
    protected void buildPosterRows(List<T> posters){
        if(mDataList == null || mColumn == 0){
            return;
        }
        int rowCount = mDataList.size() / mColumn + mDataList.size() % mColumn;
        for(int i = 0; i < rowCount; i++){
            MediaViewRowInfo row = new MediaViewRowInfo();
            row.mViewType = VIEW_TYPE_POSTER;
            row.mRowIndex = mRows.size();
            row.mRowBuilder = this;
            int start = i * mColumn;
            for(int col = 0;col< mColumn; col++){
                if(start + col < mDataList.size()){
                    row.mMediaList.add(mDataList.get(start + col));
                }
            }
            mRows.add(row);
        }
    }
    
    @Override
    protected void onBuildRows() {
        if(mDataList == null || mColumn == 0 || mDataList.size() == 0){
            return;
        }
        buildPosterRows(mDataList);
    }
    
    protected View buildDefaultPoster(MediaViewRowInfo rowInfo, final View convertView,
            final ViewGroup parent, boolean inEditMode){
        MediaViewRow view;
        if(convertView instanceof MediaViewRow){
            view = (MediaViewRow)convertView;
        }else{
            if(mSpace == -1){
                view = new MediaViewRow(mContext, mLayout, mColumn);
            }else{
                view = new MediaViewRow(mContext, mLayout, mColumn, mSpace);
            }
        }
        view.setInEditMode(inEditMode);
        view.setMediaContentBuilder(mMediaContentBuilder);
        view.setMediaViewClickListener(mViewClickListener);
        view.setMediaInfoGroup(rowInfo.mMediaList);
        onPosterViewCreated(rowInfo, view);
        return view;
    }
    
    protected View getCustomView(MediaViewRowInfo rowInfo, final View convertView,
            final ViewGroup parent, boolean inEditMode){
        return null;
    }
    
    @Override
    protected View getViewOfType(MediaViewRowInfo rowInfo, final View convertView,
            final ViewGroup parent, boolean inEditMode) {
        if(rowInfo.mViewType == VIEW_TYPE_POSTER){
            return buildDefaultPoster(rowInfo, convertView, parent, inEditMode);
        }
        return getCustomView(rowInfo, convertView, parent, inEditMode);
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }
    
    public void onPosterViewCreated(MediaViewRowInfo rowInfo, MediaViewRow row){
    }
}


