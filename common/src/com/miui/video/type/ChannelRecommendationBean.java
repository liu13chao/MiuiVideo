/**
 *  Copyright(c) 2014 XiaoMi TV Group
 *    
 *  ChannelRecommendationBean.java
 *
 *  @author tianli(tianli@xiaomi.com)
 *
 *  2014-11-3
 */
package com.miui.video.type;

import java.io.Serializable;

/**
 * @author tianli
 *
 */
public class ChannelRecommendationBean implements Serializable {

    
    private static final long serialVersionUID = 1L;
    
    // Common for OnlineMediaInfo
    public String shortdesc; // 副标题
    public String posterurl;  //海报url
    public String webpposterurl;  //海报webp url
    public String md5; //海报md5
    public int videoType; 
    public int mediaid;
    public String medianame;
    
    // Common for InfomationData and MediaInfo
    public int resolution; // 标记该电影的清晰度
    public int playlength;  //播放时长  second
    public int playcount;  //播放次数
    
    // Common for InfomationData and TV info
    public int source;
    public int channelid;

    // MediaInfo
    public int flag;
    public String category; // 分类
    public String director; // 导演
    public String actors; // 演员
    public String allcategorys; // 类型
    public float score = 0.0f;   //评分
    public int setnow;      //当前集数
    public String area;     //区域
    public String issuedate; // 上映时间
    public String lastissuedate; // 综艺最近更新时间
    public int setcount; // 总集数
    public int[] media_available_download_source;//针对单级的节目
    public int scorecount; //评分次数
    public int ismultset;   //单集 、多集
    public int setAvailableCount; //当前可用集数
    
    // TV info

    public char headletter = '#';  //电视台首字母, char
    public String videoidentifying;  //电台播放id
    public int epgid;                //佳视互动电台播放id
    public String cmccid;
    public int backgroundcolor;  //海报背景颜色
    public int hotindex;  
    public String channelname;   //和mediaName是同一个东西，和后台约定字段的时候重复了
    public TelevisionShow currentprogramme;  //电台当前节目
    
    // InfomationData
    public String playurl;
    public boolean sdkdisable;
    public String sdkinfo2;
    public int playType;

    public MediaInfo buildMediaInfo(){
        MediaInfo mediaInfo = new MediaInfo();
        mediaInfo.mediaid = mediaid;
        mediaInfo.shortdesc = shortdesc;
        mediaInfo.posterurl = posterurl;
        mediaInfo.webpposterurl = webpposterurl;
        mediaInfo.md5 = md5;
        mediaInfo.videoType = videoType;
        mediaInfo.medianame = medianame;
        mediaInfo.flag = flag;
        mediaInfo.resolution = resolution;
        mediaInfo.category = category;
        mediaInfo.director = director;
        mediaInfo.actors = actors;
        mediaInfo.allcategorys = allcategorys;
        mediaInfo.score = score;
        mediaInfo.setnow = setnow;
        mediaInfo.playlength = playlength;
        mediaInfo.area = area;
        mediaInfo.issuedate = issuedate;
        mediaInfo.lastissuedate = lastissuedate;
        mediaInfo.setcount = setcount;
        mediaInfo.media_available_download_source = media_available_download_source;
        mediaInfo.playcount = playcount;
        mediaInfo.scorecount = scorecount;
        mediaInfo.ismultset = ismultset;
        mediaInfo.setAvailableCount = setAvailableCount;
        return mediaInfo;
    }
    
    public TelevisionInfo buildTelevisionInfo(){
        TelevisionInfo televisionInfo = new TelevisionInfo();
        televisionInfo.shortdesc = shortdesc;
        televisionInfo.posterurl = posterurl;
        televisionInfo.webpposterurl = webpposterurl;
        televisionInfo.md5 = md5;
        televisionInfo.source = source;
        televisionInfo.headletter = headletter;
        televisionInfo.videoidentifying = videoidentifying;
        televisionInfo.epgid = epgid;
        televisionInfo.cmccid = cmccid;
        televisionInfo.backgroundcolor = backgroundcolor;
        televisionInfo.hotindex = hotindex;
        televisionInfo.mediaid = mediaid;
        televisionInfo.medianame = medianame;
        televisionInfo.channelid = channelid;
        televisionInfo.channelname = channelname;
        televisionInfo.currentprogramme = currentprogramme;
        return televisionInfo;
    }
    
    public InformationData buildInfomation(){
        InformationData information = new InformationData();
        information.playurl = playurl;
        information.playType = playType;
        information.sdkdisable = sdkdisable;
        information.sdkinfo2 = sdkinfo2;
        information.resolution = resolution;
        information.playlength = playlength;
        information.playcount = playcount;
        information.source = source;
        information.channelid = channelid;
        information.shortdesc = shortdesc;
        information.posterurl = posterurl;
        information.webpposterurl = webpposterurl;
        information.videoType = videoType;
        information.md5 = md5;
        information.mediaid = mediaid;
        information.medianame = medianame;
        return information;
    }
    
}
