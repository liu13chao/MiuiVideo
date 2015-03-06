/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   MediaViewRowInfo.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-11-13
 */
package com.miui.video.controller;

import java.util.ArrayList;

import com.miui.video.type.BaseMediaInfo;

/**
 * @author tianli
 *
 */
public class MediaViewRowInfo {

    public int mRowIndex;
    public int mViewType;
    public ArrayList<BaseMediaInfo> mMediaList = new ArrayList<BaseMediaInfo>();
    public BaseMediaRowBuilder mRowBuilder;
}
