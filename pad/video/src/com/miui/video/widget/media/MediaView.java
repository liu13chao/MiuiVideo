package com.miui.video.widget.media;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.miui.video.R;
import com.miui.video.api.ApiConfig;
import com.miui.video.local.Favorite;
import com.miui.video.local.PlayHistory;
import com.miui.video.offline.OfflineMedia;
import com.miui.video.offline.OfflineMediaList;
import com.miui.video.storage.BaseDevice;
import com.miui.video.storage.MediaItem;
import com.miui.video.thumbnail.ThumbnailTaskInfo;
import com.miui.video.type.ImageUrlInfo;
import com.miui.video.type.LocalMedia;
import com.miui.video.type.LocalMediaList;
import com.miui.video.type.MediaInfo;
import com.miui.video.type.SpecialSubject;
import com.miui.video.type.TelevisionInfo;
import com.miui.video.type.TelevisionShow;
import com.miui.video.util.DKLog;
import com.miui.video.util.MediaViewHelper;
import com.miui.video.util.Util;

/**
 *@author tangfuling
 *
 */

public class MediaView extends LinearLayout {
	
	private final static String TAG = MediaView.class.getName();
	
	//UI显示内容
	private Object srcContentInfo;
	private Object contentInfo;
	private Context context;
	
	private RelativeLayout mRelativeLayout;
	private ImageView borderImage;
	private MediaImageView posterImage;
	private ImageView maskImage;
	private View checkStatusImage;
	
	private View infoView;
	private TextView nameView;
	private TextView statusView;
	private View clickView;
	
	private int posterW = 0;
	private int posterH = 0;
	
	private int mediaType =  MediaViewHelper.UI_COVER_TYPE;
	private OnMediaClickListener onMediaClickListener;
	private OnMediaLongClickListener onMediaLongClickListener;
	
	private boolean showText = true;
	private boolean showMask = true;
	
	//单集，多集连载中、多集已完结   0 1 2
	private int mediaSetType;
	
	//flags
	private boolean inEditMode = false;
	private boolean isSelected = false;
	
	public MediaView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.MediaView, defStyle, 0);
		if (a != null) {
			int n = a.getIndexCount();
			for (int i = 0; i < n; i++) {
				int attr = a.getIndex(i);
				switch (attr) {
					case R.styleable.MediaView_mediaType: {
						mediaType = a.getInt(attr, mediaType);
						break;
					}
					case R.styleable.MediaView_showText: {
						showText = a.getBoolean(attr, showText);
						break;
					}
					case R.styleable.MediaView_showMask: {
						showMask = a.getBoolean(attr, showMask);
						break;
					}
				}
			}
		}
		a.recycle();
		init();
	}
	
	public MediaView(Context context, AttributeSet attrs) {
		this(context, attrs, R.attr.mediaViewStyle);
		init();
	}
	
	public MediaView(Context context, int mediaType) {
		super(context);
		this.mediaType = mediaType;
		init();
	}
	
	public MediaView(Context context, int mediaType, Object object) {
		this(context, mediaType, object, false, false);
	}
	
	public MediaView(Context context, int mediaType, Object object, boolean isSelected, boolean isEditMode) {
		super(context);
		this.mediaType = mediaType;
		this.srcContentInfo = object;
		this.isSelected = isSelected;
		this.inEditMode = isEditMode;
		init();
	}
	
	public void setInEditMode(boolean inEditMode) {
		this.inEditMode = inEditMode;
		refreshSelectStatusImage();
	}
	
	public void setIsSelected(boolean isSelected) {
		this.isSelected = isSelected;
		DKLog.d(TAG, "setIsSelected: " + isSelected);
		refreshSelectStatusImage();
	}
	
	public boolean isSelected() {
		return this.isSelected;
	}
	
	public boolean isBanner() {
		return mediaType == MediaViewHelper.UI_BANNER_TYPE;
	}
	
//	public boolean isTv() {
//		return mediaType == MediaViewType.UI_TV_TYPE;
//	}
	
//	public boolean isLocal() {
//		return mediaType == MediaViewType.UI_LOCAL_NORMAL_TYPE || mediaType == MediaViewType.UI_LOCAL_PADDING_TYPE
//				|| mediaType == MediaViewType.UI_LOCAL_DIR_TYPE || mediaType == MediaViewType.UI_LOCAL_COVER_TYPE;
//	}

	public void setClickable(boolean clickable) {
		clickView.setClickable(clickable);
	}
	
	public void setOnMediaLongClickListener(
			OnMediaLongClickListener onMediaLongClickListener) {
		this.onMediaLongClickListener = onMediaLongClickListener;
	}
	
	public OnMediaClickListener getOnMediaClickListener() {
		return onMediaClickListener;
	}

	public void setOnMediaClickListener(
			OnMediaClickListener onMediaClickListener) {
		this.onMediaClickListener = onMediaClickListener;
	}
	
	public void setContentInfo(Object object) {
		setContentInfo(object, false, inEditMode);
	}
	
	public void setContentInfo(Object object, boolean isSelected, boolean isEditMode) {
		this.srcContentInfo = object;
		this.isSelected = isSelected;
		this.inEditMode = isEditMode;
		buildContentInfo();
		if (contentInfo == null) {
			setVisibility(View.INVISIBLE);
		} else {
			setVisibility(View.VISIBLE);
			refreshContent();
		}
	}
	
	public Object getContentInfo() {
		return srcContentInfo;
	}
	
	public void setMediaType(int mediaType) {
		if (this.mediaType != mediaType) {
			this.mediaType = mediaType;
			adjustMediaType();
		}
	}
	
	public int  getMediaSetType() {
		return mediaSetType;
	}
	
	public boolean isShowText() {
		return showText;
	}

	public void setShowText(boolean showText) {
		this.showText = showText;
		if ( infoView != null) {
			infoView.setVisibility(showText ? View.VISIBLE : View.GONE);
		}
	}

	public boolean isShowMask() {
		return showMask;
	}

	public void setShowMask(boolean showMask) {
		this.showMask = showMask;
		maskImage.setVisibility(showMask ? View.VISIBLE : View.GONE);
	}
	
	public void setInfoViewColor(int nameViewColor, int statusViewColor) {
		nameView.setTextColor(nameViewColor);
		statusView.setTextColor(statusViewColor);
	}
	
	public void setMediaViewSize(int width, int height) {
		adjustMediaType(width, height);
	}

	//init
	private void init() {
        initUI();
	}
	
	private void initUI() {
		DKLog.d(TAG, "initUI");
		context = getContext();
		setOrientation(VERTICAL);
		removeAllViews();
		
		refreshPosterSize();
		initBorderImage();
		initMaskImage();
		initClickView();
		initPosterImage();
		initStatusImage();
		initFrameLayout();
		
		refreshBorderBg();
		initInfoView();
		addView(mRelativeLayout);
		addView(infoView);
		
		setContentInfo(srcContentInfo, this.isSelected, this.inEditMode);
	}
	
	private void initBorderImage() {
		borderImage = new ImageView(context);
		RelativeLayout.LayoutParams borderParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		borderParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		borderParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		borderImage.setLayoutParams(borderParams);
		borderImage.setScaleType(ScaleType.FIT_XY);
	}
	
	private void initMaskImage() {
		maskImage = new ImageView(context);
		RelativeLayout.LayoutParams maskParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		maskParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		maskImage.setScaleType(ScaleType.FIT_XY);
		maskImage.setLayoutParams(maskParams);
		maskImage.setBackgroundResource(R.drawable.media_poster_frame);
	}
	
	private void initClickView() {
		clickView = new View(context);
		clickView.setClickable(true);
		RelativeLayout.LayoutParams clickParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		clickView.setLayoutParams(clickParams);
		clickView.setOnClickListener(mOnClickListener);
		clickView.setOnLongClickListener(mOnLongClickListener);
		clickView.setBackgroundResource(R.drawable.media_click_bg);
	}
	
	private void initPosterImage() {
		posterImage = new MediaImageView(context, mediaType);
		refreshPosterParams();
	}
	
	public void refresh() {
		adjustMediaType();
	}
	
	private void initStatusImage() {
		DKLog.d(TAG, "initStatusImage");
		checkStatusImage = new View(context);
		checkStatusImage.setBackgroundResource(R.drawable.media_view_status);
		int width = context.getResources().getDimensionPixelSize(R.dimen.media_status_width);
		int height = context.getResources().getDimensionPixelSize(R.dimen.media_status_height);
		int rightMargin = context.getResources().getDimensionPixelSize(R.dimen.media_status_right_margin);
		int bottomMargin = context.getResources().getDimensionPixelSize(R.dimen.media_status_bottom_margin);
		RelativeLayout.LayoutParams statusParams = new RelativeLayout.LayoutParams(width, height);
		statusParams.rightMargin = rightMargin;
		statusParams.bottomMargin = bottomMargin;
		statusParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		statusParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		checkStatusImage.setLayoutParams(statusParams);
		refreshSelectStatusImage();
	}
	
	private void initFrameLayout() {
		mRelativeLayout = new RelativeLayout(context);
		LinearLayout.LayoutParams frameParams = new LinearLayout.LayoutParams(posterW, posterH);
		mRelativeLayout.setLayoutParams(frameParams);
		
		mRelativeLayout.addView(borderImage);
		mRelativeLayout.addView(posterImage);
		mRelativeLayout.addView(maskImage);
		mRelativeLayout.addView(clickView);
		mRelativeLayout.addView(checkStatusImage);
	}
	
	private void initInfoView() {
		infoView = View.inflate(context, R.layout.media_view_info, null);
		refreshInfoView();
		nameView = (TextView) infoView.findViewById(R.id.media_view_name);
		statusView = (TextView) infoView.findViewById(R.id.media_view_status);
	}
	
	//packaged method
	private void buildContentInfo() {
		contentInfo = srcContentInfo;
		if (contentInfo instanceof PlayHistory) {
			contentInfo = ((PlayHistory)contentInfo).getPlayItem();
		} else if (contentInfo instanceof Favorite) {
			contentInfo = ((Favorite)contentInfo).getFavoriteItem();			
		}
	}
	
	private void adjustMediaType() {
		refreshPosterSize();
		refreshBorderBg();
		refreshPosterImage();
		refreshFrameLayout();
		refreshInfoView();
	}
	
	private void adjustMediaType(int width, int height) {
		posterW = width;
		posterH = height;
		refreshBorderBg();
		refreshPosterImage();
		refreshFrameLayout();
		refreshInfoView();
	}
	
	private void refreshPosterSize() {
		DKLog.d(TAG, "refreshPosterSize: " + mediaType);
		Resources res = context.getResources();
		if (mediaType == MediaViewHelper.UI_COVER_TYPE) {
			posterW = res.getDimensionPixelSize(R.dimen.media_cover_width);
			posterH = res.getDimensionPixelSize(R.dimen.media_cover_height);
		} else if (mediaType == MediaViewHelper.UI_BANNER_TYPE) {
			posterW = res.getDimensionPixelSize(R.dimen.media_banner_width);
			posterH = res.getDimensionPixelSize(R.dimen.media_banner_height);
		} else if (mediaType == MediaViewHelper.UI_SMALL_COVER_TYPE) {
			posterW = res.getDimensionPixelSize(R.dimen.media_small_cover_width);
			posterH = res.getDimensionPixelSize(R.dimen.media_small_cover_height);
		} else if (mediaType == MediaViewHelper.UI_LOCAL_COVER_TYPE) {
			posterW = res.getDimensionPixelSize(R.dimen.media_local_cover_width);
			posterH = res.getDimensionPixelSize(R.dimen.media_local_cover_height);			
		} else if (mediaType == MediaViewHelper.UI_LOCAL_NORMAL_TYPE
				|| mediaType == MediaViewHelper.UI_LOCAL_PADDING_TYPE) {
			posterW = res.getDimensionPixelSize(R.dimen.media_local_width);
			posterH = res.getDimensionPixelSize(R.dimen.media_local_height);
		} else if (mediaType == MediaViewHelper.UI_LOCAL_DIR_TYPE) {
			posterW = res.getDimensionPixelSize(R.dimen.media_local_cover_width);
			posterH = res.getDimensionPixelSize(R.dimen.media_local_cover_height);
		}
	}
	
	private void refreshBorderBg() {
		if (contentInfo instanceof TelevisionInfo) {
			TelevisionInfo televisionInfo = (TelevisionInfo) contentInfo;
			int televisionBgColor = televisionInfo.backgroundcolor;
			switch (televisionBgColor) {
			case ApiConfig.COLOR_ORANGE:
				borderImage.setBackgroundResource(R.drawable.tv_bg_orange);
				break;
			case ApiConfig.COLOR_RED:
				borderImage.setBackgroundResource(R.drawable.tv_bg_red);
				break;
			case ApiConfig.COLOR_GREEN:
				borderImage.setBackgroundResource(R.drawable.tv_bg_green);
				break;
			case ApiConfig.COLOR_BLUE:
				borderImage.setBackgroundResource(R.drawable.tv_bg_blue);
				break;
			default:
				break;
			}
		} else {
			if (mediaType == MediaViewHelper.UI_BANNER_TYPE) {
				borderImage.setBackgroundResource(R.drawable.media_border_default_bg);
			} else if (mediaType == MediaViewHelper.UI_COVER_TYPE
					|| mediaType == MediaViewHelper.UI_SMALL_COVER_TYPE) {
				borderImage.setBackgroundResource(R.drawable.media_border_default_bg);
			} else if (mediaType == MediaViewHelper.UI_LOCAL_NORMAL_TYPE 
					|| mediaType == MediaViewHelper.UI_LOCAL_PADDING_TYPE
					|| mediaType == MediaViewHelper.UI_LOCAL_COVER_TYPE) {
				borderImage.setBackgroundResource(R.drawable.media_border_default_bg);
			} else if (mediaType == MediaViewHelper.UI_LOCAL_DIR_TYPE) {
				borderImage.setBackgroundResource(R.drawable.media_border_dir_default_bg);
			}
		}
	}
	
	private void refreshPosterImage() {
		posterImage.setMediaType(mediaType);
	}
	
	private void refreshPosterParams() {
		if (contentInfo instanceof TelevisionInfo) {
			RelativeLayout.LayoutParams posterParams = new 
					RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
			posterParams.addRule(RelativeLayout.CENTER_IN_PARENT);
			posterImage.setLayoutParams(posterParams);
		} else {
			RelativeLayout.LayoutParams posterParams = new 
					RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
			posterParams.addRule(RelativeLayout.CENTER_IN_PARENT);
			posterImage.setLayoutParams(posterParams);
		}
	}
	
	private void refreshSelectStatusImage() {
		checkStatusImage.setSelected(isSelected);
		if (inEditMode) {
			checkStatusImage.setVisibility(View.VISIBLE);
		} else {
			checkStatusImage.setVisibility(View.INVISIBLE);
		}
		DKLog.d(TAG, "refreshSelectStatusImage: " + inEditMode);
	}
	
	private void refreshFrameLayout() {
		LinearLayout.LayoutParams frameParams = new LinearLayout.LayoutParams(posterW, posterH);
		mRelativeLayout.setLayoutParams(frameParams);
	}
	
	private void refreshInfoView() {
		int infoViewTopMargin = getResources().getDimensionPixelSize(R.dimen.media_info_top_margin);
		int infoViewTopPadding = getResources().getDimensionPixelSize(R.dimen.media_info_top_padding);
		LinearLayout.LayoutParams  infoParams = new LayoutParams(posterW, LayoutParams.WRAP_CONTENT);
		if (mediaType == MediaViewHelper.UI_LOCAL_PADDING_TYPE) {
			infoParams.setMargins(0, infoViewTopMargin + infoViewTopPadding, 0, 0);
		} else {
			infoParams.setMargins(0, infoViewTopMargin, 0, 0);
		}
		infoView.setLayoutParams(infoParams);
		infoView.setVisibility(showText ? View.VISIBLE : View.GONE);
	}
	
	private void refreshContent() {
		DKLog.d(TAG, "refreshContent");
		String status = null;
		String mediaName = null;
		ImageUrlInfo smallImageUrl = null;
		ThumbnailTaskInfo[] thumbnailTaskInfos = null;
		if (contentInfo instanceof MediaInfo) {
			MediaInfo mediaInfo = (MediaInfo) contentInfo;
			mediaName = mediaInfo.medianame;
			status = MediaViewHelper.getMediaStatus(mediaInfo);
			smallImageUrl = mediaInfo.smallImageURL;
			posterImage.setImageUrlInfo(smallImageUrl);
		} else if (contentInfo instanceof TelevisionInfo) {
			TelevisionInfo televisionInfo = (TelevisionInfo) contentInfo;
			mediaName = televisionInfo.medianame;
			smallImageUrl = televisionInfo.smallImageURL;
			posterImage.setImageUrlInfo(smallImageUrl);
			TelevisionShow currentShow = televisionInfo.getCurrentShow(); 
			if (currentShow != null){
				status = currentShow.videoname;
			}
		} else if (contentInfo instanceof LocalMediaList) {
			LocalMediaList localMediaList = (LocalMediaList) contentInfo;
			mediaName = localMediaList.getName();
			status = localMediaList.getStatus();
			
			if (localMediaList.size() > 0) {
				thumbnailTaskInfos = generateThumbnailTaskInfos(localMediaList);
				posterImage.setThumbnailTaskInfos(thumbnailTaskInfos);
			}
			
		} else if (contentInfo instanceof LocalMedia) {
			LocalMedia localMedia = (LocalMedia) contentInfo;
			mediaName = localMedia.getName();
			status = localMedia.getStatus();
			
			thumbnailTaskInfos = generateThumbnailTaskInfos(localMedia);
			posterImage.setThumbnailTaskInfos(thumbnailTaskInfos);
		} else if (contentInfo instanceof SpecialSubject) {
			SpecialSubject specialSubject = (SpecialSubject) contentInfo;
			smallImageUrl = specialSubject.posterUrl;
			posterImage.setImageUrlInfo(smallImageUrl);
		} else if (contentInfo instanceof OfflineMediaList) {
			OfflineMediaList offlineMediaList = (OfflineMediaList) contentInfo;
			mediaName = offlineMediaList.getName();
			status = offlineMediaList.getStatus();
			
			thumbnailTaskInfos = generateThumbnailTaskInfos(offlineMediaList);
			posterImage.setThumbnailTaskInfos(thumbnailTaskInfos);
		} else if (contentInfo instanceof OfflineMedia) {
			OfflineMedia offlineMedia = (OfflineMedia) contentInfo;
			mediaName = offlineMedia.getName();
			status = offlineMedia.getStatus();
			
			thumbnailTaskInfos = generateThumbnailTaskInfos(offlineMedia);
			posterImage.setThumbnailTaskInfos(thumbnailTaskInfos);
		} else if (contentInfo instanceof BaseDevice) {
			BaseDevice baseDevice = (BaseDevice) contentInfo;
			mediaName = baseDevice.getName();
			status = baseDevice.getStatus();
			
			posterImage.setBackgroundResource(R.drawable.poster_device);
		} else if (contentInfo instanceof MediaItem) {
			MediaItem mediaItem = (MediaItem) contentInfo;
			mediaName = mediaItem.getName();
			
			thumbnailTaskInfos = generateThumbnailTaskInfos(mediaItem);
			posterImage.setThumbnailTaskInfos(thumbnailTaskInfos);
		}
		
		refreshPosterParams();
		refreshBorderBg();
		refreshSelectStatusImage();
		
		if (infoView != null) {	
			if (!Util.isEmpty(mediaName)) {
				nameView.setText(mediaName);
			} else {
				nameView.setText("");
			}
			if (!Util.isEmpty(status)) {
				statusView.setText(status);
			} else {
				statusView.setText("");
			}
		}
		
		infoView.setVisibility(showText ? View.VISIBLE : View.GONE);
	}
	
	private ThumbnailTaskInfo[] generateThumbnailTaskInfos(Object object) {
		ThumbnailTaskInfo[] thumbnailTaskInfos = null;
		if (object instanceof LocalMediaList) {
			LocalMediaList localMediaList = (LocalMediaList) object;
			if (localMediaList.size() == 1) {
				thumbnailTaskInfos = new ThumbnailTaskInfo[1];
				thumbnailTaskInfos[0] = generateThumbnailTaskInfo(localMediaList.get(0));
			} else if (localMediaList.size() > 1){
				thumbnailTaskInfos = new ThumbnailTaskInfo[2];
				thumbnailTaskInfos[0] = generateThumbnailTaskInfo(localMediaList.get(0));
				thumbnailTaskInfos[1] = generateThumbnailTaskInfo(localMediaList.get(1));
			}	
		} else if (object instanceof OfflineMediaList) {
			OfflineMediaList offlineMediaList = (OfflineMediaList) object;
			if (offlineMediaList.size() == 1) {
				thumbnailTaskInfos = new ThumbnailTaskInfo[1];
				thumbnailTaskInfos[0] = generateThumbnailTaskInfo(offlineMediaList.get(0));
			} else if (offlineMediaList.size() > 1) {
				thumbnailTaskInfos = new ThumbnailTaskInfo[2];
				thumbnailTaskInfos[0] = generateThumbnailTaskInfo(offlineMediaList.get(0));
				thumbnailTaskInfos[1] = generateThumbnailTaskInfo(offlineMediaList.get(1));
			}
		} else if (object instanceof LocalMedia) {
			LocalMedia localMedia = (LocalMedia) object;
			thumbnailTaskInfos = new ThumbnailTaskInfo[1];
			thumbnailTaskInfos[0] = generateThumbnailTaskInfo(localMedia);
		} else if (object instanceof OfflineMedia) {
			OfflineMedia offlineMedia = (OfflineMedia) object;
			thumbnailTaskInfos = new ThumbnailTaskInfo[1];
			thumbnailTaskInfos[0] = generateThumbnailTaskInfo(offlineMedia);
		} else if (object instanceof MediaItem) {
			MediaItem mediaItem = (MediaItem) object;
			thumbnailTaskInfos = new ThumbnailTaskInfo[1];
			thumbnailTaskInfos[0] = generateThumbnailTaskInfo(mediaItem);
		}
		return thumbnailTaskInfos;
	}
	
	private ThumbnailTaskInfo generateThumbnailTaskInfo(Object object) {
		ThumbnailTaskInfo thumbnailTaskInfo = null;
		if (object instanceof LocalMedia) {
			LocalMedia localMedia = (LocalMedia) object;
			if (!Util.isEmpty(localMedia.mediaPath)) {
				thumbnailTaskInfo = new ThumbnailTaskInfo(localMedia.mediaPath, 3);
			}
		} else if (object instanceof OfflineMedia) {
			OfflineMedia offlineMedia = (OfflineMedia) object;
			if (!Util.isEmpty(offlineMedia.localPath)) {
				thumbnailTaskInfo = new ThumbnailTaskInfo(offlineMedia.localPath, 3);
			}
		} else if (object instanceof MediaItem) {
			MediaItem mediaItem = (MediaItem) object;
			if (!Util.isEmpty(mediaItem.getMediaUrl())) {
				thumbnailTaskInfo = new ThumbnailTaskInfo(mediaItem.getMediaUrl(), 3);
			}
		}
		return thumbnailTaskInfo;
	}
	
	//UI callback
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	private OnClickListener mOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if (onMediaClickListener != null) {
				onMediaClickListener.onMediaClick(MediaView.this, srcContentInfo);
			}
		}
	};
	private OnLongClickListener mOnLongClickListener = new OnLongClickListener() {
		
		@Override
		public boolean onLongClick(View v) {
			if (onMediaLongClickListener != null) {
				onMediaLongClickListener.onMediaLongClick(MediaView.this, srcContentInfo);
			}
			return true;
		}
	};
	
	//self def class
	public static interface OnMediaClickListener {
		public void onMediaClick(MediaView mediaView, Object media);
	}
	
	public static interface OnMediaLongClickListener {
		public void onMediaLongClick(MediaView mediaView, Object media);
	}
}
