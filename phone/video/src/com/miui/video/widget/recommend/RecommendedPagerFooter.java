/**
 *  Copyright(c) 2014 XiaoMi TV Group
 *    
 *  RecommendedPagerFooter.java
 *
 *  @author tianli(tianli@xiaomi.com)
 *
 */
package com.miui.video.widget.recommend;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.miui.video.DKApp;
import com.miui.video.R;
import com.miui.video.controller.ChannelEntryHandler;
import com.miui.video.model.ChannelInfoStore;
import com.miui.video.type.Channel;
import com.miui.video.type.ChannelRecommendation;

/**
 * @author tianli
 *
 */
public class RecommendedPagerFooter extends LinearLayout{

    private ChannelRecommendation mRecommendation;
    private ChannelEntryHandler mChannelEntryHandler;
    
    private TextView mMoreBtn;
    
    public RecommendedPagerFooter(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public RecommendedPagerFooter(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RecommendedPagerFooter(Context context) {
        super(context);
    }

    @Override
    protected void onFinishInflate() {
        mMoreBtn = (TextView)findViewById(R.id.media_pager_btn_more);
        mMoreBtn.setOnClickListener(mOnClickListener);
    }
    
    public void setChannelRecommendation(ChannelRecommendation recommendation){
       mRecommendation = recommendation;
       refreshMoreBtn();
    }
    
    public void setChannelEntryHandler(ChannelEntryHandler mEntryHandler) {
        this.mChannelEntryHandler = mEntryHandler;
    }

    private void refreshMoreBtn(){
        if(mRecommendation != null && mRecommendation.id > 0){
            String str = getResources().getString(R.string.more);
            String name = mRecommendation.name;
            if(TextUtils.isEmpty(name)){
                Channel channel = DKApp.getSingleton(ChannelInfoStore.class).getChannel(mRecommendation.id);
                if(channel != null){
                    name = channel.name;
                }
            }
            if(TextUtils.isEmpty(name)){
                name = "";
            }
            str = String.format(str, name);
            if(mMoreBtn.getVisibility() != View.VISIBLE){
                mMoreBtn.setVisibility(View.VISIBLE);    
            }
            mMoreBtn.setText(str);
        }else{
            if(mMoreBtn.getVisibility() == View.VISIBLE){
                mMoreBtn.setVisibility(View.GONE);
            }
        }
    }
    
    public OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if(mChannelEntryHandler == null){
                return;
            }
            if(view == mMoreBtn){
                mChannelEntryHandler.onEnterChannel(view, mRecommendation);
            }
        }
    };

}
