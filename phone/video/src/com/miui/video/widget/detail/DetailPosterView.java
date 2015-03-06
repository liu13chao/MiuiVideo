package com.miui.video.widget.detail;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;

import com.miui.video.R;
import com.miui.video.model.ImageManager;
import com.miui.video.type.ImageUrlInfo;

public class DetailPosterView extends RelativeLayout {
	
	private Context mContext;
	private ImageManager mImageManager;
	
	private ImageView mPosterView;
	private ImageView mPlayView;
//	private View mPosterBottomMask;

	public DetailPosterView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
		init();
	}

	public DetailPosterView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		init();
	}

	public DetailPosterView(Context context) {
		super(context);
		this.mContext = context;
		init();
	}
	
	public void setImageUrlInfo(ImageUrlInfo imageUrlInfo) {
		if(imageUrlInfo != null) {
			if(!ImageManager.isUrlDone(imageUrlInfo, mPosterView)) {
				refreshPosterDefaultBg();
	            mImageManager.fetchImage(ImageManager.createTask(imageUrlInfo, null), mPosterView);
			}
		} else {
			refreshPosterDefaultBg();
		}
	}
	
	public void setPosterAlpha(float alpha) {
		float playViewAlpha = alpha;
		if(playViewAlpha < 0) {
			playViewAlpha = 0;
		} else if(playViewAlpha > 1) {
			playViewAlpha = 1;
		}
		mPlayView.setAlpha(playViewAlpha);
		
		float posterViewAlpha = alpha;
		if(posterViewAlpha < 0.2) {
			posterViewAlpha = 0.2f;
		}
//		mPosterView.setAlpha(posterViewAlpha);
	}

	//init
	private void init() {
		initManager();
		initUI();
	}
	
	private void initManager() {
		mImageManager = ImageManager.getInstance();
	}
	
	private void initUI() {
		initPosterImage();
		initPosterMask();
//		initPosterBottomMask();
		initPlayView();
	}
	
	private void initPosterImage() {
		mPosterView = new ImageView(mContext);
		mPosterView.setScaleType(ScaleType.FIT_XY);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		addView(mPosterView, params);
	}
	
	private void initPosterMask(){
	    ImageView mask = new ImageView(mContext);
	    mask.setImageResource(R.drawable.detail_poster_mask);
	    mask.setScaleType(ScaleType.FIT_XY);
	    LayoutParams p = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
	    p.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
	    mask.setLayoutParams(p);
	    addView(mask);
	}
	
//	private void initPosterBottomMask() {
//		mPosterBottomMask = new View(mContext);
//		mPosterBottomMask.setBackgroundResource(R.drawable.com_bg_gray);
//		int height = mContext.getResources().getDimensionPixelSize(R.dimen.detail_poster_bottom_mask_height);
//		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, height);
//		params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//		addView(mPosterBottomMask, params);
//	}
	
	private void initPlayView() {
		mPlayView = new ImageView(mContext);
		mPlayView.setBackgroundResource(R.drawable.detail_summary_play);
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.topMargin = mContext.getResources().getDimensionPixelSize(R.dimen.detail_play_view_top_margin);
		params.addRule(RelativeLayout.CENTER_HORIZONTAL);
		addView(mPlayView, params);
		//后来UI决定不要这个按钮了
		mPlayView.setVisibility(View.INVISIBLE);
	}
	
	//packaged method
	private void refreshPosterDefaultBg() {
		mPosterView.setImageResource(R.drawable.transparent);
	}
}
