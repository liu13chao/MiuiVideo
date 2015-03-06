/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   MediaViewClickListener.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-11-17
 */
package com.miui.video.controller;

import com.miui.video.type.BaseMediaInfo;

import android.view.View;

/**
 * @author tianli
 *
 */
public interface MediaViewClickListener {

    public void onMediaClick(View view, BaseMediaInfo media);
    public void onMediaLongClick(View view, BaseMediaInfo media);
    
}
