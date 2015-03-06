/**
 *  Copyright(c) 2014 XiaoMi TV Group
 *    
 *  CornerUpBitmapFilter.java
 *
 *  @author tianli(tianli@xiaomi.com)
 *
 *  2014-11-3
 */
package com.miui.video.controller;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * @author tianli
 *
 */
public class CornerUpBitmapFilter implements BitmapFilter {

    private int mRadius;
    
    public CornerUpBitmapFilter(int radius){
        mRadius = radius;
    }
    
    @Override
    public Bitmap filter(Bitmap bitmap) {
        if(bitmap != null && mRadius > 0){
            Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);  
            Canvas canvas = new Canvas(output);  
            final Paint paint = new Paint();  
            final Rect rect1 = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());  
            final RectF rectF1 = new RectF(rect1);
            final Rect rect2 = new Rect(0, mRadius, bitmap.getWidth(), bitmap.getHeight());
            paint.setAntiAlias(true);  
            canvas.drawARGB(0, 0, 0, 0);  
            canvas.drawRoundRect(rectF1, mRadius, mRadius, paint);
            canvas.drawRect(rect2, paint);
            paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));  
            canvas.drawBitmap(bitmap, rect1, rect1, paint);
            return output;  
        }else{
            return null;
        }
    }

}
