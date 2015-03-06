/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   RecommendedViewRowBuilder.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-11-14
 */
package com.miui.video.controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.miui.video.R;
import com.miui.video.controller.content.RecommendedGridContentBuilder;
import com.miui.video.type.ChannelRecommendation;
import com.miui.video.util.GenericCreator;
import com.miui.video.util.Util;
import com.miui.video.widget.recommend.BaseMediaViewRow;
import com.miui.video.widget.recommend.MediaPagerTab.OnTitleSelectedListener;
import com.miui.video.widget.recommend.MediaPagerTabHeader;
import com.miui.video.widget.recommend.MediaPagerTitleHeader;
import com.miui.video.widget.recommend.MediaViewRow;
import com.miui.video.widget.recommend.RecommendedPagerFooter;
import com.miui.video.widget.recommend.TvMediaViewRow;

/**
 * @author tianli
 *
 */
public class RecommendedViewBuilder extends MediaViewBuilder {
   
    public Context mContext;
    public ChannelRecommendation mRecommendation;
    public MediaViewClickListener mViewClickListener;
    public ChannelEntryHandler mChannelEntryHandler;
    public OnTitleSelectedListener mTitleListener;
    
    public RecommendedViewBuilder(Context context, ChannelRecommendation recommendation){
        mContext = context;
        mRecommendation = recommendation;
    }

    public void setViewClickListener(MediaViewClickListener viewClickHandler) {
        this.mViewClickListener = viewClickHandler;
    }

    public void setChannelEntryHandler(ChannelEntryHandler channelEntryHandler) {
        this.mChannelEntryHandler = channelEntryHandler;
    }

    public void setTitleListener(OnTitleSelectedListener titleListener) {
        this.mTitleListener = titleListener;
    }

    @Override
    public View getView(MediaViewRowInfo rowInfo, final View convertView, final ViewGroup parent) {
        if(mRecommendation == null){
            return null;
        }
        if(rowInfo.mViewType == RecommendedMediaViewRowBuilder.VIEW_TYPE_TITLE_HEADER){
            MediaPagerTitleHeader view = Util.getObject(convertView, new GenericCreator<MediaPagerTitleHeader>() {
                @Override
                public MediaPagerTitleHeader create() {
                    return (MediaPagerTitleHeader)LayoutInflater.from(mContext).
                          inflate(R.layout.recommend_card_title_header, parent, false);
                }
            }, MediaPagerTitleHeader.class);
            if(mRecommendation.data != null && mRecommendation.data.length > 0){
                view.setTitle(mRecommendation.data[0].getTabName());
            }
            return view;
        } else if(rowInfo.mViewType == RecommendedMediaViewRowBuilder.VIEW_TYPE_TAB_HEADER){
            MediaPagerTabHeader view = Util.getObject(convertView, new GenericCreator<MediaPagerTabHeader>() {
                @Override
                public MediaPagerTabHeader create() {
                    return (MediaPagerTabHeader)LayoutInflater.from(mContext).
                            inflate(R.layout.recommend_home_card_header, parent, false);
                }
            }, MediaPagerTabHeader.class);
            view.setTabs(mRecommendation.getRecommendTabNames());
            view.setTabTitleSelectListener(mTitleListener);
            return view;
        }else if(rowInfo.mViewType == RecommendedMediaViewRowBuilder.VIEW_TYPE_FOOTER){
            RecommendedPagerFooter view = Util.getObject(convertView, new GenericCreator<RecommendedPagerFooter>() {
                @Override
                public RecommendedPagerFooter create() {
                    return (RecommendedPagerFooter)LayoutInflater.from(mContext).inflate(R.layout.recommend_home_card_footer,
                          parent, false); 
                    }
            }, RecommendedPagerFooter.class);
            view.setChannelRecommendation(mRecommendation);
            view.setChannelEntryHandler(mChannelEntryHandler);
            return view;
        }else{
            BaseMediaViewRow row;
            if(convertView instanceof BaseMediaViewRow){
                row = (BaseMediaViewRow)convertView;
            }else{
                if(rowInfo.mViewType == RecommendedMediaViewRowBuilder.VIEW_TYPE_TV){
                    row = new TvMediaViewRow(mContext);
                }else if(rowInfo.mViewType == RecommendedMediaViewRowBuilder.VIEW_TYPE_POSTER_H){
                    row = new MediaViewRow(mContext, R.layout.media_view_recommended_h, 2);
                }else if(rowInfo.mViewType == RecommendedMediaViewRowBuilder.VIEW_TYPE_INFO_BANNER){
                    row= new MediaViewRow(mContext, R.layout.media_view_recommended_info_banner,  1);
                }else {
                    row= new MediaViewRow(mContext, R.layout.media_view_recommended_v,  3);
                } 
            }
            row.setMediaContentBuilder(new RecommendedGridContentBuilder(mContext, mRecommendation));
            row.setMediaViewClickListener(mViewClickListener);
            row.setMediaInfoGroup(rowInfo.mMediaList);
            row.setBackgroundResource(R.drawable.com_bg_white_corner_v_m_n);
            return row;
        }
    }
}
