/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   MediaPagerFooterBuilder.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-11-14
 */
package com.miui.video.controller;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.miui.video.R;

/**
 * @author tianli
 *
 */
public class MediaPagerFooterBuilder extends MediaViewBuilder {

    static MediaPagerFooterBuilder sInstance = new MediaPagerFooterBuilder();
    
    public MediaPagerFooterBuilder(){
    }
    
    public static MediaPagerFooterBuilder getBuilder(){
        return sInstance;
    }
    
    @Override
    public View getView(MediaViewRowInfo rowInfo, final View convertView,
            final ViewGroup parent) {
        if(convertView != null){
            return convertView;
        }
        return  LayoutInflater.from(parent.getContext()).inflate(R.layout.media_pager_footer, parent, false);
    }

}
