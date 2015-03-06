package com.miui.videoplayer.framework.popup;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.text.format.Time;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

import com.miui.video.R;
import com.miui.videoplayer.VideoPlayerActivity;
import com.miui.videoplayer.framework.ui.DuoKanMediaController.BackKeyEvent;
import com.miui.videoplayer.framework.utils.AndroidUtils;
import com.miui.videoplayer.framework.utils.DisplayInformationFetcher;

public class TopStatusBarPopupWindow extends ManagedPopupWindow {
	
	private Context mContext;
	
	private TextView mMediaNameTextView;
//	private ImageView mImageView;
	
	private IntentFilter mTimeChangedIntentFilter;
	
	private Time mTime;
	
	private boolean mHasRegisterTimeReceiver = false;
	
	private View mAnchor;
	
	public TopStatusBarPopupWindow(Context context) {
		this.setContentView(LayoutInflater.from(context).inflate(getLayoutId(), null));
		this.setWidth(LayoutParams.MATCH_PARENT);
		setHeight(LayoutParams.WRAP_CONTENT);
		this.mContext = context;
		
		View rootView = getContentView();
		
		ImageView backImageView = (ImageView) rootView.findViewById(R.id.vp_top_back_img);
		backImageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (mAnchor != null) {
					BackKeyEvent backKeyEvent = new BackKeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK);
					mAnchor.onKeyDown(KeyEvent.KEYCODE_BACK, backKeyEvent);
				}
			}
		});
		
		
		mMediaNameTextView = (TextView) rootView.findViewById(R.id.vp_top_title);
		mMediaNameTextView.setAlpha(1f);
//		mImageView = (ImageView) rootView.findViewById(R.id.top_info_bettery_img);
		
		mTimeChangedIntentFilter = new IntentFilter(Intent.ACTION_TIME_TICK);
		
		mTime = new Time();
	}
	
	protected int getLayoutId() {
		return R.layout.vp_popup_top_status_bar;
	}
	
	public void show(View anchor, Uri uri, String mediaTitle) {
//		Log.e("SHOWSHOWSHOW!!!!!!!!!!!!!!!!!!!!!!!!!!!!!", "SHOWSHOWSHOWSHOW!!!!!!!!!!!!!!!!!!!!!!");
		this.mAnchor = anchor;
		if (!mHasRegisterTimeReceiver) {
			mContext.registerReceiver(mTimeChangedBroadcastReceiver, mTimeChangedIntentFilter);
			mHasRegisterTimeReceiver = true;
		}
		this.showAtLocation(anchor, Gravity.TOP, 0, 0);
		showSystemStatusBar();
		updateBattery();
		if (mediaTitle != null) {
			StringBuilder sb = new StringBuilder();
			sb.append(formatMediaName(mediaTitle));
			if(VideoPlayerActivity.curCi > 0 && VideoPlayerActivity.mediaCount > 1) {
				String str = mContext.getResources().getString(R.string.episode_suffix);
				str = String.format(str, VideoPlayerActivity.curCi);
				sb.append(" ");
				sb.append(str);
			}
			mMediaNameTextView.setText(sb.toString());
		} else {
			updateMediaName(uri);
		}
 		updateCurrentTime();
	}
	

	private void showSystemStatusBar() {
//		android.app.StatusBarManager mStatusBarManager = (StatusBarManager) mContext.getSystemService(Context.STATUS_BAR_SERVICE);
//		mStatusBarManager.disable(ExtraStatusBarManager.DISABLE_SIMPLE_STATUS_BAR);
	}

	private void updateBattery() {
		float battery = AndroidUtils.getCurrentBattery(mContext);
		Log.i("battery", battery + " ");
	}

	public void updateMediaName(Uri uri) {
		if (uri != null) {
			String mediaName = null;
			if (uri.getScheme() != null && uri.getScheme().equals("content")) {
				//handler the content://... uri
				Log.i("TopStatusBarPopupWindow", "input uri: " + uri);
				String realFilePath = AndroidUtils.getRealFilePathFromContentUri(mContext, uri);
				Log.i("TopStatusBarPopupWindow", "realFilePath: " + realFilePath);
				if (realFilePath != null) {
					Uri parsedUri = Uri.parse(realFilePath);
					if (parsedUri != null) {
						mediaName = parsedUri.getLastPathSegment();
					}
				}
			} else {
				mediaName = uri.getLastPathSegment();
			}
			
			if (mediaName != null) {
				mMediaNameTextView.setText(formatMediaName(mediaName));
			}
		}
	}

	private String formatMediaName(String mediaName) {
		int orientation = DisplayInformationFetcher.getInstance(mContext).getScreenOrientation();
		int maxlen = 14;//中文最多7，英文最多14,竖屏下，横屏加倍
		if (orientation == DisplayInformationFetcher.SCREEN_LAND) {
			maxlen = 24;
		}
		StringBuffer strBuffer = new StringBuffer();
		if (mediaName != null && mediaName.length() > maxlen/2) {
			String endString = "...";
			int lenTempt = 0;
			for(int i = 0;i < mediaName.length();i++){
				String str = mediaName.substring(i, i+1);
				if(lenTempt <= maxlen){
					strBuffer.append(str);
					if(str.matches("[\\u4E00-\\u9FA5]+"))
						lenTempt += 2;
					else
						lenTempt++;
				}else{
					String name = strBuffer.substring(0, strBuffer.length()-1);
					name += endString;
					return name;
				}
			}
		}
		return mediaName;
	}

	private void updateCurrentTime() {
		if(mTime != null) {
			mTime.setToNow();
		}
	}
	
	@Override
	public void dismiss() {
//		Log.e("dismiss!!!!!!!!!!!!!!!!!!!!!!!!!!!!!", "dismiss!!!!!!!!!!!!!!!!!!!!!!");
		if (mHasRegisterTimeReceiver) {
			try {
				mContext.unregisterReceiver(mTimeChangedBroadcastReceiver);
			} catch (Exception e) {
				e.printStackTrace();
			}
			mHasRegisterTimeReceiver = false;
		}
		super.dismiss();
	}
	
	private BroadcastReceiver mTimeChangedBroadcastReceiver = new  BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
//			Log.i("receive: ", intent.getAction() + "");
			if (Intent.ACTION_TIME_TICK.equals(intent.getAction())) {
				updateCurrentTime();
			}
		}
	};

	@Override
	public void show(View anchor) {
		// TODO Auto-generated method stub
		
	}
}
