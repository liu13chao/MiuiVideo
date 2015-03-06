package com.miui.videoplayer.framework.popup;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.ListView;
import com.miui.video.R;
import com.miui.videoplayer.framework.DuoKanConstants;
import com.miui.videoplayer.framework.ui.DuoKanMediaController;
import com.miui.videoplayer.framework.ui.LocalMediaPlayerControl;
import com.miui.videoplayer.framework.utils.DisplayInformationFetcher;

public abstract class MenuPopupWindow extends ManagedPopupWindow {
	private boolean mIsPopShowing = false;
	private Context mContext;
	private ListView mOptionMenuListView;
	private DuoKanMediaController mDuoKanMediaController;
	
	private boolean mMenuDismissed = true;
	private boolean mVideoIsPlaying = false;

	private LocalMediaPlayerControl mLocalMediaPlayerControl;
	private VpCtrlFullScreenPopupWindow mFullScreenPopupWindow;
	
	public MenuPopupWindow(Context context) {
		super(LayoutInflater.from(context).inflate(R.layout.vp_popup_ctrl_function, null));
		if (DuoKanConstants.ENABLE_V5_UI) {
			this.setContentView(LayoutInflater.from(context).inflate(R.layout.vp_popup_ctrl_function_v5, null));
		}
		
		this.mContext = context;
		
		setupWindow();
	}

	private void setupWindow() {
		this.setFocusable(true);
		this.setTouchable(true);
		this.setBackgroundDrawable(new ColorDrawable());
		this.setAnimationStyle(R.style.menu_popup_anim_style);
		
		View contentView = getContentView();
		mOptionMenuListView = (ListView) contentView.findViewById(R.id.option_menu_listview);
		mOptionMenuListView.setSelector(R.drawable.vp_list_item_bg);
		mOptionMenuListView.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (mDuoKanMediaController != null && event.getAction() == KeyEvent.ACTION_DOWN && 
						(keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP)) {
					return mDuoKanMediaController.onKeyDown(keyCode, event);
				}
				if (keyCode == KeyEvent.KEYCODE_MENU && event.getRepeatCount() == 0) {
					if(!mIsPopShowing){
						mIsPopShowing = true;
					} else {
						dismiss();
					}
				}
				return false;
			}
		});
		
	}
	
	@Override
	public void dismiss() {
		mIsPopShowing = false;
		if (mMenuDismissed) {
			if (mLocalMediaPlayerControl != null && !mLocalMediaPlayerControl.isPlaying() && mVideoIsPlaying) {
				mLocalMediaPlayerControl.start();
			}
			if (mFullScreenPopupWindow != null && mFullScreenPopupWindow.isShowing()) {
				mFullScreenPopupWindow.dismiss();
			}
		} else {
			mMenuDismissed = true;
		}
		super.dismiss();
	}

	@Override
	public void dismiss(boolean remove) {
		mIsPopShowing = false;
		super.dismiss(remove);
	}

	public void setMenuDismissed(boolean menuDismissed) {
		this.mMenuDismissed = menuDismissed;
	}
	
	public void setPlayingStatus(boolean isPlaying) {
		this.mVideoIsPlaying = isPlaying;
	}

	public boolean getPlayingStatus() {
		return this.mVideoIsPlaying;
	}
	
	@Override
	public void show(View anchor) {
		mVideoIsPlaying = false;
		if (mLocalMediaPlayerControl != null && mLocalMediaPlayerControl.isPlaying()) {
			mVideoIsPlaying = true;
			mLocalMediaPlayerControl.pause();
		}
		if (mFullScreenPopupWindow == null) {
			mFullScreenPopupWindow = new VpCtrlFullScreenPopupWindow(mContext);
		}
		mFullScreenPopupWindow.show(anchor);
		
		int orientation = DisplayInformationFetcher.getInstance(mContext).getScreenOrientation();
		if (DuoKanConstants.ENABLE_V5_UI) {
//			getContentView().setBackgroundResource(getBackgroundResId(orientation));
		}
		updateWidthHeight(orientation);
		this.setAnimationStyle(R.style.menu_popup_anim_style);
		showAtLocation(anchor, Gravity.LEFT, 0, 0);
	}
	
	protected abstract int getBackgroundResId(int orientation);
	
	private void updateWidthHeight(int orientation) {
		if (orientation == DisplayInformationFetcher.SCREEN_LAND) {
//			int width = (int) mContext.getResources().getDimension(R.dimen.popup_right_menu_option_width);
//			if (DuoKanConstants.ENABLE_V5_UI) {
//				width = (int) mContext.getResources().getDimension(R.dimen.popup_right_menu_option_width_v5);
//			}
//			int width = getMenuWidthForLandScreen();
//			this.setWidth(width);
//			this.setHeight(LayoutParams.WRAP_CONTENT);
		} else {
//			int height = getMenuHeightForPortScreen();
//			this.setWidth(LayoutParams.WRAP_CONTENT);
//			this.setHeight(height);
		}
	}
	
	protected abstract int getMenuHeightForPortScreen();
	protected abstract int getMenuWidthForLandScreen();
	
	public void setDuoKanMediaController(DuoKanMediaController duoKanMediaController) {
		this.mDuoKanMediaController = duoKanMediaController;
	}
	
	protected ListView getOptionMenuListView() {
		return mOptionMenuListView;
	}
	
	protected VpCtrlFullScreenPopupWindow getFullScreenPopupWindow() {
		return mFullScreenPopupWindow;
	}

	public void setFullScreenPopupWindow(VpCtrlFullScreenPopupWindow fullScreenPopupWindow) {
		this.mFullScreenPopupWindow = fullScreenPopupWindow;
	}
	
	protected LocalMediaPlayerControl getLocalMediaPlayerControl() {
		return mLocalMediaPlayerControl;
	}

	public void setLocalMediaPlayerControl(LocalMediaPlayerControl localMediaPlayerControl) {
		this.mLocalMediaPlayerControl = localMediaPlayerControl;
	}
}
