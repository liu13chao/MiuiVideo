package com.miui.video;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.miui.video.adapter.BaseMediaListAdapter;
import com.miui.video.adapter.GridRowInfoAdapter;
import com.miui.video.base.BaseMediaListActivity;
import com.miui.video.controller.content.SharedDeviceContentBuilder;
import com.miui.video.storage.BaseDevice;
import com.miui.video.storage.DLNADevice;
import com.miui.video.storage.DLNAMediaManager;
import com.miui.video.storage.DLNAMediaManager.MediaUpdateListener;
import com.miui.video.storage.DeviceManager;
import com.miui.video.storage.DeviceManager.DeviceObserver;
import com.miui.video.type.BaseMediaInfo;
import com.miui.video.util.Util;
import com.miui.video.widget.EmptyView;

/**
 *@author tangfuling
 *
 */

public class ShareDeviceActivity extends BaseMediaListActivity {
	
	//UI
//	private LoadingListView mLoadingListView;
//	private ListViewEx mListView;
//	private View mEmptyView;
//	private View mLoadingView;
	private  GridRowInfoAdapter<BaseDevice> mAdapter;
	
	//data
	private List<BaseDevice> mAllDevices = new ArrayList<BaseDevice>();
	
	private DeviceManager mDeviceManager;
	private DLNAMediaManager mDLNAMediaManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		getData();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mDeviceManager.removeObserver(mDeviceObserver);
		mDLNAMediaManager.removeListener(mMediaUpdateListener);
	}
	
	//init
	private void init() {
		initManager();
//		initUI();
	}
	
	private void initManager() {
		mDeviceManager = DKApp.getSingleton(DeviceManager.class);
		mDeviceManager.addObserver(mDeviceObserver);
		mDLNAMediaManager = DKApp.getSingleton(DLNAMediaManager.class);
		mDLNAMediaManager.addListener(mMediaUpdateListener);
	}
	
//	private void initUI() {
//		initListView();
//	}
	
//	private void initListView() {
//		setTopTitle(R.string.share_device);
//		mLoadingListView = (LoadingListView) findViewById(R.id.com_media_list);
//		mListView = mLoadingListView.getListView();
//		int height = (int) getResources().getDimension(R.dimen.video_common_list_top_padding);
//		mListView.setPadding(0, height, 0, 0);
//		mListView.setClipToPadding(false);
//		mAdapter = new GridRowInfoAdapter<BaseDevice>(this, 2, R.layout.media_view_grid_h);
//		mAdapter.setMediaViewClickListener(mClickListener);
//		mAdapter.setMediaContentBuilder(new SharedDeviceContentBuilder(this));
//		mListView.setAdapter(mAdapter);
//		mListView.setSelector(R.drawable.transparent);
//		mListView.setVerticalScrollBarEnabled(false);
//		mListView.setAdapter(mAdapter);
//		mLoadingView = View.inflate(this, R.layout.load_view, null);
//		mLoadingListView.setLoadingView(mLoadingView);
//		
//		mEmptyView = View.inflate(this, R.layout.empty_view_media, null);
//		TextView emptyTitle = (TextView) mEmptyView.findViewById(R.id.empty_title);
//		emptyTitle.setText(getResources().getString(R.string.device_empty_hint));
//		ImageView emptyIcon = (ImageView) mEmptyView.findViewById(R.id.empty_icon);
//		emptyIcon.setBackgroundResource(R.drawable.empty_icon_error);
//	}
	
	//packaged method
	private void getData() {
		getDevices();
	}
	
	private void getDevices() {
		mAllDevices.clear();
		List<BaseDevice> devices = mDeviceManager.getDevices();
		if(devices != null) {
			mAllDevices.addAll(devices);
		}
		refreshListView();
		
		mDeviceManager.scan();
		if(mAllDevices.size() == 0) {
			mLoadingListView.setShowLoading(true);
		}
	}
	
	private void refreshListView() {
		mLoadingListView.setShowLoading(false);
		
		mAllDevices.clear();
		List<BaseDevice> devices = mDeviceManager.getDevices();
		if(devices != null) {
			mAllDevices.addAll(devices);
		}
		mAdapter.setDataList(mAllDevices);
		if(mAllDevices.size() > 0) {
			return;
		}
		
		int emptyViewTopMargin = getResources().getDimensionPixelSize(R.dimen.video_common_empty_top_margin);
		mLoadingListView.setEmptyView(mEmptyView, emptyViewTopMargin);
	}
	
	private void startShareDLNAMediaActivity(String deviceName) {
		if(!Util.isEmpty(deviceName)) {
			Intent intent = new Intent();
			intent.putExtra(ShareDLNAMediaActivity.KEY_DEVICE_NAME, deviceName);
			intent.setClass(this, ShareDLNAMediaActivity.class);
			startActivity(intent);
		}
	}
	
//	//UI callback
//	private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
//
//		@Override
//		public void onItemClick(AdapterView<?> parent, View view, int position,
//				long id) {
//		}
//	};
		
	//data callback
	private DeviceObserver mDeviceObserver = new DeviceObserver() {
		
		@Override
		public void onDeviceRemoved(BaseDevice device) {
			refreshListView();
		}
		
		@Override
		public void onDeviceAdded(BaseDevice device) {
			mDLNAMediaManager.browseDevice(device);
			refreshListView();
		}
	};
	
	private MediaUpdateListener mMediaUpdateListener = new MediaUpdateListener() {
		
		@Override
		public void onMediaUpdate() {
			refreshListView();
		}
	};

    @Override
    protected CharSequence getPageTitle() {
        return getResources().getString(R.string.share_device);
    }

    @Override
    protected void onItemClick(BaseMediaInfo mediaInfo) {
        if(mediaInfo instanceof DLNADevice) {
            DLNADevice dlnaDevice = (DLNADevice) mediaInfo;
            startShareDLNAMediaActivity(dlnaDevice.getName());
        }
    }

    @Override
    protected void onItemLongClick(BaseMediaInfo mediaInfo) {
    }

    @Override
    protected BaseMediaListAdapter<?> initListAdapter() {
        mAdapter = new GridRowInfoAdapter<BaseDevice>(this, 2, R.layout.media_view_grid_h);
        mAdapter.setMediaContentBuilder(new SharedDeviceContentBuilder(this));
//        mAdapter.setMediaViewClickListener(mClickListener);
        return mAdapter;
    }

    @Override
    protected View getEmptyView() {
        return new EmptyView(this, R.string.device_empty_hint, R.drawable.empty_icon_error);
    }
}
