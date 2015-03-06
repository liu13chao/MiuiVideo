/**
 *  Copyright(c) 2014 XiaoMi TV Group
 *    
 *  ViewUtils.java
 *
 *  @author tianli(tianli@xiaomi.com)
 *
 *  2014-11-8
 */
package com.miui.video.util;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

import com.miui.video.DKApp;
import com.miui.video.controller.CoverBitmapCache;

/**
 * @author tianli
 *
 */
public class ViewUtils {
    
    public static void showView(View view){
        if(view != null){
            if(view.getVisibility() != View.VISIBLE){
                view.setVisibility(View.VISIBLE);
            }
        }
    }
    
    public static void hideView(View view){
        if(view != null){
            if(view.getVisibility() == View.VISIBLE){
                view.setVisibility(View.INVISIBLE);
            }
        }
    }
    
    public static Rect getViewBoundRect(View view){
        int[] location      = new int[2];
        view.getLocationOnScreen(location);
        Rect rect = new Rect(location[0], location[1], location[0] + view.getWidth(), location[1] 
                + view.getHeight());
        return rect;
    }
    
    public static int getViewHeight(View view){
        if(view != null){
            int height = view.getMeasuredHeight();
            if(height == 0){
                LayoutParams params = view.getLayoutParams();
                if(params != null && params.height > 0){
                    return params.height;
                }
                view.measure(0, 0);
                height = view.getMeasuredHeight();
            }
            return height;
        }
        return 0;
    }
    
    public static int getViewWidth(View view){
        if(view != null){
            int width = view.getMeasuredWidth();
            if(width == 0){
                LayoutParams params = view.getLayoutParams();
                if(params != null && params.width > 0){
                    return params.width;
                }
                view.measure(0, 0);
                width = view.getMeasuredWidth();
            }
            return width;
        }
        return 0;
    }
    
    @SuppressWarnings("deprecation")
    public static void setPoster(View view, int res){
        if(view == null){
            return;
        }
        int width = ViewUtils.getViewWidth(view);
        int height = ViewUtils.getViewHeight(view);
        if(width > 0 && height > 0){
            Bitmap b = DKApp.getSingleton(CoverBitmapCache.class).
                    getDefaultCover(width, height, res);
            view.setBackgroundDrawable(new BitmapDrawable(b));
        }else{
            view.setBackgroundResource(res);
        }
    }
    
    public static void setImagePoster(ImageView view, int res){
        if(view == null){
            return;
        }
        int width = ViewUtils.getViewWidth(view);
        int height = ViewUtils.getViewHeight(view);
        if(width > 0 && height > 0){
            Bitmap b = DKApp.getSingleton(CoverBitmapCache.class).
                    getDefaultCover(width, height, res);
            view.setImageBitmap(b);
        }else{
            view.setImageResource(res);
        }
    }
    
}
