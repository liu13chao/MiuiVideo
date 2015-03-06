package com.miui.video.widget;

import miui.graphics.BitmapFactory;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.miui.video.R;

public class BlurImageView extends ImageView {

	private Context mContext;
	private int mRadius;
	private Bitmap mSrcBitmap;
	private Bitmap mBlurBitmap;
	
	public BlurImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		initDimen();
	}

	public BlurImageView(Context context) {
		super(context);
		this.mContext = context;
		initDimen();
	}
	
	public void blurBitmap(Bitmap bitmap) {
		if(mSrcBitmap != bitmap) {
			this.mSrcBitmap = bitmap;
			new AsyncGetBlurImageTask().execute();
		}
	}
	
	//init
	private void initDimen() {
		mRadius = (int) mContext.getResources().getDimension(R.dimen.video_common_blur_radius);
	}
	
	//packaged method
	private void setBlurImageBitmap(Bitmap blurBmp) {
		this.mBlurBitmap = blurBmp;
		//save src bitmap
		Bitmap tmpBmp = mSrcBitmap;
		setImageBitmap(blurBmp);
		//restore src bitmap
		mSrcBitmap = tmpBmp;        
	}
	
	//background task
	private class AsyncGetBlurImageTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			if(mSrcBitmap != null && mRadius != 0) {
				mBlurBitmap = BitmapFactory.fastBlur(mSrcBitmap, mRadius);
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			setBlurImageBitmap(mBlurBitmap);
		}
	}
}
