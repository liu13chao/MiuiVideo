package com.miui.videoplayer.framework.views;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;

public class LevelNinePathDrawable extends Drawable {
	  private static final int MAX_LEVEL = 10000;
	  private static final int MIN_WIDTH = 10;
	
	  private NinePatchDrawable mDrawable;
 
	  public LevelNinePathDrawable(NinePatchDrawable d) {
	         if (d == null) {
	             throw new NullPointerException();
	         }
	         mDrawable = d;
	     }
	 
	    @Override
	     public void draw(Canvas canvas) {
	         final float scaleX = ((float)getLevel()) / MAX_LEVEL;
	         final Rect bounds = getBounds();
	         float width = bounds.width() * scaleX;
	         if (width > MIN_WIDTH) {
	             mDrawable.setBounds(bounds.left, bounds.top, (int)(bounds.left + width), bounds.bottom);
	             mDrawable.draw(canvas);
	         } else {
	             canvas.save();
	             canvas.scale(scaleX, 1f);
	             mDrawable.setBounds(getBounds());
	             mDrawable.draw(canvas);
	             canvas.restore();
	         }
	     }

		@Override
		public int getOpacity() {
			return 0;
		}

		@Override
		public void setAlpha(int alpha) {
		}

		@Override
		public void setColorFilter(ColorFilter cf) {
		}

}
