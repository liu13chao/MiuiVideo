package com.miui.video;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.miui.video.adapter.MediaListHAdapter;
import com.miui.video.base.BaseTitleActivity;
import com.miui.video.controller.PlaySession;
import com.miui.video.storage.BaseDevice;
import com.miui.video.storage.DLNAMediaManager;
import com.miui.video.storage.DLNAMediaManager.MediaUpdateListener;
import com.miui.video.storage.DeviceManager;
import com.miui.video.storage.MediaItem;
import com.miui.video.widget.ListViewEx;
import com.miui.video.widget.LoadingListView;

/**
 *@author tangfuling
 *
 */

public class ShareDLNAMediaActivity extends BaseTitleActivity {

	public static final String KEY_DEVICE_NAME = "key_device_name";
	
	//UI
	private LoadingListView mLoadingListView;
	private ListViewEx mListView;
	private View mEmptyView;
	private View mLoadingView;
	private MediaListHAdapter mAdapter;
	
	//manager
	private DeviceManager mDeviceManager;
	private DLNAMediaManager mDLNAMediaManager;
	
	//received data
	private String mDeviceName;
	private BaseDevice mBaseDevice;
	
	//data
	private List<MediaItem> mMediaItems = new ArrayList<MediaItem>();
	
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
		mDLNAMediaManager.removeListener(mMediaUpdateListener);
	}
	
	//init
	private void init() {
		initManager();
		initReceivedData();
		initUI();
	}
	
	private void initManager() {
		mDeviceManager = DKApp.getSingleton(DeviceManager.class);
		mDLNAMediaManager = DKApp.getSingleton(DLNAMediaManager.class);
		mDLNAMediaManager.addListener(mMediaUpdateListener);
	}
	
	private void initReceivedData() {
		Intent intent = getIntent();
		mDeviceName = intent.getStringExtra(KEY_DEVICE_NAME);
		mBaseDevice = mDeviceManager.findDeviceByName(mDeviceName);
	}
	
	private void initUI() {
		initListView();
	}
	
	private void initListView() {
		setTopTitle(mDeviceName);
		
		mLoadingListView = (LoadingListView) findViewById(R.id.com_media_list);
		mListView = mLoadingListView.getListView();
		int paddingLeft = getResources().getDimensionPixelSize(R.dimen.page_margin);
		int paddingTop = getResources().getDimensionPixelSize(R.dimen.page_margin_top);
		mListView.setPadding(paddingLeft, paddingTop,  paddingLeft, paddingTop);
		mListView.setVerticalScrollBarEnabled(false);
		mListView.setSelector(R.drawable.transparent);
		mListView.setClipToPadding(false);
		
		mAdapter = new MediaListHAdapter(this);
		mAdapter.setOnItemClickListener(mOnItemClickListener);
		mListView.setAdapter(mAdapter);
		
		mListView.setSelector(R.drawable.transparent);
		mListView.setVerticalScrollBarEnabled(false);
		mListView.setAdapter(mAdapter);
		
		mLoadingView = View.inflate(this, R.layout.load_view, null);
		mLoadingListView.setLoadingView(mLoadingView);
		
		mEmptyView = View.inflate(this, R.layout.empty_view_media, null);
		TextView emptyTitle = (TextView) mEmptyView.findViewById(R.id.empty_title);
		emptyTitle.setText(getResources().getString(R.string.device_media_empty_hint));
		ImageView emptyIcon = (ImageView) mEmptyView.findViewById(R.id.empty_icon);
		emptyIcon.setBackgroundResource(R.drawable.empty_icon_error);
	}
	
	//packaged method
	private void getData() {
		mMediaItems.clear();
		List<MediaItem> mediaItems = mDLNAMediaManager.getMediaItems(mDeviceName);
		if(mediaItems != null) {
			mMediaItems.addAll(mediaItems);
		}
		
		refreshListView();
		if(mMediaItems == null || mMediaItems.size() == 0) {
			mLoadingListView.setShowLoading(true);
		}
		
		mDLNAMediaManager.browseDevice(mBaseDevice);
	}
	
	private void refreshListView() {
		mLoadingListView.setShowLoading(false);
		mAdapter.setGroup(mMediaItems);
		
		if(mMediaItems.size() > 0) {
			return;
		}
		
		int emptyViewTopMargin = getResources().getDimensionPixelSize(R.dimen.video_common_empty_top_margin);
		mLoadingListView.setEmptyView(mEmptyView, emptyViewTopMargin);
	}
	
	//UI callback
	private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Object item = parent.getItemAtPosition(position);
			if(item instanceof MediaItem) {
				MediaItem mediaItem = (MediaItem) item;
				new PlaySession(ShareDLNAMediaActivity.this).startPlayerShareDevice(mediaItem);
			}
		}
	};

	//data callback
	private MediaUpdateListener mMediaUpdateListener = new MediaUpdateListener() {
		
		@Override
		public void onMediaUpdate() {
			mMediaItems.clear();
			List<MediaItem> mediaItems = mDLNAMediaManager.getMediaItems(mDeviceName);
			if(mediaItems != null) {
				mMediaItems.addAll(mediaItems);
			}
			
			refreshListView();
		}
	};

	@Override
	protected int getContentViewRes() {
		return R.layout.com_media_list;
	}
}
