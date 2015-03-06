
package com.miui.videoplayer.framework.popup;

import java.util.List;

import android.R.color;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Button;
import android.widget.TextView;

import com.miui.video.R;
import com.miui.videoplayer.framework.airkan.AirkanManager;
import com.miui.videoplayer.framework.milink.IDeviceDiscoveryListener;

public class VpCtrlAirKanDevicesPopupWindow extends PauseMediaPlayerPopupWindow implements
        IDeviceDiscoveryListener, IAirKanDevicesPopupWindow {
    private static final String TAG = VpCtrlAirKanDevicesPopupWindow.class.getSimpleName();

    private static final int MESSAGE_WHAT_QUERY_DEVICE_LIST = 0;

    private Context mContext;
    private ImageView mPhoneImageView;
    private ViewGroup mDeviceContainer;
    private AirkanManager mAirKanManager;

    private TextView mPhoneNameTextView;
    private Uri mVideoUri;

    public VpCtrlAirKanDevicesPopupWindow(Context context, AirkanManager airkanManager) {
        super(context, LayoutInflater.from(context).inflate(
                R.layout.vp_popup_bottom_airkan_device_selection, null));

		int width = (int) context.getResources().getDimension(R.dimen.vp_ctrl_airkan_pop_width);
		this.setWidth(width);
		this.setHeight(LayoutParams.WRAP_CONTENT);

        this.mContext = context;
        this.mAirKanManager = airkanManager;
        // this.mVideoUri = videoUri;

        setupViews();
    }

    public void setVideoUri(Uri videoUri) {
        this.mVideoUri = videoUri;
    }

    private void setupViews() {
        this.setTouchable(true);
        // this.setFocusable(true);
        this.setOutsideTouchable(true);
        this.setBackgroundDrawable(new ColorDrawable());

        ViewGroup rootView = (ViewGroup) getContentView();
        mPhoneImageView = (ImageView) rootView.findViewById(R.id.airkan_device_imageview);
        mPhoneImageView.setOnClickListener(mPhoneImageOnClickListener);

        String xiaomiPhoneName = mContext.getResources().getString(
                R.string.airkan_device_xiaomi_phone);
        mPhoneNameTextView = (TextView) rootView.findViewById(R.id.airkan_device_name_textview);
        mPhoneNameTextView.setText(xiaomiPhoneName);

        mDeviceContainer = (ViewGroup) rootView.findViewById(R.id.device_imageview_container);
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        updatePhoneImageViewBackground();
        removeRemoteDeviceImageViews();
        updateRemoteDeviceList();
        super.showAtLocation(parent, gravity, x, y);
        mAirKanManager.registeOnDeviceChangeListener(this);
    }

    @Override
    public void dismiss() {
        mAirKanManager.unregisteOnDeviceChangeListener(this);
        super.dismiss();
    }

    private void updateRemoteDeviceList() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                List<String> deviceNameList = mAirKanManager.queryAirkanDevices();
                Log.i(TAG, "found device number: " + deviceNameList.size());
                Message m = Message.obtain();
                m.what = MESSAGE_WHAT_QUERY_DEVICE_LIST;
                m.obj = deviceNameList;
                mHandler.sendMessage(m);
            }
        }).start();
    }

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MESSAGE_WHAT_QUERY_DEVICE_LIST) {
                List<String> deviceNameList = (List<String>) msg.obj;
                removeRemoteDeviceImageViews();
                addRemoteDeviceImageViews(deviceNameList);
            }
        }

    };

    private void updatePhoneImageViewBackground() {
        if (mAirKanManager.isPlayingInLocal()) {
            mPhoneImageView.setImageResource(R.drawable.vp_airkan_mobile_selected);
            mPhoneNameTextView.setTextColor(mContext.getResources().getColor(
                    R.color.listview_item_selected_color));
        } else {
            mPhoneImageView.setImageResource(R.drawable.vp_airkan_xiaomi_phone_imageview);
            mPhoneNameTextView.setTextColor(Color.WHITE);
        }
    }

    private void addRemoteDeviceImageViews(List<String> deviceNameList) {
        // Log.e("ADD COUNT: ", deviceNameList.size() + "");
        // deviceNameList.add("1");
        // deviceNameList.add("2");
        // deviceNameList.add("3");
        // deviceNameList.add("4");
        // deviceNameList.add("5");
        // deviceNameList.add("6");
        int leftMargin = (int) mContext.getResources().getDimension(
                R.dimen.popup_bottom_airkan_device_item_margin_left);
        for (int i = 0; i < deviceNameList.size(); i++) {
            View deviceViewGroup = LayoutInflater.from(mContext).inflate(
                    R.layout.vp_airkan_device_scrollview_item, null);
            TextView textView = (TextView) deviceViewGroup
                    .findViewById(R.id.airkan_device_name_textview);
            Log.i(TAG, "device name : " + deviceNameList.get(i) + "");
            textView.setText(deviceNameList.get(i));
            ImageView imageView = (ImageView) deviceViewGroup
                    .findViewById(R.id.airkan_device_imageview);
            imageView.setImageResource(R.drawable.vp_airkan_dktv_imageview);
            if (!mAirKanManager.isPlayingInLocal()
                    && mAirKanManager.getPlayingDeviceName().equals(deviceNameList.get(i))) {
                imageView.setImageResource(R.drawable.vp_airkan_dktv_selected);
                textView.setTextColor(mContext.getResources().getColor(color.holo_orange_light));
            } else {
                imageView.setImageResource(R.drawable.vp_airkan_dktv_imageview);
                textView.setTextColor(Color.WHITE);
            }
            imageView.setTag(deviceNameList.get(i));
            imageView.setOnClickListener(mAirkanDeviceOnClickListener);

            LayoutParams layoutParam = new LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.MATCH_PARENT);
            layoutParam.leftMargin = leftMargin;

            mDeviceContainer.addView(deviceViewGroup, layoutParam);
            mDeviceContainer.requestLayout();
        }
    }

    private void removeRemoteDeviceImageViews() {
        int childCount = mDeviceContainer.getChildCount();
        if (childCount > 1) {
            mDeviceContainer.removeViews(1, childCount - 1);
            mDeviceContainer.requestLayout();
        }
    }

    private OnClickListener mPhoneImageOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (!mAirKanManager.isPlayingInLocal()) {
                mAirKanManager.takebackToPhone();
            }
            dismiss();
        }
    };

    private OnClickListener mAirkanDeviceOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            String targetDeviceName = (String) v.getTag();
            if (mAirKanManager.isPlayingInLocal()) {
                mAirKanManager.playToDevice(targetDeviceName, mVideoUri);
                dismiss();
            } else {
                Log.i(TAG, "device name: " + mAirKanManager.getPlayingDeviceName());
                if (!mAirKanManager.getPlayingDeviceName().equals(targetDeviceName)) {
                    mAirKanManager.takebackToPhone();
                    dismiss();
                    mAirKanManager.playToDevice(targetDeviceName, mVideoUri);
                }
            }
            // dismiss();
        }

    };

    private void refresh() {
        removeRemoteDeviceImageViews();
        updateRemoteDeviceList();
    }

    @Override
    public void onOpened() {
        Log.i(TAG, "device opened ");
    }

    @Override
    public void show(View anchor) {
        showAtLocation(anchor, Gravity.BOTTOM, 0, 0);
    }

    @Override
    public void onDeviceAdded(String newDevice) {
        Log.i(TAG, "device added " + newDevice);
        refresh();
    }

    @Override
    public void onDeviceRemoved(String removedDevice) {
        Log.i(TAG, "device removed " + removedDevice);
        refresh();
    }

}
