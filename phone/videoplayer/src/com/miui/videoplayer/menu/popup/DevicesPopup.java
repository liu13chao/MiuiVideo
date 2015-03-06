/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   DevicesPopup.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-12-1
 */
package com.miui.videoplayer.menu.popup;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import com.miui.video.R;
import com.miui.video.util.Util;
import com.miui.videoplayer.adapter.AirkanDevicesAdapter;
import com.miui.videoplayer.framework.airkan.AirkanManager;

import java.util.List;

/**
 * @author tianli
 *
 */
public class DevicesPopup extends BaseMenuPopup {

    public AirkanManager mAirkanManager;

    private AirkanDevicesAdapter mDevicesAdapter;

    public DevicesPopup(Context context, AirkanManager airkanManager) {
        super(context);
        mAirkanManager = airkanManager;
        mDevicesAdapter = new AirkanDevicesAdapter(getContext());
        mListView.setAdapter(mDevicesAdapter);
        mListView.setOnItemClickListener(mOnItemClickListener);
        setTitle(getResources().getString(R.string.vp_device_list));
    }

    private void initDevices(){
        if(mAirkanManager != null){
            List<String> devices = mAirkanManager.queryAirkanDevices();
            String xiaomiPhoneName = getContext().getResources().getString(
                    R.string.airkan_device_xiaomi_phone);
            devices.add(0, xiaomiPhoneName);
            mDevicesAdapter.setGroup(devices);
            mDevicesAdapter.setPlayingDeviceName(mAirkanManager.getPlayingDeviceName());
        }
    }

    @Override
    public void show(ViewGroup anchor) {
        super.show(anchor);
        initDevices();
    }

    @Override
    protected int getPopupWidth() {
        return getContext().getResources().getDimensionPixelSize(
                R.dimen.vp_menu_popup_variety_width);
    }

    //UI callback
    private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                long id) {
            dismiss();
            String targetDeviceName = mDevicesAdapter.getItem(position);
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
}
