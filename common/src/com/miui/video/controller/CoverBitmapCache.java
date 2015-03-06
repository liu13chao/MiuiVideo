/**
 *  Copyright(c) 2014 XiaoMi TV Group
 *    
 *  CoverBItmapCache.java
 *
 *  @author tianli(tianli@xiaomi.com)
 *
 *  2014-11-8
 */
package com.miui.video.controller;

import java.util.Hashtable;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import com.miui.video.model.AppSingleton;

/**
 * @author tianli
 *
 */
public class CoverBitmapCache extends AppSingleton{
    
    private Hashtable<String, Bitmap> mBitmapCache = new Hashtable<String, Bitmap>();
    
    public String getKey(int width, int height, int res){
        return width + ", " + height + "," + res ;
    }
    
    public Bitmap getDefaultCover(int width, int height, int res){
        String key = getKey(width, height, res);
        Bitmap bitmap = mBitmapCache.get(key);
        if(bitmap != null){
            return bitmap;
        }
        bitmap = getBitmap(res, width, height);
        if(bitmap != null){
            mBitmapCache.put(key, bitmap);
        }
        return bitmap;
    }
    
    private Bitmap getBitmap(int resId, int width, int height){
        Drawable d = mContext.getResources().getDrawable(resId);
        Bitmap bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        d.setBounds(0, 0, width, height);
        d.draw(canvas);
        return bitmap;
    }
}
