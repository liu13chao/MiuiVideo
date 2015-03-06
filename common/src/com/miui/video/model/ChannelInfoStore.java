/**
 *  Copyright(c) 2014 XiaoMi TV Group
 *    
 *  ChannelInfoStore.java
 *
 *  @author tianli(tianli@xiaomi.com)
 *
 *  2014-11-3
 */
package com.miui.video.model;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import android.content.Context;

import com.miui.video.model.loader.ChannelLoader;
import com.miui.video.model.loader.DataLoader;
import com.miui.video.model.loader.DataLoader.LoadListener;
import com.miui.video.type.Channel;

/**
 * @author tianli
 *
 */
public class ChannelInfoStore extends AppSingleton {

    public ChannelLoader mDataLoader;
    
    public Hashtable<Integer, Channel> mChannels = null;
    
    private List<WeakReference<ChannelInfoDataListener>> mObservers = new 
            ArrayList<WeakReference<ChannelInfoDataListener>>();
    
    @Override
    public void init(Context context) {
        super.init(context);
        mDataLoader = new ChannelLoader();
        mDataLoader.addListener(mLoadListener);
        mDataLoader.load();
    }

    public boolean isLoaded(){
        if(mChannels !=  null){
            return mChannels.size() > 0;
        }
        return false;
    }
    
    public void load(){
        mDataLoader.load();
    }
    
    public void addObserver(ChannelInfoDataListener observer){
        if(observer == null){
            return;
        }
        for(int i = 0; i < mObservers.size(); i++){
            WeakReference<ChannelInfoDataListener> listener = mObservers.get(i);
            if(listener != null && listener.get() == observer){
                // already added.
                return;
            }
        }
        mObservers.add(new WeakReference<ChannelInfoDataListener>(observer));
    }
    
    public void removeObserver(ChannelInfoDataListener observer){
        if(observer == null){
            return;
        }
        for(int i = 0; i < mObservers.size(); i++){
            WeakReference<ChannelInfoDataListener> listener = mObservers.get(i);
            if(listener != null && listener.get() == observer){
                mObservers.remove(listener);
                return;
            }
        }
    }
    
    public void notifyObservers(boolean successful){
        for(int i = 0; i < mObservers.size(); i++){
            WeakReference<ChannelInfoDataListener> ref = mObservers.get(i);
            if(ref != null){
                ChannelInfoDataListener listener = ref.get();
                if(listener != null){
                    listener.onChannelInfoDone(successful);
                }
            }
        }
    }
    
    public Channel getChannel(Channel channel){
        if(channel != null){
            Channel cache = getChannel(channel.id);
            if(cache != null){
                return cache;
            }
        }
        return channel;
    }
    
    public Channel getChannel(int channelId){
        if(mChannels != null){
            return mChannels.get(channelId);
        }
        return null;
    }
    
    public String getChannelName(int channelId){
        if(mChannels != null){
            Channel channel = getChannel(channelId);
            if(channel != null){
                return channel.name;
            }
        }
        return null;
    }
    
    private void cacheChannels(){
        Hashtable<Integer, Channel> set = new Hashtable<Integer, Channel>();
        List<Channel> list = mDataLoader.getChannels();
        if(list != null){
            for(Channel  channel : list){
                 if(channel != null){
                    cacheChannel(set, channel);
                }
            }
            mChannels = set;
        }
    }
    
    private void cacheChannel(Hashtable<Integer, Channel> set, Channel channel){
        if(set == null || channel == null){
            return;
        }
        if(set.get(channel.id) != null && (set.get(channel.id).subfilter != null || 
                set.get(channel.id).sub != null)){
            return;
        }
        set.put(channel.id, channel);
        if(channel.sub != null){
            for(Channel  subChannel : channel.sub){
                if(subChannel != null){
                    cacheChannel(set, subChannel);
                }
            }
        }
    }
    
    public LoadListener mLoadListener = new LoadListener(){
        @Override
        public void onLoadFinish(DataLoader loader) {
            if(isLoaded()){
                return; //  duplicate request, avoid it.
            }
            cacheChannels();
            if(isLoaded()){
                notifyObservers(true);
            }else{
                notifyObservers(false);
            }
        }

        @Override
        public void onLoadFail(DataLoader loader) {
            notifyObservers(false);
        }
    };
    
    public static interface ChannelInfoDataListener{
        public void onChannelInfoDone(boolean isSuccesful);
    }

}
