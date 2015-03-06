package com.miui.videoplayer.framework.popup;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.miui.video.R;
import com.miui.videoplayer.VideoPlayerActivity;
import com.miui.videoplayer.framework.utils.DisplayInformationFetcher;

public class TopStatusBarPopupWindowV5 extends TopStatusBarPopupWindow {
	private TextView localOnlineTextView;
	private Context mContext;
	
	private ImageView mBackImageView;
	private TextView mMediaNameTextView;
	
	public TopStatusBarPopupWindowV5(Context context) {
		super(context);
		setHeight(LayoutParams.WRAP_CONTENT);
	
		mContext = context;
		localOnlineTextView = (TextView) getContentView().findViewById(R.id.vp_top_sub_title);
		mBackImageView = (ImageView) getContentView().findViewById(R.id.vp_top_back_img);
		mMediaNameTextView = (TextView) getContentView().findViewById(R.id.vp_top_title);
	}

	public TextView getMediaNameTextView() {
		return mMediaNameTextView;
	}

	@Override
	protected int getLayoutId() {
		return R.layout.vp_popup_top_status_bar_v5;
	}

	public void show(View anchor, Uri uri, String mediaTitle, int mediaCi) {
		updateLayout();
		super.show(anchor, uri, mediaTitle);
		if (VideoPlayerActivity.mediaSubTitle != null) {
			localOnlineTextView.setText(VideoPlayerActivity.mediaSubTitle);
		} else {
			localOnlineTextView.setText(mContext.getResources().getString(R.string.top_status_local_media));
		}
		Log.e("mediaci: ", mediaCi + "");
		if (uri != null && uri.getScheme() != null) {
			String scheme = uri.getScheme();
			if (scheme.equals("http") || scheme.equals("https") || scheme.equals("rtsp")) {
				localOnlineTextView.setText(mContext.getResources().getString(R.string.top_status_online_media));
			}
		}
	}

	public void updateLayout() {
		/*
		int orientation = DisplayInformationFetcher.getInstance(mContext).getScreenOrientation();
		LayoutParams backLp = (RelativeLayout.LayoutParams) mBackImageView.getLayoutParams();
		LayoutParams mediaNameLp = (RelativeLayout.LayoutParams) mMediaNameTextView.getLayoutParams();
		if (orientation == DisplayInformationFetcher.SCREEN_LAND) {
			backLp.topMargin = 0;
			mediaNameLp.topMargin = 4;
		} else {
			backLp.topMargin = 20;
			mediaNameLp.topMargin = 24;
		}
		getContentView().requestLayout();
		*/
	}
}
