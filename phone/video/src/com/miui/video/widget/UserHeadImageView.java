package com.miui.video.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

public class UserHeadImageView extends ImageView {
	
	public UserHeadImageView(Context context) {
		super(context);
	}
	
	public UserHeadImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	public void setImageBitmap(Bitmap bm) {
		super.setImageBitmap(toRoundCorner(bm));
	}
	
	private Bitmap toRoundCorner(Bitmap bitmap) {  
		if(bitmap == null) {
			return null;
		}

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);  
        Canvas canvas = new Canvas(output);  
        final Paint paint = new Paint();  
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());  
        final RectF rectF = new RectF(rect);  
        paint.setAntiAlias(true);  
        canvas.drawARGB(0, 0, 0, 0); 
        int radius = bitmap.getWidth() / 2;
        canvas.drawRoundRect(rectF, radius, radius, paint);  
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));  
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;  
    }
}
