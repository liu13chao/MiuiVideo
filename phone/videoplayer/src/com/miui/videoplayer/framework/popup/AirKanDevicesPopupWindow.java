package com.miui.videoplayer.framework.popup;

import java.util.List;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.miui.video.R;
import com.miui.video.util.Util;
import com.miui.videoplayer.adapter.AirkanDevicesAdapter;
import com.miui.videoplayer.framework.airkan.AirkanManager;

public class AirKanDevicesPopupWindow extends BasePopupWindow {

//	private Uri mPlayingUri;
	private AirkanManager mAirkanManager;
	private AirkanDevicesAdapter mAirkanDevicesAdapter;
	
	public AirKanDevicesPopupWindow(Context context, View anchor) {
		super(context, anchor);
		init();
	}
	
	public void attachAirkanManager(AirkanManager airkanManager) {
		this.mAirkanManager = airkanManager;
	}
	
	
	@Override
	public void show() {
		super.show();
		if(mAirkanManager != null) {
			mAirkanDevicesAdapter.setPlayingDeviceName(mAirkanManager.getPlayingDeviceName());
			List<String> devices = mAirkanManager.queryAirkanDevices();
			String xiaomiPhoneName = mContext.getResources().getString(
	                 R.string.airkan_device_xiaomi_phone);
			devices.add(0, xiaomiPhoneName);
			mAirkanDevicesAdapter.setGroup(devices);
		}
	}

	//init
	private void init() {
		setTitle(R.string.vp_device_list);
		mAirkanDevicesAdapter = new AirkanDevicesAdapter(mContext);
		setAdapter(mAirkanDevicesAdapter);
		setOnItemClickListener(mOnItemClickListener);
	}
	
	//UI callback
	private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			dismiss();
			String targetDeviceName = mAirkanDevicesAdapter.getItem(position);
			if(Util.isEmpty(targetDeviceName) || mAirkanManager == null) {
				return;
			}
			if(targetDeviceName.equals(mAirkanManager.getPlayingDeviceName())) {
				return;
			}
			if(mAirkanManager.isPlayingInLocal()) {
            	mAirkanManager.playToDevice(targetDeviceName);
            } else {
            	if(targetDeviceName.equals(AirkanManager.AIRKAN_DEVICE_XIAOMI_PHONE)) {
            		mAirkanManager.takebackToPhone();
            	} else {
            		mAirkanManager.playToDevice(targetDeviceName);
            	}
            }
		}
	};

	@Override
	public int getGravity() {
		return Gravity.RIGHT;
	}

	@Override
	public int getAnimationStyle() {
		return R.style.rightmenu_popup_anim_style;
	}
}
