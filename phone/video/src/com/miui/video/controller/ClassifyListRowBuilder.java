/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   ClassifyListRowBuilder.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-11-16
 */
package com.miui.video.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.miui.video.R;
import com.miui.video.controller.content.MediaContentBuilder;
import com.miui.video.type.BaseMediaInfo;
import com.miui.video.widget.recommend.MediaViewRow;

/**
 * @author tianli
 *
 */

public class ClassifyListRowBuilder<T extends BaseMediaInfo> extends BaseMediaRowBuilder {

    public final static int VIEW_TYPE_HEADER = 0;
    public final static int VIEW_TYPE_FOOTER = 1;
    public final static int VIEW_TYPE_POSTER = 2;
    public final static int VIEW_TYPE_COUNT = 3;

    private HashMap<String, List<T>> mClassifyMap;
    
    private MediaContentBuilder mMediaContentBuilder;
    
    int mLayout;
    int mColumn = 3;
    
//    public ClassifyListRowBuilder(Context context) {
//        super(context);
//    }
    
    public ClassifyListRowBuilder(Context context, int column, int layout) {
        super(context);
        mLayout = layout;
        mColumn = column;
    }

    public void setClassifyMap(HashMap<String, List<T>> map){
        mClassifyMap = map;
    }
    
    public void setMediaContentBuilder(MediaContentBuilder mediaContentBuilder) {
        mMediaContentBuilder = mediaContentBuilder;
    }

    protected void buildPosterRows(List<T> posters){
        if(posters == null){
            return;
        }
        int col = 0;
        ClassifyRowInfo  row = new ClassifyRowInfo();
        row.mViewType = VIEW_TYPE_POSTER;
        row.mRowBuilder = this;
        mRows.add(row);
        for(int i = 0; i < posters.size(); i++){
            T media = posters.get(i);
            row.mMediaList.add(media);
            if(col % 3 == 2 && i != posters.size() - 1){
                row = new ClassifyRowInfo();
                row.mViewType = VIEW_TYPE_POSTER;
                row.mRowBuilder = this;
                mRows.add(row);
            }
            col++;
        }
    }
    
    protected View getViewOfPoster(MediaViewRowInfo rowInfo, final View convertView,
            final ViewGroup parent, boolean inEditMode) {
        MediaViewRow view;
        if(convertView instanceof MediaViewRow){
            view = (MediaViewRow)convertView;
        }else{
            view = new MediaViewRow(mContext, mLayout, mColumn);
        }
        view.setInEditMode(inEditMode);
        view.setMediaContentBuilder(mMediaContentBuilder);
        view.setMediaViewClickListener(mViewClickListener);
        view.setMediaInfoGroup(rowInfo.mMediaList);
        view.setBackgroundResource(R.drawable.com_bg_white_corner_v_m_n);
        return view;
    }
    
    public int getViewTypeCount(){
        return VIEW_TYPE_COUNT;
    }
    
    @Override
    final protected void onBuildRows() {
        if(mClassifyMap == null){
            return;
        }
        Set<String> keys = mClassifyMap.keySet();
        if(keys == null || keys.size() == 0){
            return;
        }
        for(String key : keys){
            if(TextUtils.isEmpty(key) || mClassifyMap.get(key) == null){
                continue;
            }
            List<T> posters = mClassifyMap.get(key);
            if(posters.size() > 0){
                addHeaderRow(key);
                buildPosterRows(posters);
                addFooterRow();
            }
        }
    }

    private void addHeaderRow(String category){
        ClassifyRowInfo row = new ClassifyRowInfo();
        row.mViewType = VIEW_TYPE_HEADER;
        row.mRowIndex = mRows.size();
        row.mRowBuilder = this;
        row.mCategory = category;
        mRows.add(row);
    }

    private void addFooterRow(){
        ClassifyRowInfo row = new ClassifyRowInfo();
        row.mViewType = VIEW_TYPE_FOOTER;
        row.mRowIndex = mRows.size();
        row.mRowBuilder = this;
        mRows.add(row);
    }

    @Override
    protected View getViewOfType(MediaViewRowInfo rowInfo, final View convertView,
            final ViewGroup parent, boolean inEditMode) {
        if(rowInfo.mViewType == VIEW_TYPE_HEADER){
            ClassifyRowInfo row = (ClassifyRowInfo)rowInfo;
            return new MediaPagerTitleBuilder(mContext, row.mCategory).getView(rowInfo, convertView, parent);
        }else if(rowInfo.mViewType == VIEW_TYPE_FOOTER){
            return MediaPagerFooterBuilder.getBuilder().getView(rowInfo, convertView, parent);
        }else{
            return getViewOfPoster(rowInfo, convertView, parent, inEditMode);
        }
    }

    public static class ClassifyRowInfo extends MediaViewRowInfo{
        public String mCategory;
    }

}
