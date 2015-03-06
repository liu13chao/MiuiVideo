package com.miui.videoplayer.framework.popup;

import java.util.List;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;

import com.miui.video.R;
import com.miui.videoplayer.Constants;
import com.miui.videoplayer.VideoPlayerActivity;
import com.miui.videoplayer.framework.airkan.AirkanManager;
import com.miui.videoplayer.framework.views.OriginMediaController;
import com.miui.videoplayer.widget.VpCtrlMenu;
import com.miui.videoplayer.widget.VpCtrlMenuItem;

/**
 *@author tangfuling
 *
 */

public class VpCtrlMenuPopupWindow extends ManagedPopupWindow {
	private Context mContext;
	private Handler mHandler;
	private int margin = 0;
	
	private VpCtrlMenu mVpCtrlMenu;
	private VpCtrlMenuItem mMenuItemSelectSource;
	private VpCtrlMenuItem mMenuItemSelectCi;
	private VpCtrlMenuItem mMenuItemOffline;
	private VpCtrlMenuItem mMenuItemMiLink;
	private VpCtrlMenuItem mMenuItemFunction;
	
	private AirkanManager mAirKanManager;
	private static final int START_AIRKAN = 8;
	
    private VpCtrlFullScreenPopupWindow mFullScreenPopupWindow;
	
	public VpCtrlMenuPopupWindow(Context context, Handler handler) {
		super(LayoutInflater.from(context).inflate(R.layout.vp_popup_ctrl_menu, null));
		this.mContext = context;
		this.mHandler = handler;
		this.setWidth(android.app.ActionBar.LayoutParams.WRAP_CONTENT);
		this.setHeight(android.app.ActionBar.LayoutParams.WRAP_CONTENT);
		this.setTouchable(true);
		this.setOutsideTouchable(true);
		init();
	}
	
	public void setAirKanManager(AirkanManager airKanManager) {
		this.mAirKanManager = airKanManager;
	}
	
	public void setMiLinkEnabled(boolean enabled) {
		if(enabled) {
			mMenuItemMiLink.setVisibility(View.VISIBLE);
		} else {
			mMenuItemMiLink.setVisibility(View.GONE);
		}
	}
	
	//init
	private void init() {
		View contentView = getContentView();
		mVpCtrlMenu = (VpCtrlMenu) contentView.findViewById(R.id.vp_ctrl_menu);
		mMenuItemSelectSource = new VpCtrlMenuItem(mContext);
		mMenuItemSelectSource.setBackgroundResource(R.drawable.vp_list_item_bg);
		mMenuItemSelectSource.setIcon(R.drawable.vp_icon_select_source);
		mMenuItemSelectSource.setText(R.string.vp_select_source);
		mMenuItemSelectSource.setOnClickListener(mOnClickListener);
		mVpCtrlMenu.addLeftMenu(mMenuItemSelectSource);
		mMenuItemSelectCi = new VpCtrlMenuItem(mContext);
		mMenuItemSelectCi.setBackgroundResource(R.drawable.vp_list_item_bg);
		mMenuItemSelectCi.setIcon(R.drawable.vp_icon_select_ci);
		mMenuItemSelectCi.setText(R.string.vp_select_ci);
		mMenuItemSelectCi.setOnClickListener(mOnClickListener);
		mVpCtrlMenu.addLeftMenu(mMenuItemSelectCi);
		mMenuItemOffline = new VpCtrlMenuItem(mContext);
		mMenuItemOffline.setBackgroundResource(R.drawable.vp_list_item_bg);
		mMenuItemOffline.setIcon(R.drawable.vp_icon_offline);
		mMenuItemOffline.setText(R.string.vp_offline);
		mMenuItemOffline.setOnClickListener(mOnClickListener);
//		mVpCtrlMenu.addItem(mMenuItemOffline);
		mMenuItemMiLink = new VpCtrlMenuItem(mContext);
		mMenuItemMiLink.setBackgroundResource(R.drawable.vp_list_item_bg);
		mMenuItemMiLink.setIcon(R.drawable.vp_icon_milink);
		mMenuItemMiLink.setText(R.string.vp_milink);
		mMenuItemMiLink.setOnClickListener(mOnClickListener);
		mVpCtrlMenu.addLeftMenu(mMenuItemMiLink);
		mMenuItemFunction = new VpCtrlMenuItem(mContext);
		mMenuItemFunction.setBackgroundResource(R.drawable.vp_list_item_bg);
		mMenuItemFunction.setIcon(R.drawable.vp_icon_function);
		mMenuItemFunction.setText(R.string.vp_function);
		mMenuItemFunction.setOnClickListener(mOnClickListener);
		mMenuItemFunction.setDividerVisibility(View.INVISIBLE);
		mVpCtrlMenu.addLeftMenu(mMenuItemFunction);
		
		if (mFullScreenPopupWindow == null) {
			mFullScreenPopupWindow = new VpCtrlFullScreenPopupWindow(mContext);
		}
	}

	@Override
	public void show(View anchor) {
		boolean isShowSelect = VideoPlayerActivity.isShowCiSelect();
		if(isShowSelect) {
			mMenuItemSelectSource.setVisibility(View.VISIBLE);
			mMenuItemSelectCi.setVisibility(View.VISIBLE);
		} else {
			mMenuItemSelectSource.setVisibility(View.GONE);
			mMenuItemSelectCi.setVisibility(View.GONE);
		}
		mFullScreenPopupWindow.show(anchor);
		this.showAtLocation(anchor, Gravity.LEFT, margin, 0);
	}
	
	
	@Override
	public void dismiss() {
		super.dismiss();
		if(mFullScreenPopupWindow.isShowing()) {
			mFullScreenPopupWindow.dismiss();
		}
	}
	
	
	/*
	@Override
	public void show(View anchor) {
		// TODO Auto-generated method stub
		boolean isShowSelect = VideoPlayerActivity.isShowCiSelect();
		boolean isShowDownload = VideoPlayerActivity.isShowCiDownload();
		isShowDownload = false;
		if (isShowSelect || isShowDownload) {
			if (isShowSelect) {
				mSelectCiView.setVisibility(View.VISIBLE);
			} else {
				mSelectCiView.setVisibility(View.GONE);				
			}
			
			if (isShowDownload) {
				mDownloadView.setVisibility(View.VISIBLE);
			} else {
				mDownloadView.setVisibility(View.GONE);
			}
			
			if (isShowDownload&&isShowSelect) {
				divider.setVisibility(View.VISIBLE);
			} else {
				divider.setVisibility(View.GONE);
			}
			this.showAtLocation(anchor, Gravity.LEFT, margin, 0);
		} else {
			if (isShowing()) {
				try {
					dismiss();				
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}
	}
	*/
	
	//UI callback
	private OnClickListener mOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View view) {
			if(mHandler == null) {
				return;
			}
			
			if(view == mMenuItemSelectSource) {
				mHandler.sendEmptyMessage(OriginMediaController.SHOW_SOURCE_SELECT);
			} else if(view == mMenuItemSelectCi) {
				mHandler.sendEmptyMessage(OriginMediaController.SHOW_CI_SELECT);
			} else if(view == mMenuItemOffline) {
				Message msg = mHandler.obtainMessage(OriginMediaController.DOWNLOAD_CI);
				msg.arg1 = VideoPlayerActivity.curCi;
				msg.arg2 = Constants.OFFLINE_OPERATION_ADD;
				mHandler.sendMessage(msg);
			} else if(view == mMenuItemMiLink) {
				List<String> deviceNameList = mAirKanManager.queryAirkanDevices();
				Message msg = mHandler.obtainMessage(START_AIRKAN, deviceNameList);
				mHandler.sendMessage(msg);
			} else if(view == mMenuItemFunction) {
				Message msg = mHandler.obtainMessage(OriginMediaController.MENU_FUNCTION);
				mHandler.sendMessage(msg);
			}
		}
	};
}
