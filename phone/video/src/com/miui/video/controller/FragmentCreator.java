/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   FragmentCreator.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-11-6
 */
package com.miui.video.controller;

import android.os.Bundle;

import com.miui.video.info.InfoChannelFragment;
import com.miui.video.info.InfoChannelRecommendFragment;
import com.miui.video.info.InfoConstants;
import com.miui.video.type.Channel;
import com.miui.video.type.ChannelRecommendation;

/**
 * @author tianli
 *
 */
public class FragmentCreator {

    public static InfoChannelFragment createInfoChannelFragment(Channel rootChannel, 
            Channel channel){
        InfoChannelFragment fragment = new InfoChannelFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(InfoConstants.KEY_CHANNEL, channel);
        bundle.putSerializable(InfoConstants.KEY_ROOT_CHANNEL, rootChannel);
        fragment.setArguments(bundle);
        return fragment;
    }
    
    public static InfoChannelRecommendFragment createInfoRecommendFragment(Channel channel, 
            ChannelRecommendation[] recommends){
        InfoChannelRecommendFragment fragment = new InfoChannelRecommendFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(InfoConstants.KEY_CHANNEL, channel);
        bundle.putSerializable(InfoConstants.KEY_CHANNEL_RECOMMEND_LIST, 
                recommends);
        fragment.setArguments(bundle);
        return fragment;
    }
    
}
