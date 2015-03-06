package com.miui.video.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.miui.video.DKApp;
import com.miui.video.R;
import com.miui.video.adapter.MediaViewListAdapter;
import com.miui.video.dialog.ShareDLNAMediaDialog;
import com.miui.video.storage.BaseDevice;
import com.miui.video.storage.DLNADevice;
import com.miui.video.storage.DLNAMediaManager;
import com.miui.video.storage.DeviceManager;
import com.miui.video.storage.DLNAMediaManager.MediaUpdateListener;
import com.miui.video.storage.DeviceManager.DeviceObserver;
import com.miui.video.util.Util;
import com.miui.video.widget.ListViewEx;
import com.miui.video.widget.LoadingListView;
import com.miui.video.widget.media.MediaView;
import com.miui.video.widget.media.MediaView.OnMediaClickListener;

/**
 *@author dz
 *
 */

public class SharedDevicesFragment extends Fragment {
	
	private Context mContext;
	
	//UI
	private View mContentView;	
	private MediaViewListAdapter mMediaViewListAdapter;
	
	private LoadingListView mLoadingListView;
	private ListViewEx mListView;
	private View mLoadingView;
	private View mEmptyView;
		
	//data
	private List<Object> mAllDevices = new ArrayList<Object>();
	
	private DeviceManager mDeviceManager;
	private DLNAMediaManager mDLNAMediaManager;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		mContentView = inflater.inflate(R.layout.local_list_view, null);
		return mContentView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mContext = getActivity();
		init();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		getData();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		mDeviceManager.removeObserver(mDeviceObserver);
		mDLNAMediaManager.removeListener(mMediaUpdateListener);
	}
	
	//screen change
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		refreshListView();
	}
	
	//init
	private void init() {
		initManagers();
		initUI();
	}
	
	
	private void initUI() {
		initLocalListView();
	}

	private void initLocalListView() {
		mLoadingListView = (LoadingListView) mContentView.findViewById(R.id.loading_list);
		mListView = mLoadingListView.getListView();
		mListView.setVerticalFadingEdgeEnabled(true);
		mListView.setFadingEdgeLength(getResources().getDimensionPixelSize(R.dimen.video_fade_edge_length));
		
		mMediaViewListAdapter = new MediaViewListAdapter(mContext);
		mMediaViewListAdapter.setOnMediaClickListener(mOnMediaClickListener);
		mListView.setAdapter(mMediaViewListAdapter);
		
		mLoadingView = View.inflate(mContext, R.layout.load_view, null);
		mLoadingListView.setLoadingView(mLoadingView);
		
		mEmptyView = View.inflate(mContext, R.layout.empty_view_media, null);
		TextView emptyHint = (TextView) mEmptyView.findViewById(R.id.empty_hint);
		emptyHint.setText(getResources().getString(R.string.device_empty_hint));
		ImageView emptyIcon = (ImageView) mEmptyView.findViewById(R.id.empty_icon);
		emptyIcon.setBackgroundResource(R.drawable.empty_icon_media);		
	}

	private void initManagers() {
		mDeviceManager = DKApp.getSingleton(DeviceManager.class);
		mDeviceManager.addObserver(mDeviceObserver);
		mDLNAMediaManager = DKApp.getSingleton(DLNAMediaManager.class);
		mDLNAMediaManager.addListener(mMediaUpdateListener);
	}
		
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
		mMediaViewListAdapter.setGroup(mAllDevices);
		
		if(mAllDevices.size() > 0) {
			return;
		}

		mLoadingListView.setEmptyView(mEmptyView);
	}
	
	private void startShareDLNAMediaDialog(String deviceName) {
		if(!Util.isEmpty(deviceName)) {
			Intent intent = new Intent();
			intent.putExtra(ShareDLNAMediaDialog.KEY_DEVICE_NAME, deviceName);
			intent.setClass(mContext, ShareDLNAMediaDialog.class);
			startActivity(intent);
		}
	}
	
	//UI callback
	private OnMediaClickListener mOnMediaClickListener = new OnMediaClickListener() {
		
		@Override
		public void onMediaClick(MediaView mediaView, Object media) {
			if(media instanceof DLNADevice) {
				DLNADevice dlnaDevice = (DLNADevice) media;
				startShareDLNAMediaDialog(dlnaDevice.getName());
			}
		}
	};
	
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
}
