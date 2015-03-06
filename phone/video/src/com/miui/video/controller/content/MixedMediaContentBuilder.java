/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   MixedMediaContentBuilder.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-11-19
 */
package com.miui.video.controller.content;

import android.content.Context;

/**
 * @author tianli
 *
 */
public abstract class MixedMediaContentBuilder extends BaseGridContentBuilder {
    
    public MixedMediaContentBuilder(Context context) {
        super(context);
    }
    
    public abstract boolean isHorizontalPoster();

    public boolean isNameInCenter() {
        return true;
    }
    
}
