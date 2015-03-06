/**
 *  Copyright(C) 2013 XiaoMi TV Group
 * 
 *  BitmapHelper.java  
 * 
 *  @author tianli (tianli@xiaomi.com)
 *
 *  @date 2013-11-26
 */
package com.miui.video.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;

import com.miui.video.R;
import com.xiaomi.common.util.BitmapHelper;

/**
 * @author tianli
 *
 */
public class BitmapUtil {

	public static Bitmap clipRoundCorner(Context context, Bitmap bm) {
        if (bm != null) {
            final Resources res = context.getResources();
            final int radius = res.getDimensionPixelSize(R.dimen.poster_corner_radius);
            final int color = res.getColor(R.color.poster_border);
            final Bitmap cliped = BitmapHelper.clipRoundCornerBitmap(bm, radius, color);
        //  bm.recycle();
            bm = cliped;
        }
        return bm;
    }
	
	public static Bitmap scaleImage(Bitmap bitmap, int destW, int destH) {
		if(bitmap == null) {
			return null;
		}
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        float scaleWidth = ((float) destW) / width;
        float scaleHeight = ((float) destH) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, width,
                        height, matrix, true);
        return scaledBitmap;
	}
	
    public static Bitmap filterImage(Bitmap src) {
        if (src == null) {
            return null;
        }

        final float minV = 0.7f;
        final float maxV = 0.85f;

        float[] hsvVals = getMainHSV(src);
        float deltaV = 0;
        if (hsvVals[2] < minV) {
            deltaV = minV - hsvVals[2];
        } else if (hsvVals[2] > maxV) {
            deltaV = maxV - hsvVals[2];
            if (hsvVals[1] < 0.1f) {
                deltaV *= 2;
            }
        }
        if (Math.abs(deltaV) < 0.0001f) {
            return src;
        }

        int w = src.getWidth();
        int h = src.getHeight();
        int[] pixels = new int[w * h];
        src.getPixels(pixels, 0, w, 0, 0, w, h);
        float[] HSV = new float[3];
        int index = 0;
        for(int y = 0; y < h; ++y) {
            for(int x = 0; x < w; ++x) {
                Color.colorToHSV(pixels[index], HSV);
                HSV[2] = clamp(HSV[2] + deltaV, 0f, 1f);
                pixels[index] = perferBlue(Color.HSVToColor(HSV));
                ++index;
            }
        }
        return Bitmap.createBitmap(pixels, w, h, Bitmap.Config.ARGB_8888);
    }
	
	private static float[] getMainHSV(Bitmap bm) {
        Bitmap onePixelBitmap = Bitmap.createScaledBitmap(bm, 1, 1, true);
        int pixel = onePixelBitmap.getPixel(0,0);

        int red = Color.red(pixel);
        int blue = Color.blue(pixel);
        int green = Color.green(pixel);

        float[] hsvVals = new float[3];
        Color.RGBToHSV(red, green, blue, hsvVals);
        return hsvVals;
    }

    private static int perferBlue(int color) {
        final int delta = 20;
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        if (b > 255 - delta) {
            r -= delta;
            r = (int) clamp(r, 0, 255);
            g -= delta;
            g = (int) clamp(g, 0, 255);
        } else {
            b += delta;
        }

        return Color.rgb(r, g, b);
    }
    
    private static float clamp(float x, float min, float max) {
        if (x > max) return max;
        if (x < min) return min;
        return x;
    }
}
