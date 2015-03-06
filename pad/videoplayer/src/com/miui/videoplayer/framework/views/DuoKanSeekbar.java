package com.miui.videoplayer.framework.views;



import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.NinePatchDrawable;
import android.util.AttributeSet;
import android.widget.SeekBar;

public class DuoKanSeekbar extends SeekBar {

	public DuoKanSeekbar(Context context) {
		super(context);
	}

	public DuoKanSeekbar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public DuoKanSeekbar(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public void setProgressDrawable(Drawable drawable) {

		if (drawable instanceof LayerDrawable) {
			boolean hasNinePatch = false;
			final LayerDrawable layers = (LayerDrawable) drawable;
			final int count = layers.getNumberOfLayers();
			final Drawable[] outDrawables = new Drawable[count];
			for (int i = 0; i < count; ++i) {
				int id = layers.getId(i);
				Drawable child = layers.getDrawable(i);
				if ((id == android.R.id.progress || id == android.R.id.secondaryProgress) && child instanceof NinePatchDrawable) {
					child = new LevelNinePathDrawable((NinePatchDrawable) child);
					hasNinePatch = true;
				}
				outDrawables[i] = child;
			}

			if (hasNinePatch) {
				LayerDrawable newLayers = new LayerDrawable(outDrawables);
				for (int i = 0; i < count; ++i) {
					newLayers.setId(i, layers.getId(i));
				}
				drawable = newLayers;
			}

		}
		super.setProgressDrawable(drawable);
	}

}
