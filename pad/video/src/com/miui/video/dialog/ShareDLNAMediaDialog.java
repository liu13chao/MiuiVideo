package com.miui.video.dialog;

import java.util.ArrayList;
import java.util.List;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.miui.video.DKApp;
import com.miui.video.R;
import com.miui.video.adapter.MediaViewListAdapter;
import com.miui.video.base.BaseDialog;
import com.miui.video.common.PlaySession;
import com.miui.video.storage.BaseDevice;
import com.miui.video.storage.DLNAMediaManager;
import com.miui.video.storage.DeviceManager;
import com.miui.video.storage.MediaItem;
import com.miui.video.storage.DLNAMediaManager.MediaUpdateListener;
import com.miui.video.widget.ListViewEx;
import com.miui.video.widget.LoadingListView;
import com.miui.video.widget.media.MediaView;
import com.miui.video.widget.media.MediaView.OnMediaClickListener;

/**
 *@author tangfuling
 *
 */

public class ShareDLNAMediaDialog extends BaseDialog {

	public static final String KEY_DEVICE_NAME = "key_device_name";
	
	//UI
	private TextView mTitleTop; 
	private LoadingListView mLoadingListView;
	private ListViewEx mListViewEx;
	private View mEmptyView;
	private View mLoadingView;
	private MediaViewListAdapter mAdapter;

	private Button mBtnMore;
	
	//manager
	private DeviceManager mDeviceManager;
	private DLNAMediaManager mDLNAMediaManager;
	
	//received data
	private String mDeviceName;
	private BaseDevice mBaseDevice;
	
	private int mPosterW;
	private int mPosterH;
	
	//data
	private List<Object> mMediaItems = new ArrayList<Object>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.local_detail);
		
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
		initDimen();
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
		mTitleTop = (TextView) findViewById(R.id.local_detail_title);
		mTitleTop.setText(mDeviceName);
		mBtnMore = (Button) findViewById(R.id.local_detail_more);
		mBtnMore.setVisibility(View.GONE);
		
		initListView();
	}
	
	private void initDimen() {
		mPosterW = this.getResources().getDimensionPixelSize(R.dimen.local_detail_media_width);
		mPosterH = this.getResources().getDimensionPixelSize(R.dimen.local_detail_media_height);
	}
	
	private void initListView() {
		mLoadingListView = (LoadingListView) findViewById(R.id.local_detail_list);
		FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mLoadingListView.getLayoutParams();
		params.bottomMargin = getResources().getDimensionPixelSize(R.dimen.local_detail_list_bottom_margin);
		mLoadingListView.setLayoutParams(params);
		mListViewEx = mLoadingListView.getListView();
		mAdapter = new MediaViewListAdapter(this);
		mAdapter.setOnMediaClickListener(mOnMediaClickListener);
		mListViewEx.setAdapter(mAdapter);
		int nameViewColor = getResources().getColor(R.color.p_80_black);
		int statusViewColor = getResources().getColor(R.color.p_50_black);
		mAdapter.setInfoViewColor(nameViewColor, statusViewColor);
		mAdapter.setPosterSize(mPosterW, mPosterH);
		
		mLoadingView = View.inflate(this, R.layout.load_view, null);
		mLoadingListView.setLoadingView(mLoadingView);
		
		mEmptyView = View.inflate(this, R.layout.empty_view_media, null);
		TextView emptyHint = (TextView) mEmptyView.findViewById(R.id.empty_hint);
		emptyHint.setText(getResources().getString(R.string.device_media_empty_hint));
		ImageView emptyIcon = (ImageView) mEmptyView.findViewById(R.id.empty_icon);
		emptyIcon.setBackgroundResource(R.drawable.empty_icon_media);
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
	
	//packaged method
	private void refreshListView() {
		mLoadingListView.setShowLoading(false);
		mAdapter.setGroup(mMediaItems);
		
		if(mMediaItems.size() > 0) {
			return;
		}
		
		mLoadingListView.setEmptyView(mEmptyView);
	}
	
	//UI callback
	private OnMediaClickListener mOnMediaClickListener = new OnMediaClickListener() {
		
		@Override
		public void onMediaClick(MediaView mediaView, Object media) {
			if(media instanceof MediaItem) {
				MediaItem mediaItem = (MediaItem) media;
				PlaySession playSession = DKApp.getSingleton(PlaySession.class);
				playSession.startPlayerShareDevice(mediaItem);
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
}
