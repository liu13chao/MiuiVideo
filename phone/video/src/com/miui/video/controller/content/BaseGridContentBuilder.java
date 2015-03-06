/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   BaseGridContentBuilder.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-11-20
 */
package com.miui.video.controller.content;

import android.content.Context;

/**
 * @author tianli
 *
 */
public abstract class BaseGridContentBuilder extends MediaContentBuilder {

    public BaseGridContentBuilder(Context context) {
        super(context);
    }
    
    public abstract String getSubtitle();
    
    public abstract String getStatus();
    
    public abstract String getStatusExtra();
    
    public abstract boolean isStatusInCenter();
    
    public abstract boolean isSubtitleInCenter();

    @Override
    public boolean isSinglelineOK() {
        return true;
    }
    
}
