/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   RecommendationComposer.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-11-3
 */
package com.miui.video.controller;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import com.miui.video.DKApp;
import com.miui.video.model.AppSettings;
import com.miui.video.model.loader.BannerLoader;
import com.miui.video.model.loader.DataLoader;
import com.miui.video.model.loader.DataLoader.LoadListener;
import com.miui.video.model.loader.RecommendationLoader;
import com.miui.video.type.ChannelRecommendation;
import com.miui.video.type.TelevisionInfo;
import com.miui.video.widget.banner.BannerView;
import com.miui.video.widget.bg.OnlineBgBmpManager;

/**
 * @author tianli
 *
 */
public class RecommendationComposer {

    private boolean mIsReleased = false;
    
    //Data loader
    private BannerLoader mBannerLoader;
    private RecommendationLoader mRecommendationLoader;

    // Data
    private ArrayList<Object> mBanners;
    private ArrayList<ChannelRecommendation> mChannelRecommend = null;
    
    private UIActionListener mListener;
    
    // Views
    private BannerView mBannerView;
//    private ChannelRecommendAdapter2 mRecommendAdapter;

    public RecommendationComposer(BannerView banner, int channelId){
//        mRecommendAdapter = adapter;
        mBannerView = banner;
        if(channelId > 0){
            mBannerLoader = new BannerLoader(channelId, "");
        }else{
            mBannerLoader = new BannerLoader(0, "");
        }
        mRecommendationLoader = new RecommendationLoader(channelId);
        mBannerLoader.addListener(mLoadListener);
        mRecommendationLoader.addListener(mLoadListener);
    }
    
    public void action(UIActionListener listener){
        if(mIsReleased){
            return;
        }
        mListener = listener;
        loadBanner();
        loadRecommendation();
    }
    
    private void loadBanner() {
        mBannerLoader.load();
    }
    
    public TelevisionInfo[] getTelevisionRecommends(){
        return mRecommendationLoader.getTvRecommendation();
    }
    
    public List<ChannelRecommendation> getRecommendations(){
        if(mRecommendationLoader != null){
            return mRecommendationLoader.getRecommendations();
        }
        return null;
    }
    
    private void loadRecommendation() {
        mRecommendationLoader.load();
    }

    public void release(){
        mIsReleased = true;
        mBannerLoader.removeListener(mLoadListener);
        mRecommendationLoader.removeListener(mLoadListener);
    }

    //data callback
    private LoadListener mLoadListener = new LoadListener() {
        @Override
        public void onLoadFinish(DataLoader loader) {
            if(mIsReleased){
                return;
            }
            if(loader instanceof BannerLoader){
                mBanners = mBannerLoader.getBanners();
                DKApp.getSingleton(OnlineBgBmpManager.class).setBanners(mBanners);
                saveSearchRecommend(mBannerLoader.getSearchKeywords());
                refreshBanner();
            } else if(loader instanceof RecommendationLoader){
                mChannelRecommend = mRecommendationLoader.getRecommendations();
                refreshListView();
            }
        }

        @Override
        public void onLoadFail(DataLoader loader) {
            if(mIsReleased){
                return;
            }
            if(loader instanceof BannerLoader){
                handleBannerError();
            }else if(loader instanceof RecommendationLoader){
                handleRecommendError();
            }
        }
    };
    
    private void refreshBanner() {
        if(mBanners != null && mBanners.size() > 0){
            mBannerView.setBanners(mBanners);
            if(mListener != null){
                mListener.onBannerListener(true);
            }
        }else{
            handleBannerError();
        }
    }
    
    private void handleBannerError(){
        if(mListener != null){
            mListener.onBannerListener(false);
        }
    }
    
    private void refreshListView() {
        if(mChannelRecommend != null && mChannelRecommend.size() > 0){
//            mRecommendAdapter.setRecommendLoader(mRecommendationLoader);
            if(mListener != null){
                mListener.onRecommendListener(true);
            }
        }else{
            handleRecommendError();
        }
    }
    
//    private getRecommend
    
    private void handleRecommendError(){
        if(mListener != null){
            mListener.onRecommendListener(false);
        }
    }
    
    private void saveSearchRecommend(String[] searchRecommend) {
        if(searchRecommend == null || searchRecommend == null || searchRecommend.length == 0) {
            return;
        }
        LinkedHashSet<String> hashSet = new LinkedHashSet<String>();
        for(int i = 0; i < searchRecommend.length; i++) {
            hashSet.add(searchRecommend[i]);
        }
        DKApp.getSingleton(AppSettings.class).saveSearchRecommend(hashSet);
    }
    
//    private void prepareRecommendChannels(List<Channel> channels) {
//        mListChannels = new ArrayList<Channel>(channels);
//        //remove tv channel
//        if(!mEnableTv) {
//            for(int i = 0; i < mListChannels.size(); i++) {
//                Channel channel = mListChannels.get(i);
//                if(channel.isTvChannel()) {
//                    mListChannels.remove(channel);
//                }
//            }
//        } else {
//            mTvInfos = mRecommendationLoader.getTvRecommendation();
//            mTvEpgManager.addTelevisionInfo(mTvInfos);
//        }
//    }

    public interface UIActionListener {
        public void onBannerListener(boolean successful);
        public void onRecommendListener(boolean successful);
    }
}
