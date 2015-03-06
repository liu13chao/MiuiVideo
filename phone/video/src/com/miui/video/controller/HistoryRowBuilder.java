/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   HistoryRowBuilder.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-11-16
 */
package com.miui.video.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.miui.video.R;
import com.miui.video.local.PlayHistory;
import com.miui.video.util.GenericCreator;
import com.miui.video.util.Util;
import com.miui.video.widget.recommend.HistoryTimeTitleView;
import com.miui.video.widget.recommend.MediaViewRow;

/**
 * @author tianli
 *
 */

public class HistoryRowBuilder extends BaseMediaRowBuilder {

    public static int VIEW_TYPE_HEADER = 0;
    public static int VIEW_TYPE_DATE = 1;
    public static int VIEW_TYPE_POSTER = 2;
    public static int VIEW_TYPE_FOOTER = 3;
    public static int VIEW_TYPE_COUNT = 4;

    private HashMap<String, List<PlayHistory>> mHistoryMap;

    public HistoryRowBuilder(Context context) {
        super(context);
    }

    public void setHistoryMap(HashMap<String, List<PlayHistory>> map){
        mHistoryMap = map;
    }

    @Override
    protected void onBuildRows() {
        List<String> keys = getKeyList(mHistoryMap);
        if(keys.size() > 0){
            addHeaderRow();
            for(String key : keys){
                List<PlayHistory> historyList = mHistoryMap.get(key);
                addDateRow(key);
                addDataRows(historyList);
            }
            addFooterRow();
        }
    }
    
    private List<String> getKeyList(HashMap<String, List<PlayHistory>> map){
        List<String> list = new ArrayList<String>();
        if(map != null){
            Set<String> keys = map.keySet();
            for(String key : keys){
                if(TextUtils.isEmpty(key)){
                    continue;
                }
                List<PlayHistory> historyList = map.get(key);
                if(historyList != null && historyList.size() > 0){
                    list.add(key);
                }
            }
        }
        if(list.size() > 1){
            Collections.sort(list);
            Collections.reverse(list);
        }
        return list;
    }

    private void addHeaderRow(){
        HistoryRowInfo row = new HistoryRowInfo();
        row.mViewType = VIEW_TYPE_HEADER;
        row.mRowIndex = mRows.size();
        row.mRowBuilder = this;
        mRows.add(row);
    }

    private void addDateRow(String date){
        HistoryRowInfo row = new HistoryRowInfo();
        row.mViewType = VIEW_TYPE_DATE;
        row.mRowIndex = mRows.size();
        row.mRowBuilder = this;
        row.mPlayDate = date;
        mRows.add(row);
    }

    private void addDataRows(List<PlayHistory> list){
        if(list == null){
            return;
        }
        int col = 0;
        HistoryRowInfo  row = new HistoryRowInfo();
        row.mViewType = VIEW_TYPE_POSTER;
        row.mRowIndex = mRows.size();
        row.mRowBuilder = this;
        mRows.add(row);
        for(int i = 0; i < list.size(); i++){
            PlayHistory history = list.get(i);
            if(history.getPlayItem() == null){
                continue;
            }
            row.mMediaList.add(history);
            if(col % 3 == 2 && i != list.size() - 1){
                row = new HistoryRowInfo();
                row.mViewType = VIEW_TYPE_POSTER;
                row.mRowIndex = mRows.size();
                row.mRowBuilder = this;
                mRows.add(row);
            }
            col++;
        }
    }

    private void addFooterRow(){
        MediaViewRowInfo row = new MediaViewRowInfo();
        row.mViewType = VIEW_TYPE_FOOTER;
        row.mRowIndex = mRows.size();
        row.mRowBuilder = this;
        mRows.add(row);
    }

    @Override
    protected View getViewOfType(MediaViewRowInfo rowInfo, final View convertView,
            final ViewGroup parent, boolean inEditMode) {
        if(rowInfo.mViewType == VIEW_TYPE_HEADER){
            if(convertView != null){
                return convertView;
            }
            return  LayoutInflater.from(parent.getContext()).inflate(R.layout.history_header, parent, false);
        }else if(rowInfo.mViewType == VIEW_TYPE_FOOTER){
            return MediaPagerFooterBuilder.getBuilder().getView(rowInfo, convertView, parent);
        } else if(rowInfo.mViewType == VIEW_TYPE_DATE){
            HistoryTimeTitleView view = Util.getObject(convertView, new GenericCreator<HistoryTimeTitleView>(){
                @Override
                public HistoryTimeTitleView create() {
                    return (HistoryTimeTitleView)LayoutInflater.from(mContext).inflate(R.layout.history_time_title,
                            parent, false); 
                }
            }, HistoryTimeTitleView.class);
            view.setPlayDate(((HistoryRowInfo)rowInfo).mPlayDate);
            return view;
        } else if(rowInfo.mViewType == VIEW_TYPE_POSTER){
            MediaViewRow view;
            if(convertView instanceof MediaViewRow){
                view = (MediaViewRow)convertView;
            }else{
                view = new MediaViewRow(mContext, R.layout.mixed_media_view, 3);
            }
            view.setMediaContentBuilder(mContentBuilder);
            view.setInEditMode(inEditMode);
            view.setMediaViewClickListener(mViewClickListener);
            view.setMediaInfoGroup(rowInfo.mMediaList);
            view.setBackgroundResource(R.drawable.com_bg_white_corner_v_m_n);
            return view;
        }
        return null;
    }

    private static class HistoryRowInfo extends MediaViewRowInfo{
        public String mPlayDate;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

}
