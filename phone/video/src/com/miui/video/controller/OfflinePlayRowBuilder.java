/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   OfflinePlayRowBuilder.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-11-22
 */
package com.miui.video.controller;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.miui.video.R;
import com.miui.video.offline.OfflineMedia;

/**
 * @author tianli
 *
 */
public class OfflinePlayRowBuilder extends SimpleListRowBuilder<OfflineMedia> {

    public final static int VIEW_TYPE_MORE = SimpleListRowBuilder.VIEW_TYPE_COUNT;
    public final static int VIEW_TYPE_COUNT = SimpleListRowBuilder.VIEW_TYPE_COUNT + 1;
    
    private OnClickListener mOnMoreClickListener;
    
    public OfflinePlayRowBuilder(Context context) {
        super(context, R.layout.offline_play_view);
    }

    public void setOnMoreClickListener(OnClickListener onMoreClickListener) {
        this.mOnMoreClickListener = onMoreClickListener;
    }

    @Override
    protected void buildPosterRows(List<OfflineMedia> posters) {
        super.buildPosterRows(posters);
        MediaViewRowInfo row = new MediaViewRowInfo();
        row.mViewType = VIEW_TYPE_MORE;
        row.mRowIndex = mRows.size();
        row.mRowBuilder = this;
        mRows.add(row);
    }

    @Override
    public int getViewTypeCount() {
        return super.getViewTypeCount() + 1;
    }

    @Override
    protected View getCustomView(MediaViewRowInfo rowInfo, View convertView,
            ViewGroup parent, boolean inEditMode) {
        if(rowInfo.mViewType == VIEW_TYPE_MORE){
            if(convertView != null){
                return convertView;
            }
            View view = LayoutInflater.from(mContext).inflate(R.layout.offline_ep_btn_more, parent, false);
            view.findViewById(R.id.offline_ep_button).setOnClickListener(mOnMoreClickListener);
            return view;
        }
        return super.getCustomView(rowInfo, convertView, parent, inEditMode);
    }
}
