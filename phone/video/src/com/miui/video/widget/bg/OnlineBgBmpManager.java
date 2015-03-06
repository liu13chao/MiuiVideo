package com.miui.video.widget.bg;

import java.util.ArrayList;
import java.util.List;

import miui.graphics.BitmapFactory;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;

import com.miui.video.R;
import com.miui.video.model.AppSingleton;
import com.miui.video.model.DataStore;
import com.miui.video.type.Banner;
import com.miui.video.type.ImageUrlInfo;
import com.miui.video.util.BitmapUtil;
import com.miui.video.util.Util;

public class OnlineBgBmpManager extends AppSingleton {

	//synchronized
	private List<Bitmap> mBlurBitmaps = new ArrayList<Bitmap>();
	private List<Bitmap> mBitmaps = new ArrayList<Bitmap>();
	
	private List<Object> mBanners;
	private int mCurPosition;
	private int mRadius;
	
	private List<OnBitmapLoadListener> mListeners = new ArrayList<OnBitmapLoadListener>();
	
	private Handler mHandler = new Handler(Looper.getMainLooper());

	@Override
    public void init(Context context) {
        super.init(context);
        mRadius = (int) context.getResources().getDimension(R.dimen.video_common_blur_radius);
    }

    public void addListener(OnBitmapLoadListener listener) {
		if(listener != null && !mListeners.contains(listener)) {
			mListeners.add(listener);
		}
	}
	
	public void removeListener(OnBitmapLoadListener listener) {
		if(listener != null) {
			mListeners.remove(listener);
		}
	}
	
	public void setBanners(List<Object> banners) {
		this.mBanners = banners;
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				loadBitmap();
			}
		}).start();
	}
	
	public void setCurPosition(int position) {
		this.mCurPosition = position;
	}
	
	public Bitmap getCurBlurBitmap() {
		if(mCurPosition >= 0 && mCurPosition < mBlurBitmaps.size()) {
			return mBlurBitmaps.get(mCurPosition);
		}
		return null;
	}
	
	public Bitmap getLastBlurBitmap() {
		int blurBitmapSize = mBlurBitmaps.size();
		if(blurBitmapSize != 0) {
			int lastPosition = (mCurPosition - 1 + blurBitmapSize) % blurBitmapSize;
			if(lastPosition >= 0 && lastPosition < blurBitmapSize) {
				return mBlurBitmaps.get(lastPosition);
			}
		}
		return null;
	}
	
	//packaged method
	private synchronized void loadBitmap() {
		mBitmaps.clear();
		mBlurBitmaps.clear();
		if(mBanners == null) {
			return;
		}
		for(int i = 0; i < mBanners.size(); i++) {
			Object obj = mBanners.get(i);
			Bitmap bitmap = null;
			Bitmap blurBitmap = null;
			if(obj instanceof Banner) {
				Banner banner = (Banner) obj;
				ImageUrlInfo imageUrlInfo = banner.getPosterInfo();
				if(imageUrlInfo != null && !Util.isEmpty(imageUrlInfo.getImageUrl())) {
					bitmap = DataStore.getInstance().getImage(imageUrlInfo, true);
					if(bitmap != null && mRadius != 0) {
						bitmap = BitmapUtil.scaleImage(bitmap, 260, 130);
						bitmap = BitmapUtil.filterImage(bitmap);
						blurBitmap = BitmapFactory.fastBlur(bitmap, mRadius);
					}
				}
			}
			mBitmaps.add(bitmap);
			mBlurBitmaps.add(blurBitmap);
			
			if(i == 0) {
				mHandler.post(mNotifyBitmapDoneRunnable);
			}
		}
	}
	
	private Runnable mNotifyBitmapDoneRunnable = new Runnable() {
		
		@Override
		public void run() {
			for(int i = 0; i < mListeners.size(); i++) {
				OnBitmapLoadListener listener = mListeners.get(i);
				listener.onBitmapDone();
			}
		}
	};
	
	//self def class
	public interface OnBitmapLoadListener {
		public void onBitmapDone();
	}
}
