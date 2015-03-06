/**
 *  Copyright(c) 2014 XiaoMi TV Group
 *    
 *  CoverBitmapFilter.java
 *
 *  @author tianli(tianli@xiaomi.com)
 *
 *  2014-11-9
 */
package com.miui.video.controller;

import android.graphics.Bitmap;

/**
 * @author tianli
 *
 */
public class CoverBitmapFilter implements BitmapFilter {

    private int mWidth, mHeight;
    
    private BitmapFilter mFilter;
    
    public CoverBitmapFilter(int width, int height ){
        mWidth = width;
        mHeight = height;
    }
    
    public CoverBitmapFilter(int width, int height, BitmapFilter filter){
        mWidth = width;
        mHeight = height;
        mFilter = filter;
    }
    
    @Override
    public Bitmap filter(Bitmap bitmap) {
        Bitmap output = bitmap;
        if(bitmap != null && mWidth != 0 && mHeight != 0){
            if(bitmap.getWidth() != mWidth || bitmap.getHeight() != mHeight){
                output =  Bitmap.createScaledBitmap(bitmap, mWidth, mHeight, true);
            }
        }
        if(mFilter != null && output != null){
            output = mFilter.filter(output);
        }
        return output;
    }

}
