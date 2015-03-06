package com.miui.video.info;

import java.util.HashMap;

import android.annotation.SuppressLint;

import com.miui.video.DKApp;
import com.miui.video.model.ChannelInfoStore;
import com.miui.video.type.Channel;

public class InfoChannelDataFactory {
	
	private static InfoChannelDataFactory mInstance = null;
	private InfoChannelDataFactory(){}
	
	@SuppressLint("UseSparseArrays")
    private HashMap<Integer, InfoChannelDataManager> mManagers = new HashMap<Integer, InfoChannelDataManager>();
	
	public synchronized static InfoChannelDataFactory getInstance(){
		if(mInstance == null){
			mInstance = new InfoChannelDataFactory();
		}
		return mInstance;
	}
	
	public InfoChannelDataManager getManager(int channelId){
	    InfoChannelDataManager manager = mManagers.get(channelId);
	    if(manager == null){
	        Channel channel = DKApp.getSingleton(ChannelInfoStore.class).getChannel(channelId);
	        if(channel != null){
	            manager = new InfoChannelDataManager();
	            manager.setChannelId(channelId);
	            mManagers.put(channelId, manager);
	        }
	    }
		return manager;
	}
	
	public InfoChannelDataManager getManager(Channel channel){
	    if(channel == null){
	        return null;
	    }
        InfoChannelDataManager manager = mManagers.get(channel.id);
        if(manager == null){
            if(channel != null){
                manager = new InfoChannelDataManager();
//                manager.setData(channel);
                manager.setChannelId(channel.id);
                mManagers.put(channel.id, manager);
            }
        }
        return manager;
    }
	
}
