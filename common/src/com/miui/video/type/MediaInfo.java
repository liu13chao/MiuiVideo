/**
 *   Copyright(c) 2012 DuoKan TV Group
 *    
 *   MediaInfo.java
 *
 *   @author tianli(tianli@duokan.com)
 *
 *   2012-8-11 
 */
package com.miui.video.type;

import android.content.Context;

import com.miui.video.DKApp;
import com.miui.video.R;
import com.miui.video.controller.MediaConfig;
import com.miui.video.controller.MediaViewHelper;
import com.miui.video.util.Util;
import com.xiaomi.mitv.common.json.JsonSerializer;

/**
 * @author tianli
 * 
 */
public class MediaInfo extends OnlineMediaInfo {

    private static final long serialVersionUID = 3L;

    public int style;
    
    public int flag;
    public int resolution; // 标记该电影的清晰度
    public String category; // 分类
    public String director; // 导演
    public String actors; // 演员
    public String allcategorys; // 类型
    public float score = 0.0f;   //评分
    public int setnow;      //当前集数
    public int playlength;  //播放时长  second
    public String area;     //区域
    public String issuedate; // 上映时间
    public String lastissuedate; // 综艺最近更新时间
    public int setcount; // 总集数
    public int[] media_available_download_source;//针对单级的节目
    public int playcount;  //播放次数
    public int scorecount; //评分次数
    public int ismultset;   //单集 、多集
    public int setAvailableCount; //当前可用集数

    public MediaInfo() {
        mediaid = -1;
        flag = 0;
        resolution = 0;
        category = "";
        medianame = "";
        director = "";
        actors = "";
        allcategorys = "";
        score = 0.0f;
        setnow = 0;
        playlength = 0;
        area = "";
        issuedate = "";
        lastissuedate = "";
        setcount = 0;
        playcount = 0;
        ismultset = 0;
        style = 0;
        videoType = MediaConfig.MEDIA_TYPE_LONG;
    }

    public boolean isMultiSetType() {
        if (ismultset > 0)
            return true;
        return false;
    }

    public boolean isFinished() {
        if (ismultset > 0) {
            if (setnow == setcount)
                return true;
            else
                return false;
        } else {
            return true;
        }
    }

    @Override
    public String getDesc() {
        return MediaViewHelper.getMediaStatus(this);
    }

    @Override
    public String getDescSouth() {
        Context context = DKApp.getAppContext();
        if(context != null){
            return context.getString(R.string.score_by, Util.formatFloat(score));
        }
        return "";
    }

    @Override
    public String getMediaStatus() {
        return MediaViewHelper.getMediaStatus(this);
    }
    
    public String getScroeStatus() {
        Context context = DKApp.getAppContext();
        if(context != null){
            return context.getString(R.string.score_by, Util.formatFloat(score));
        }
        return "";
    }
    
    public static MediaInfo parseFromJson(String json){
        try{
            return JsonSerializer.getInstance().deserialize(json, MediaInfo.class);
        }catch(Throwable t){
        }
        return null;
    }
    
    public String toJson(){
           try{
                return JsonSerializer.getInstance().serialize(this);
            }catch(Throwable t){
            }
            return null;
    }
}
