/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   InfoChannelFragmentLoader.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-11-6
 */
package com.miui.video.info;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.miui.video.DKApp;
import com.miui.video.R;
import com.miui.video.base.BaseFragment;
import com.miui.video.controller.FragmentCreator;
import com.miui.video.model.ChannelInfoStore;
import com.miui.video.model.ChannelInfoStore.ChannelInfoDataListener;
import com.miui.video.model.loader.DataLoader;
import com.miui.video.model.loader.DataLoader.LoadListener;
import com.miui.video.model.loader.RecommendationLoader;
import com.miui.video.type.Channel;
import com.miui.video.type.ChannelRecommendation;
import com.miui.video.util.Util;

/**
 * @author tianli
 *
 */
public class InfoChannelFragmentLoader {

    public final static String TAG = "InfoChannelFragmentLoader";
    
    // Data Store
    private Channel mChannel;
    private RecommendationLoader mRecomendLoader;
    private ChannelInfoStore mChannelStore;
    
    private boolean mIsRecommendLoaded = false;
    
    private InfoChannelRecommendFragment mRecommendFragment;
    
    // Listeners
    InfoChannelFragmentListener mListener;
    
    public InfoChannelFragmentLoader(Channel channel){
        mChannel = channel;
        mChannelStore = DKApp.getSingleton(ChannelInfoStore.class);
        mChannelStore.addObserver(mChannelListener);
        mRecomendLoader = new RecommendationLoader(mChannel.id);
        mRecomendLoader.addListener(mRecommendListener);
    }
    
    public void load(){
        if(!mChannelStore.isLoaded()){
            mChannelStore.load();
        }
        mRecomendLoader.load();
    }
    
    public void setListener(InfoChannelFragmentListener listener){
        mListener = listener;
    }
    
    public void release(){
        mChannelStore.removeObserver(mChannelListener);
        mRecomendLoader.removeListener(mRecommendListener);
    }
    
    private void initFragments(){
        mChannel = DKApp.getSingleton(ChannelInfoStore.class).getChannel(mChannel.id);
        List<BaseFragment> fragments = new ArrayList<BaseFragment>();
        List<CharSequence> names = new ArrayList<CharSequence>();
        if(mChannel != null && mChannel.sub != null) {
            Channel[] sub = mChannel.sub;
            int start = 0;
            if(mRecommendFragment != null){
                start = 1;
                names.add(DKApp.getAppContext().getResources().getString(R.string.channel_recommend));
                fragments.add(mRecommendFragment);
            }
            for(int i = 0; i < sub.length; i++) {
                Channel channel = sub[i];
                InfoChannelFragment fragment = FragmentCreator.createInfoChannelFragment(mChannel, 
                        channel);
                if(fragment != null){
                    fragments.add(fragment);
                    names.add(channel.name);
                }
                if(start + i == 0) {
                    fragment.setForceInitData(true);
                }
            }
        }
        if(fragments.size() > 0){
            notifyFragments(Util.list2Array(fragments, BaseFragment.class),  
                    Util.list2Array(names, CharSequence.class));
        }else{
            notifyFragments(null, null);  
        }
    }
    
    private void notifyFragments(BaseFragment[] fragments, CharSequence[] names){
        if(mListener != null){
            mListener.onFragmentLoad(fragments, names);
        }
    }
    
    private ChannelInfoDataListener mChannelListener = new ChannelInfoDataListener() {
        @Override
        public void onChannelInfoDone(boolean isSuccesful) {
            Log.d(TAG, "channel info load done.  ret is " + isSuccesful);
            if(isSuccesful){
                if(mIsRecommendLoaded){
                    initFragments();
                }
            }else{
                notifyFragments(null, null);
            }
        }
    };
    
    private LoadListener mRecommendListener = new LoadListener() {
        @Override
        public void onLoadFinish(DataLoader loader) {
            Log.d(TAG, "recommend load finished.");
            if(mIsRecommendLoaded){
                return;
            }
            mIsRecommendLoaded = true;
            if(DKApp.getSingleton(ChannelInfoStore.class).isLoaded()){
                List<ChannelRecommendation> recommends = mRecomendLoader.getRecommendations();
                if(recommends != null && recommends.size() > 0){
                    mRecommendFragment = FragmentCreator.createInfoRecommendFragment(
                            mChannel, Util.list2Array(recommends, ChannelRecommendation.class));
                }
                initFragments();
            }
        }
        
        @Override
        public void onLoadFail(DataLoader loader) {
            Log.d(TAG, "recommend load error.");
            // handle on error
            mIsRecommendLoaded = true;
            notifyFragments(null, null);
        }
    };

   public interface InfoChannelFragmentListener{
       public void onFragmentLoad(BaseFragment[] fragments, CharSequence[] names);
   }
    
}
