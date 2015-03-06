
package com.miui.videoplayer.framework.popup;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.miui.video.R;
import com.miui.videoplayer.framework.airkan.AirkanManager;
import com.miui.videoplayer.framework.milink.IDeviceDiscoveryListener;

public class VpCtrlAirKanDevicesPopupWindowV5 extends PauseMediaPlayerPopupWindow implements
        IDeviceDiscoveryListener, IAirKanDevicesPopupWindow {
    private static final String TAG = VpCtrlAirKanDevicesPopupWindowV5.class.getSimpleName();

    private static final int MESSAGE_WHAT_QUERY_DEVICE_LIST = 0;

    private AirkanManager mAirKanManager;
    private Uri mVideoUri;
    private Context mContext;

    private ListView mListView;

    private int mItemCount = 0;

    public VpCtrlAirKanDevicesPopupWindowV5(Context context, AirkanManager airkanManager) {
        super(context, LayoutInflater.from(context).inflate(
                R.layout.vp_popup_ctrl_airkan_device_v5, null));

        this.mContext = context;
        this.mAirKanManager = airkanManager;
        int width = (int) context.getResources().getDimension(R.dimen.vp_ctrl_airkan_pop_width);
        this.setWidth(width);
        this.setHeight(LayoutParams.WRAP_CONTENT);
        
        setupViews();
    }

    private void setupViews() {
        this.setTouchable(true);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        this.setBackgroundDrawable(new ColorDrawable());

        View contentView = this.getContentView();
        
        TextView topTitleName = (TextView) contentView.findViewById(R.id.vp_popup_ctrl_top_title_name);
        topTitleName.setText(R.string.vp_device_list);
        
        mListView = (ListView) contentView.findViewById(R.id.airkan_devices_selection_listview);

        // String[] values = new String[] {"1", "2", "3", "4", "5", "6"};
        // ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext,
        // R.layout.vp_airkan_devices_selection_listview_item_v5,
        // R.id.airkan_devices_selection_listview_item_textview, values);
        // mListView.setAdapter(adapter);
        mListView.setSelector(R.drawable.vp_list_item_bg);
        mListView.setOnItemClickListener(mOnItemClickListener);
    }

    private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.e(TAG, "OnItemClickListener");
            if (position == 0 && !mAirKanManager.isPlayingInLocal()) {
                mAirKanManager.takebackToPhone();
            }
            if (position != 0) {
                TextView textView = (TextView) view
                        .findViewById(R.id.vp_ctrl_airkan_device_title);
                String targetDeviceName = textView.getText().toString();
                if (mAirKanManager.isPlayingInLocal()) {
                    mAirKanManager.playToDevice(targetDeviceName, mVideoUri);
                } else {
                    Log.i(TAG, "device name: " + mAirKanManager.getPlayingDeviceName());
                    if (!mAirKanManager.getPlayingDeviceName().equals(targetDeviceName)) {
                        mAirKanManager.takebackToPhone();
                        mAirKanManager.playToDevice(targetDeviceName, mVideoUri);
                    }
                }
            }
            dismiss();
        }

    };

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        // updatePhoneImageViewBackground();
        // removeRemoteDeviceImageViews();
        // updateRemoteDeviceList();
        refresh();
        this.setAnimationStyle(R.style.menu_popup_anim_style);
        super.showAtLocation(parent, Gravity.LEFT, x, y);
        mAirKanManager.registeOnDeviceChangeListener(this);
    }

    /***
     * 根据设备数量调节弹出窗口大小
     */
    public int getPopWinHeight()
    {
        int height = (int) mContext.getResources().getDimension(
                R.dimen.popup_left_menu_option_width);

        int listItemHeight = (int) mContext.getResources().getDimension(
                R.dimen.popup_left_menu_option_listview_item_height_v5);
        int divideHeight = (int) mContext.getResources().getDimension(
                R.dimen.popup_left_menu_option_listview_item_divider_height);
        int maxcount = 4;// UI list中最多同时显示4个item；
        if (mItemCount < maxcount) {
            height = height - (maxcount - mItemCount) * (listItemHeight + divideHeight);
        }
        return height;
    }

    @Override
    public void dismiss() {
        mAirKanManager.unregisteOnDeviceChangeListener(this);
        super.dismiss();
    }

    @Override
    public void onOpened() {
        // refresh();
    }

    private void refresh() {
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

    private Handler mHandler = new MyHandler(this);

    // private Handler mHandler = new Handler(){
    //
    // @Override
    // public void handleMessage(Message msg) {
    // if (msg.what == MESSAGE_WHAT_QUERY_DEVICE_LIST) {
    // List<String> deviceNameList = (List<String>) msg.obj;
    // // addRemoteDeviceImageViews(deviceNameList);
    // String xiaomiPhoneName =
    // mContext.getResources().getString(R.string.airkan_device_xiaomi_phone);
    // deviceNameList.add(0, xiaomiPhoneName);
    // ArrayAdapter<String> adapter = new
    // AirkanDevicesArrayAdapter(deviceNameList);
    // mListView.setAdapter(adapter);
    // mItemCount = deviceNameList.size();
    //
    //
    // }
    // }
    // };
    private class MyHandler extends Handler {

        private VpCtrlAirKanDevicesPopupWindowV5 mAirKanDevicesPopupWindowV5;

        public MyHandler() {
        }

        public MyHandler(VpCtrlAirKanDevicesPopupWindowV5 airKanDevicesPopupWindowV5) {
            mAirKanDevicesPopupWindowV5 = airKanDevicesPopupWindowV5;
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MESSAGE_WHAT_QUERY_DEVICE_LIST) {
                List<String> deviceNameList = (List<String>) msg.obj;
                // addRemoteDeviceImageViews(deviceNameList);
                String xiaomiPhoneName = mContext.getResources().getString(
                        R.string.airkan_device_xiaomi_phone);
                deviceNameList.add(0, xiaomiPhoneName);
                AirkanDeviceAdapter adapter = new AirkanDeviceAdapter(mContext, deviceNameList);
                mItemCount = deviceNameList.size();
                /*
                if (DisplayInformationFetcher.getInstance(mContext).getScreenOrientation() == DisplayInformationFetcher.SCREEN_PORT) {
                    int height = mAirKanDevicesPopupWindowV5.getPopWinHeight();
                    mAirKanDevicesPopupWindowV5.update(LayoutParams.MATCH_PARENT, height);
                }
                */
                mListView.setAdapter(adapter);

            }
        }
    }
    
	private class AirkanDeviceAdapter extends BaseAdapter {
		
		private Context context;
		private List<String> items;
		
		public AirkanDeviceAdapter(Context context, List<String> items) {
			this.context = context;
			this.items = items;
		}
		
		private class ViewHolder {
			private TextView  itemTv;
		}
		
		@Override
		public String getItem(int position) {
			if(items != null && position < items.size()) {
				return items.get(position);
			}
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}
		
		@Override
		public int getCount() {
			if(items != null) {
				return items.size();
			}
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder vHolder = null;
			if (convertView == null) {
				vHolder = new ViewHolder();
				convertView = View.inflate(context, R.layout.vp_popup_ctrl_airkan_device_item, null);
				vHolder.itemTv = (TextView) convertView.findViewById(R.id.vp_ctrl_airkan_device_title);
				convertView.setTag(vHolder);
			} else {
				vHolder = (ViewHolder) convertView.getTag();
			}
			String item = getItem(position);
			if(item != null) {
				vHolder.itemTv.setText(item);
			}
            if ((position == 0 && mAirKanManager.isPlayingInLocal()) ||
                    (mAirKanManager.getPlayingDeviceName() != null && item != null 
                    && mAirKanManager.getPlayingDeviceName().equals(item))) {
            	vHolder.itemTv.setTextColor(mContext.getResources().getColor(R.color.vp_90_blue));
            } else {
                vHolder.itemTv.setTextColor(mContext.getResources().getColor(R.color.vp_90_white));
            }
			return convertView;
		}
	}

    @Override
    public void setVideoUri(Uri uri) {
        mVideoUri = uri;
    }

    @Override
    public void show(View anchor) {
        showAtLocation(anchor, Gravity.LEFT, 0, 0);
    }

    @Override
    public void onDeviceAdded(java.lang.String newDevice) {
        refresh();
    }

    @Override
    public void onDeviceRemoved(java.lang.String removedDevice) {
        refresh();
    }
}
