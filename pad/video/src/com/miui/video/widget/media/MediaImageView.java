package com.miui.video.widget.media;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.miui.video.DKApp;
import com.miui.video.R;
import com.miui.video.model.ImageManager;
import com.miui.video.thumbnail.ThumbnailManager;
import com.miui.video.thumbnail.ThumbnailTaskInfo;
import com.miui.video.type.ImageUrlInfo;
import com.miui.video.util.MediaViewHelper;
import com.miui.video.widget.CornerImageView;

/**
 *@author tangfuling
 *
 */

public class MediaImageView extends LinearLayout{
	
	//UI
	private CornerImageView[] imageViews;
	private int mediaType;
	
	//managers
	private ImageManager imageManager;
	private ThumbnailManager thumbnailManager;
	
	//display model
	private static final int DISPLAY_MODEL_NORMAL = 1;
	private static final int DISPLAY_MODEL_DIR = 2;
	private int currentDisplayModel = -1;
	
	public MediaImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	public MediaImageView(Context context, int mediaType) {
		super(context);
		this.mediaType = mediaType;
		init();
	}
	
	public void setMediaType(int mediaType) {
		this.mediaType = mediaType;
		if (isDisplayModelChanged(mediaType)) {
			adjustMediaType();
		} else {
			refreshViews();
		}
	}
	
	public void setThumbnailTaskInfos(ThumbnailTaskInfo[] thumbnailTaskInfos) {
		if (thumbnailTaskInfos != null && imageViews != null) {
			int size = Math.min(thumbnailTaskInfos.length, imageViews.length);
			for (int i = 0; i < size; i++) {
				if (thumbnailTaskInfos[i] != null && imageViews[i] != null) {
					if (thumbnailManager.fetchThumbnail(thumbnailTaskInfos[i], imageViews[i], null, true) == false) {
						imageViews[i].setImageResource(R.drawable.transparent);
					}
				} else {
					imageViews[i].setImageResource(R.drawable.transparent);
				}
			}
		} else {
			refreshDefaultBg();
		}
	}
	
	public void setImageUrlInfo(ImageUrlInfo smallImageUrl) {
		if (smallImageUrl != null && imageViews != null) {
			if (imageViews[0] != null) {
				imageManager.fetchImage(smallImageUrl, imageViews[0]);
				if (!ImageManager.isUrlDone(smallImageUrl, imageViews[0])) {
					refreshDefaultBg();
				}
			}
		} else {
			refreshDefaultBg();
		}
	}
	
	private void init() {
		initUI();
		initManagers();
	}
	
	private void initUI() {
		setOrientation(VERTICAL);
		
		imageViews = new CornerImageView[2];
		for (int i = 0; i < imageViews.length; i++) {
			imageViews[i] = new CornerImageView(getContext());
			imageViews[i].setScaleType(ScaleType.FIT_XY);
		}
		
		refreshViews();
		refreshDefaultBg();
	}
	
	private void initManagers() {
		imageManager = ImageManager.getInstance();
		thumbnailManager = DKApp.getSingleton(ThumbnailManager.class);
	}
	
	//packaged method
	private void adjustMediaType() {
		refreshViews();
		refreshDefaultBg();
	}
	
	private void refreshViews() {
		removeAllViews();
		if (mediaType != MediaViewHelper.UI_LOCAL_DIR_TYPE) {
			currentDisplayModel = DISPLAY_MODEL_NORMAL;
			LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			imageViews[0].setLayoutParams(params);
			addView(imageViews[0]);
		} else {
			currentDisplayModel = DISPLAY_MODEL_DIR;
			Resources res = getResources();
			int contentWidth = res.getDimensionPixelSize(R.dimen.media_image_view_content_width);
			int contentHeight = res.getDimensionPixelSize(R.dimen.media_image_view_content_height);
			int contentTopMargin = res.getDimensionPixelSize(R.dimen.media_image_view_content_top_margin);
			int contentIntervalV = res.getDimensionPixelSize(R.dimen.media_image_view_content_intervalV);
			
			LayoutParams params0 = new LayoutParams(contentWidth, contentHeight);
			params0.gravity = Gravity.CENTER_HORIZONTAL;
			params0.topMargin = contentTopMargin;
			imageViews[0].setLayoutParams(params0);
			
			LayoutParams params1 = new LayoutParams(contentWidth, contentHeight);
			params1.gravity = Gravity.CENTER_HORIZONTAL;
			params1.topMargin = contentIntervalV;
			imageViews[1].setLayoutParams(params1);
			
			addView(imageViews[0]);
			addView(imageViews[1]);
		}
	}
	
	private void refreshDefaultBg() {
		if (mediaType == MediaViewHelper.UI_BANNER_TYPE) {
			imageViews[0].setImageResource(R.drawable.transparent);
		} else if (mediaType == MediaViewHelper.UI_COVER_TYPE
				|| mediaType == MediaViewHelper.UI_SMALL_COVER_TYPE) {
			imageViews[0].setImageResource(R.drawable.transparent);
		} else if (mediaType == MediaViewHelper.UI_LOCAL_NORMAL_TYPE 
				|| mediaType == MediaViewHelper.UI_LOCAL_PADDING_TYPE
				|| mediaType == MediaViewHelper.UI_LOCAL_COVER_TYPE) {
			imageViews[0].setImageResource(R.drawable.transparent);
		} else if (mediaType == MediaViewHelper.UI_LOCAL_DIR_TYPE) {
			for (int i = 0; i < imageViews.length; i++) {
				imageViews[i].setImageResource(R.drawable.transparent);
			}
		}
	}
	
	private boolean isDisplayModelChanged(int mediaType) {
		if (mediaType != MediaViewHelper.UI_LOCAL_DIR_TYPE && currentDisplayModel == DISPLAY_MODEL_DIR) {
			currentDisplayModel = DISPLAY_MODEL_NORMAL;
			return true;
		}
		if (mediaType == MediaViewHelper.UI_LOCAL_DIR_TYPE && currentDisplayModel == DISPLAY_MODEL_NORMAL) {
			currentDisplayModel = DISPLAY_MODEL_DIR;
			return true;
		}
		return false;
	}
}


