/**
 *  Copyright(c) 2014 XiaoMi TV Group
 *    
 *  ChannelRecommendationTab.java
 *
 *  @author tianli(tianli@xiaomi.com)
 *
 *  2014-11-3
 */
package com.miui.video.type;

import java.io.Serializable;

import com.miui.video.statistic.VideoTypeTagDef;

/**
 * @author tianli
 *
 */
public class ChannelRecommendationTab implements Serializable {

    private static final long serialVersionUID = 1L;
    
    public int id;
    public String tabname;
    public int count; // 该频道总共的电影数量
    public int midtype; // 影片0，直播200
    
    public ChannelRecommendationBean[]  medialist;
    
    public MediaInfo[] mediaInfoList;
    public TelevisionInfo[] televisionInfoList;
    public InformationData[] informationDataList;
    
    public String getTabName(){
        if(tabname == null){
            return "";
        }
        return tabname;
    }

    public BaseMediaInfo[] getRecommendMedias() {
        if(midtype == VideoTypeTagDef.VIDEO_MEDIA) {
            return mediaInfoList;
        } else if(midtype == VideoTypeTagDef.VIDEO_TELEVISION) {
            return televisionInfoList;
        } else if(midtype == VideoTypeTagDef.VIDEO_INFORMATION) {
            return informationDataList;
        }
        return null;
    }

    public int getRecommendCount(){
        BaseMediaInfo[] list = getRecommendMedias();
        if(list != null){
            return list.length;
        }
        return 0;
    }
    
    public void completeData() {
        if(medialist != null && medialist.length > 0) {
            if(midtype == VideoTypeTagDef.VIDEO_MEDIA) {
                mediaInfoList = new MediaInfo[medialist.length];
                for(int i = 0; i < medialist.length; i++){
                    mediaInfoList[i] = medialist[i].buildMediaInfo();
                }
            } else if(midtype == VideoTypeTagDef.VIDEO_TELEVISION) {
                televisionInfoList = new TelevisionInfo[medialist.length];
                for(int i = 0; i < medialist.length; i++){
                    televisionInfoList[i] = medialist[i].buildTelevisionInfo();
                }
            } else if(midtype == VideoTypeTagDef.VIDEO_INFORMATION) {
                informationDataList = new InformationData[medialist.length];
                for(int i = 0; i < medialist.length; i++){
                    informationDataList[i] = medialist[i].buildInfomation();
                }
            }
        }
    }

}
