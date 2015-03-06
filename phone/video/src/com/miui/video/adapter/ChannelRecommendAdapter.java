/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   InfoRecommendAdapter.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-11-6
 */
package com.miui.video.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.miui.video.controller.BaseMediaRowBuilder;
import com.miui.video.controller.ChannelEntryHandler;
import com.miui.video.controller.MediaViewClickListener;
import com.miui.video.controller.MediaViewRowInfo;
import com.miui.video.controller.RecommendedInfoViewRowBuilder;
import com.miui.video.controller.RecommendedMediaViewRowBuilder;
import com.miui.video.controller.RecommendedMediaViewRowBuilder.OnTabSelectListener;
import com.miui.video.type.Channel;
import com.miui.video.type.ChannelRecommendation;
import com.miui.video.util.Util;

/**
 * @author tianli
 *
 */
public class ChannelRecommendAdapter extends BaseGroupAdapter<MediaViewRowInfo> {

    private Channel mChannel;
    private List<BaseMediaRowBuilder> mCards = new ArrayList<BaseMediaRowBuilder>();
    
    public ChannelRecommendAdapter(Context context, Channel channel) {
        super(context);
        mChannel = channel;
    }
    
    public ChannelRecommendAdapter(Context context) {
        super(context);
    }

    private ChannelRecommendation[] mRecommends;
    
    private MediaViewClickListener mViewClickListener;
    private ChannelEntryHandler mChannelEntryHandler;
    
    public void setChannelRecommendations(ChannelRecommendation[] recommend){
        mRecommends = recommend;
        initCards();
    }
    
    public void setChannelRecommendations(List<ChannelRecommendation> recommend){
        mRecommends = Util.list2Array(recommend, ChannelRecommendation.class);
        initCards();
    }
    
    public void setViewClickHandler(MediaViewClickListener clickHandler){
        mViewClickListener = clickHandler;
    }
    
    public void setChannelEntryHandler(ChannelEntryHandler handler){
        mChannelEntryHandler = handler;
    }
    
    private void initCards(){
        if(mRecommends == null){
            return;
        }
        mCards.clear();
        for(ChannelRecommendation recommend : mRecommends){
            BaseMediaRowBuilder card = createRecommendedCard(recommend);
            if(card != null){
                mCards.add(card);
            }
        }
        buildRows();
    }
    
    private BaseMediaRowBuilder createRecommendedCard(ChannelRecommendation recommendation){
        if(recommendation != null){
            RecommendedMediaViewRowBuilder card;
            if(mChannel != null && mChannel.isInformationType()){
                card = new RecommendedInfoViewRowBuilder(mContext, recommendation);
            }else{
                card = new RecommendedMediaViewRowBuilder(mContext, recommendation);
            }
            card.setTabSelectListener(mTabSelectListener);
            card.build();
            return card;
        }
        return null;
    }
    
    private void buildRows(){
        ArrayList<MediaViewRowInfo> list = 
                new ArrayList<MediaViewRowInfo>();
        for(BaseMediaRowBuilder card : mCards){
          list.addAll(card.getRows());
        }
        setGroup(list);
    }
    
    @Override
    public int getCount() {
        return mGroup.size();
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MediaViewRowInfo rowInfo = getItem(position);
        if(rowInfo != null && rowInfo.mRowBuilder != null){
            rowInfo.mRowBuilder.setMediaViewClickListener(mViewClickListener);
            if(rowInfo.mRowBuilder instanceof RecommendedMediaViewRowBuilder){
                ( (RecommendedMediaViewRowBuilder)rowInfo.mRowBuilder).
                    setChannelEntryHandler(mChannelEntryHandler);
            }
            return rowInfo.mRowBuilder.getView(parent.getContext(), convertView, parent, 
                    rowInfo, false);
        }
        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        if(position >= 0 && position < mGroup.size()){
            return mGroup.get(position).mViewType;
        }
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return RecommendedMediaViewRowBuilder.VIEW_TYPE_COUNT;
    }
    
    private OnTabSelectListener mTabSelectListener = new OnTabSelectListener() {
        @Override
        public void onTabSelected(BaseMediaRowBuilder card, int position) {
            buildRows();
        }
    };
    
}
