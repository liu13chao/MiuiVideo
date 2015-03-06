/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   InfoEpisode.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-12-2
 */
package com.miui.video.info;

import com.miui.video.type.InformationData;
import com.miui.videoplayer.model.Episode;

/**
 * @author tianli
 *
 */
public class InfoEpisode extends Episode {
    
    private InformationData mInfoData;

    public InformationData getInfoData() {
        return mInfoData;
    }

    public void setInfoData(InformationData mInfoData) {
        this.mInfoData = mInfoData;
    }
    
    
    
}
