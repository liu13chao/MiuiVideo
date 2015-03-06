/**
 *  Copyright(c) 2014 XiaoMi TV Group
 *    
 *  MoreBtnAction.java
 *
 *  @author tianli(tianli@xiaomi.com)
 *
 *  2014-11-12
 */
package com.miui.video.controller.action;

import android.app.Activity;
import android.content.Intent;

import com.miui.video.ChannelActivity;
import com.miui.video.info.InfoChannelActivity;
import com.miui.video.live.TvChannelActivity;
import com.miui.video.type.Channel;
import com.miui.video.type.ChannelRecommendation;

/**
 * @author tianli
 *
 */
public class MoreBtnAction extends LauncherAction{
    
    public ChannelRecommendation mRecommendation;
    
    public MoreBtnAction(Activity activity, ChannelRecommendation recommendation) {
        super(activity);
      this.mRecommendation = recommendation;
    }

    @Override
    public Intent getIntent() {
        if(mRecommendation != null){
            if(Channel.isInformationType(mRecommendation.channeltype)){
                Intent intent = new Intent(mActivity, InfoChannelActivity.class);
                intent.putExtra(InfoChannelActivity.KEY_CHANNEL, mRecommendation.buildChannel());
                return intent;
            }else if(Channel.isTvChannel(mRecommendation.id)){
                Intent intent = new Intent(mActivity, TvChannelActivity.class);
                return intent;
            }else{
                Intent intent = new Intent(mActivity, ChannelActivity.class);
                intent.putExtra(ChannelActivity.KEY_CHANNEL, mRecommendation.buildChannel());
                return intent;
            }
        }
        return null;
    }
    
}
