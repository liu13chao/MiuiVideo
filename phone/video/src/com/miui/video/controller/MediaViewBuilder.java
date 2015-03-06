/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   MediaViewBuilder.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-11-13
 */
package com.miui.video.controller;

import android.view.View;
import android.view.ViewGroup;

/**
 * @author tianli
 *
 */
public abstract class MediaViewBuilder {
    
    public abstract View getView(MediaViewRowInfo rowInfo, View convertView, ViewGroup parent);
    
}
