/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   RecommendedMediaViewRowBuilder.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-11-13
 */
package com.miui.video.controller;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.miui.video.type.BaseMediaInfo;
import com.miui.video.type.Channel;
import com.miui.video.type.ChannelRecommendation;
import com.miui.video.type.ChannelRecommendationTab;
import com.miui.video.widget.recommend.MediaPagerTab.OnTitleSelectedListener;
import com.miui.video.widget.recommend.MediaPagerTabHeader;

/**
 * @author tianli
 *
 */
public class RecommendedMediaViewRowBuilder extends BaseMediaRowBuilder {

    public static int VIEW_TYPE_TAB_HEADER = 0;
    public static int VIEW_TYPE_INFO_BANNER = 1;
    public static int VIEW_TYPE_POSTER_V = 2;
    public static int VIEW_TYPE_POSTER_H = 3;
    public static int VIEW_TYPE_TV = 4;
    public static int VIEW_TYPE_FOOTER = 5;
    public static int VIEW_TYPE_TITLE_HEADER = 6;
    public static int VIEW_TYPE_COUNT = 7;
    
    protected ChannelRecommendation mRecommendation;
    protected int mCurPage = 0;
    
    protected RecommendedViewBuilder mViewBuilder;
    protected OnTabSelectListener mTabSelectListener;
    
    public RecommendedMediaViewRowBuilder(Context context, ChannelRecommendation recommendation){
        super(context);
        mRecommendation = recommendation;
        mViewBuilder = new RecommendedViewBuilder(context, recommendation);
        mViewBuilder.setTitleListener(mTabTitleListener);
    }

    public void setPageNo(int pageNo){
        mCurPage = pageNo;
        mRows.clear();
        onBuildRows();
    }
    
    public void setTabSelectListener(OnTabSelectListener tabSelectListener) {
        this.mTabSelectListener = tabSelectListener;
    }

    @Override
    protected void onBuildRows() {
        if(mRecommendation == null || mRecommendation.data == null || mCurPage < 0 ||
                mCurPage >= mRecommendation.data.length){
            return;
        }
        ChannelRecommendationTab tab = mRecommendation.data[mCurPage];
        if(tab== null || tab.getRecommendMedias() == null){
            return;
        }
        int count = getRowCount(tab);
        BaseMediaInfo[] medias = tab.getRecommendMedias();
        int column = getColumnSize();
        for(int i = 0; i < count; i++){
            MediaViewRowInfo rowInfo = new MediaViewRowInfo();
            mRows.add(rowInfo);
            rowInfo.mRowBuilder = this;
            if(i == 0){
                if(mRecommendation.getRecommendTabCount() == 1){
                    rowInfo.mViewType = VIEW_TYPE_TITLE_HEADER;
                }else{
                    rowInfo.mViewType = VIEW_TYPE_TAB_HEADER;
                }
            }else if(i == count - 1){
                rowInfo.mViewType = VIEW_TYPE_FOOTER;
            }else{
                if(isTvType()){
                    rowInfo.mViewType = VIEW_TYPE_TV;
                }else if(isHorizontal()){
                    rowInfo.mViewType = VIEW_TYPE_POSTER_H;
                }else{
                    rowInfo.mViewType = VIEW_TYPE_POSTER_V;
                }
                int start = (i - 1) * column;
                for(int col = 0; col < column; col++){
                    if(start + col < medias.length){
                        rowInfo.mMediaList.add(medias[start + col]);
                    }
                }
            }
        }
    }

    @Override
    public void setMediaViewClickListener(MediaViewClickListener viewClickHandler) {
        super.setMediaViewClickListener(viewClickHandler);
        mViewBuilder.setViewClickListener(viewClickHandler);
    }
    
    public void setChannelEntryHandler(ChannelEntryHandler handler){
        mViewBuilder.setChannelEntryHandler(handler);
    }

    private int getRowCount(ChannelRecommendationTab tab){
        if(tab != null){
            int columns = getColumnSize();
            BaseMediaInfo[] medias = tab.getRecommendMedias();
            if(medias != null && medias.length > 0){
                return 2 + (medias.length) / columns + (medias.length) % columns;
            }
        }
        return 0;
    }

    private int getColumnSize(){
        if(mRecommendation != null && mRecommendation.listtype == UIConfig.LIST_TYPE_H){
            return 2;
        }
        return 3;
    }

    private boolean isHorizontal(){
        if(mRecommendation != null && mRecommendation.listtype == UIConfig.LIST_TYPE_H){
            return true;
        }
        return false;
    }

    private boolean isTvType(){
        if(mRecommendation != null && Channel.isTvChannel(mRecommendation.id)){
            return true;
        }
        return false;
    }
    
    private void notifyTabSelected(int position){
        if(mTabSelectListener != null){
            mTabSelectListener.onTabSelected(this, position);
        }
    }

    @Override
    protected View getViewOfType(MediaViewRowInfo rowInfo, View convertView, ViewGroup parent, boolean inEditMode) {
        View view = mViewBuilder.getView(rowInfo, convertView, parent);
        if(view instanceof MediaPagerTabHeader){
           ( (MediaPagerTabHeader)view).setCurPage(mCurPage);
        }
        return view;
    }
    
    public static interface OnTabSelectListener{
        public void onTabSelected(BaseMediaRowBuilder builder, int position);
    }
    
    private OnTitleSelectedListener mTabTitleListener = new OnTitleSelectedListener() {
        @Override
        public void onTitleSelected(int position) {
            if(position != mCurPage){
                setPageNo(position);
                notifyTabSelected(position);
            }
        }
    };

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }
}
