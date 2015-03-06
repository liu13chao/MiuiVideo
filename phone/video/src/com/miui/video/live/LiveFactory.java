/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   LiveFactory.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014年9月22日
 */
package com.miui.video.live;

import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;

import com.miui.video.type.TelevisionInfo;
import com.miui.videoplayer.videoview.IVideoView;

/**
 * @author tianli
 *
 */
public class LiveFactory {
    
    public static IVideoView createLiveVideoView(Context context, int source){
        if(source == 0){
            return new JiashiVideoView(context);
        }else if(source == 1){
            return new CmccVideoView(context);
        }
        return null;
    }
    
    public static String createLivePlayInfo(Context context, TelevisionInfo tvInfo){
        if(tvInfo.source == 0){
            return "" + tvInfo.epgid;
        }else if(tvInfo.source == 1){
            try{
                JSONObject json = new JSONObject();
                json.put("cmccid", tvInfo.cmccid);
                if(tvInfo.currentprogramme != null && !TextUtils.isEmpty(tvInfo.currentprogramme.cmccplayinfo)){
                    json.put("cmccplayinfo",  tvInfo.currentprogramme.cmccplayinfo);
                }
                return json.toString();
            }catch(Exception e){
            }
        }
        return "";
    }
    
}
