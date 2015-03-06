package com.miui.video.info;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.miui.video.controller.MediaConfig;
import com.miui.video.controller.PlaySession;
import com.miui.video.type.InformationData;
import com.miui.video.util.StringUtils;
import com.miui.video.util.Util;
import com.miui.videoplayer.model.BaseUri;
import com.miui.videoplayer.model.OnlineUri;

public class InfoPlayUtil {

	public static void playInformation(Activity context, InformationData informationData, String sourcePath) {
		if(informationData == null || context == null) {
			return;
		}
        if(informationData.channelid == 0){
            new PlaySession(context).startPlayerInfomation(informationData);
        }else if(!Util.playBySdk(informationData.sdkinfo2, informationData.sdkdisable, 
                informationData.source, MediaConfig.MEDIA_TYPE_SHORT) &&
                informationData.playType == MediaConfig.PLAY_TYPE_HTML5){
            new PlaySession(context).startPlayerInfomation(informationData);  
        }else{
            Intent intent = new Intent(context, InfoChannelPlayActivity.class);
            intent.putExtra(InfoChannelPlayActivity.KEY_INFODATA, informationData);
            context.startActivity(intent);
        }
	}
	
	public static BaseUri buildUri(InformationData infoData, int position, String playUrl){
	    if(infoData != null){
	        OnlineUri uri = new OnlineUri(infoData.mediaid, position, infoData.playurl,
	                infoData.medianame, infoData.source, infoData.resolution, 
	                infoData.sdkinfo2, infoData.sdkdisable, Uri.parse(StringUtils.avoidNull(playUrl)),  
	                MediaConfig.MEDIA_TYPE_SHORT);
	        return uri;
	    }
	    return null;
	}
}
