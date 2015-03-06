/**
 *   Copyright(c) 2012 DuoKan TV Group
 *    
 *   ChannelRecommendation.java
 *
 *   @author tianli(tianli@duokan.com)
 *
 *   2012-8-21 
 */
package com.miui.video.type;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author tianli
 * 
 */
public class ChannelRecommendation implements Serializable {
	private static final long serialVersionUID = 2L;

	public int id;
	public int count;
	public String name;
	public int channeltype;  //频道分类
    public int listtype; // 表示横版海标还是竖版海报
	public ChannelRecommendationTab[] data;
	
	public Channel buildChannel(){
	    Channel channel = new Channel();
	    channel.id = id;
	    channel.name = name;
	    channel.channeltype = channeltype;
	    channel.name = name;
	    return channel;
	}
	
	public int getRecommendTabCount(){
	    if(data != null){
	        return data.length;
	    }
      return 0;
	}
	
	public List<String> getRecommendTabNames(){
	    List<String> names = new ArrayList<String>();
        if(data != null){
            for(ChannelRecommendationTab tab: data){
                names.add(tab.getTabName());
            }
        }
      return names;
    }
	
	public boolean isTvChannel(){
	    return Channel.isTvChannel(id);
	}
	
	public void completeData() {
		if(data != null && data.length > 0) {
		    for(int i = 0; i < data.length; i++){
		        if(data[i] != null){
		              data[i].completeData();
		        }
		    }
		}
	}
}
