package com.miui.videoplayer.framework.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.miui.video.R;
import com.miui.videoplayer.framework.history.PlayHistoryManager.PlayHistoryEntry;
import com.miui.videoplayer.framework.popup.BufferFullScreenPopupWindow;
import com.miui.videoplayer.framework.popup.ReceiveEventV5PopupWindow;
import com.miui.videoplayer.framework.popup.TopStatusBarPopupWindowV5;

public class OriginMediaControllerV5 extends OriginMediaController{
	private static final String TAG = OriginMediaControllerV5.class.getSimpleName();
	
	private Context mContext;
	//private ImageView mBufferingImageView;
	private ProgressBar mProgressBar;
	private ImageView mPauseImageView;
	
	private ReceiveEventV5PopupWindow mReceiveEventV5PopupWindow;
	private BufferFullScreenPopupWindow mBufferFullScreenPopupWindow;
	
	public OriginMediaControllerV5(Context context) {
		super(context);
		
		this.mContext = context;
	}

	public OriginMediaControllerV5(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		this.mContext = context;
	}

	public OriginMediaControllerV5(Context context, boolean useFastForward) {
		super(context, useFastForward);
		
		this.mContext = context;
	}

	@Override
	protected int getLayoutId() {
		return R.layout.vp_popup_ctrl_bottom_v5;
	}

	@Override
	protected int getPauseImageResId() {
		return R.drawable.vp_pause_v5_imageview;
	}

	@Override
	protected int getPlayImageResId() {
		return R.drawable.vp_play_v5_imageview;
	}

	protected int getBigPauseImageResId() {
		return R.drawable.vp_pause_v5_imageview;
	}
	
	protected int getBigPlayImageResId() {
		return R.drawable.vp_play_v5_imageview;
	}
	@Override
	protected View makeControllerView() {
		View result = super.makeControllerView();
		//mBufferingImageView = (ImageView) result.findViewById(R.id.buffering);
		mProgressBar = (ProgressBar) result.findViewById(R.id.bufferingprogressbar);
		mPauseImageView = (ImageView) result.findViewById(R.id.pause);
		return result;
	}

	@Override
	public void changeToAirkanSize() {
		// TODO Auto-generated method stub
//		super.changeToAirkanSize();
		if(getCtrlMenuPopupWindow() != null) {
			getCtrlMenuPopupWindow().setMiLinkEnabled(true);
		}
	}

	@Override
	public void changeToLocalPlaySize() {
		// TODO Auto-generated method stub
//		super.changeToLocalPlaySize();
		if(getCtrlMenuPopupWindow() != null) {
			getCtrlMenuPopupWindow().setMiLinkEnabled(false);
		}
	}

	public void bufferingStart() {
//		if (mBufferingProgressDialog == null) {
//			mBufferingProgressDialog = new BufferingProgressDialog(mActivity);
//			mBufferingProgressDialog.setOnKeyDownListener(this);
//		}
//		mBufferingProgressDialog.show();
		Log.e(TAG, "Buffering start");
		mPauseImageView.setVisibility(View.INVISIBLE);
		mProgressBar.setVisibility(View.VISIBLE);
		//mBufferingImageView.setVisibility(View.VISIBLE);
		//Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.vp_buffering_rotate);
		//mBufferingImageView.startAnimation(animation);
		if (mBufferFullScreenPopupWindow == null) {
			mBufferFullScreenPopupWindow = new BufferFullScreenPopupWindow(mContext);
//			mFullScreenPopupWindow.setBackgroundAlpha(255);
//			mFullScreenPopupWindow.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.vp_fullscreen_play_backgroud_v5));
		}
		String mediaTitle = "";
		if (this.getTopStatusBarPopupWindow() instanceof TopStatusBarPopupWindowV5) {
			//TopStatusBarPopupWindowV5 topStatusBarPopupWindow = (TopStatusBarPopupWindowV5) this.getTopStatusBarPopupWindow();
			//mediaTitle = topStatusBarPopupWindow.getMediaNameTextView().getText().toString();
			String playingUri = this.getPlayingUri();
			if (playingUri != null) {
				PlayHistoryEntry entry  = this.getMediaTitle(playingUri.toString());
				if (entry != null) {
					mediaTitle = entry.getVideoName();
				}
			}
		}
		
		mBufferFullScreenPopupWindow.show(getDuoKanMediaController(), mediaTitle);
	}
	
	public void bufferUpdating(int percent) {
		if (percent == 100) {
			return;
		}
		if (mBufferFullScreenPopupWindow != null && mBufferFullScreenPopupWindow.isShowing()) {
			mBufferFullScreenPopupWindow.setBufferedPercent(percent);
		}
	}

	public void bufferingEnd() {
//		if (mBufferingProgressDialog != null) {
//			mBufferingProgressDialog.dismiss();
//		}
		Log.e(TAG, "Buffering end");
		mPauseImageView.setVisibility(View.VISIBLE);
		//mBufferingImageView.setVisibility(View.INVISIBLE);
		mProgressBar.setVisibility(View.INVISIBLE);
		if (mBufferFullScreenPopupWindow != null && mBufferFullScreenPopupWindow.isShowing()) {
			mBufferFullScreenPopupWindow.dismiss();
		}
	}

//	@Override
//	public boolean onInfo(MediaPlayer mp, int what, int extra) {
//		if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
//			Log.i(TAG, "Buffering start");
//			bufferingStart();
//		}
//		
//		if (what == MediaPlayer.MEDIA_INFO_BUFFERING_UPDATE) {
//		
//		}
//		
//		if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
//			Log.i(TAG, "Buffering end");
//			bufferingEnd();
//		}
//		return true;
//	}
//
//	@Override
//	public boolean onInfo(android.media.MediaPlayer mp, int what, int extra) {
//		if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
//			Log.i(TAG, "Buffering start");
//			bufferingStart();
//		}
//		if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
//			Log.i(TAG, "Buffering end");
//			bufferingEnd();
//		}
//		return true;
//	}

	@Override
	public void show(int timeout) {
		
		super.show(timeout);
		if (!getReceiveEventV5PopupWindow().isShowing()) {
			getReceiveEventV5PopupWindow().show(getDuoKanMediaController());
		}
	}

	@Override
	public void hide() {
		super.hide();
		if (getReceiveEventV5PopupWindow().isShowing()) {
			getReceiveEventV5PopupWindow().dismiss();
		}
	}
	
	public ReceiveEventV5PopupWindow getReceiveEventV5PopupWindow() {
		if (mReceiveEventV5PopupWindow == null) {
			mReceiveEventV5PopupWindow = new ReceiveEventV5PopupWindow(mContext, getDuoKanMediaController());
		}
		return mReceiveEventV5PopupWindow;
	}

	@Override
	public void onScreenOrientationChanged(int orientation) {
		super.onScreenOrientationChanged(orientation);
		if (getReceiveEventV5PopupWindow().isShowing()) {
			getReceiveEventV5PopupWindow().dismiss();
			getReceiveEventV5PopupWindow().show(getDuoKanMediaController());
		}
		if (this.getTopStatusBarPopupWindow() instanceof TopStatusBarPopupWindowV5 && this.getTopStatusBarPopupWindow().isShowing()) {
			((TopStatusBarPopupWindowV5) this.getTopStatusBarPopupWindow()).updateLayout();
		}
	}
}
