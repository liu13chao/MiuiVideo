package com.miui.video.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 *@author tangfuling
 *
 */

public class CornerImageView extends ImageView {
	
	private int mRadius;
	
	public CornerImageView(Context context) {
		super(context);
	}
	
	public CornerImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public void setRadius(int radius) {
		this.mRadius = radius;
	}
	
	@Override
	public void setImageBitmap(Bitmap bm) {
		super.setImageBitmap(toRoundCorner(bm));
	}
	
	private Bitmap toRoundCorner(Bitmap bitmap) {  
		if(bitmap == null) {
			return null;
		}
		if(mRadius == 0) {
			return bitmap;
		}

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);  
        Canvas canvas = new Canvas(output);  
        final Paint paint = new Paint();  
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());  
        final RectF rectF = new RectF(rect);  
        paint.setAntiAlias(true);  
        canvas.drawARGB(0, 0, 0, 0);  
        canvas.drawRoundRect(rectF, mRadius, mRadius, paint);  
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));  
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;  
    }
}
