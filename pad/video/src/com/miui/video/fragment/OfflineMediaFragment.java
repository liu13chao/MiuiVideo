package com.miui.video.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.miui.video.DKApp;
import com.miui.video.R;
import com.miui.video.adapter.MediaViewListAdapter;
import com.miui.video.api.def.MediaConstantsDef;
import com.miui.video.base.EditableFragment;
import com.miui.video.common.PlaySession;
import com.miui.video.dialog.LocalDetailDialog;
import com.miui.video.dialog.OfflineMediaDetailDialog;
import com.miui.video.offline.OfflineMedia;
import com.miui.video.offline.OfflineMediaList;
import com.miui.video.offline.OfflineMediaManager;
import com.miui.video.offline.OfflineMediaManager.OfflineMediaListener;
import com.miui.video.type.LocalMedia;
import com.miui.video.type.LocalMediaList;
import com.miui.video.util.DKLog;
import com.miui.video.widget.ListViewEx;
import com.miui.video.widget.LoadingListView;
import com.miui.video.widget.actionmode.ActionModeBottomMenu;
import com.miui.video.widget.actionmode.ActionModeBottomMenuItem;
import com.miui.video.widget.actionmode.ActionModeView;
import com.miui.video.widget.media.MediaView;
import com.miui.video.widget.media.MediaView.OnMediaClickListener;
import com.miui.video.widget.media.MediaView.OnMediaLongClickListener;

/**
 *@author dz
 *
 */

public class OfflineMediaFragment extends EditableFragment {
	//UI
	private View mContentView;

	private LoadingListView mOfflineLoadingListView;
	private ListViewEx mLocalListView;
//	private View mOfflineLoadingView;
	private View mOfflineEmptyView;
	private MediaViewListAdapter mMediaViewListAdapter;
	
	//data from Offline
	private ArrayList<Object> mOfflineMedias = new ArrayList<Object>();
	
	//manager
	private OfflineMediaManager mOfflineMediaManager;
	
	//tag
	private int mMenuItemDeleteTag = 0;
	
	
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
		init();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		DKLog.d("OfflineMediaFragment", "onResume");
		mOfflineMediaManager.addOfflineMediaListener(mOfflineMediaListener);
		loadOfflineMedia();
		mOfflineLoadingListView.setShowLoading(true);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		mOfflineMediaManager.removeOfflineMediaListener(mOfflineMediaListener);
	}
		
	//init
	private void init() {
		initManagers();
		initUI();
	}
		
	private void initUI() {
		initLocalListView();
		refreshLocalListView();
	}
	
	private void initLocalListView() {
		mOfflineLoadingListView = (LoadingListView) mContentView.findViewById(R.id.loading_list);
		mLocalListView = mOfflineLoadingListView.getListView();
		mLocalListView.setVerticalFadingEdgeEnabled(true);
		mLocalListView.setFadingEdgeLength(getResources().getDimensionPixelSize(R.dimen.video_fade_edge_length));
		
		mMediaViewListAdapter = new MediaViewListAdapter(mActivity);
		mMediaViewListAdapter.setOnMediaClickListener(mOnMediaClickListener);
		mMediaViewListAdapter.setOnMediaLongClickListener(mOnMediaLongClickListener);
		mLocalListView.setAdapter(mMediaViewListAdapter);
		
//		mOfflineLoadingView = View.inflate(mActivity, R.layout.load_view, null);
//		mOfflineLoadingListView.setLoadingView(mOfflineLoadingView);
		mOfflineLoadingListView.setShowLoading(true);
		
		mOfflineEmptyView = View.inflate(mActivity, R.layout.empty_view_media, null);
		TextView emptyHint = (TextView) mOfflineEmptyView.findViewById(R.id.empty_hint);
		emptyHint.setText(getResources().getString(R.string.local_media_empty_hint));
		ImageView emptyIcon = (ImageView) mOfflineEmptyView.findViewById(R.id.empty_icon);
		emptyIcon.setBackgroundResource(R.drawable.empty_icon_media);

		mOfflineLoadingListView.setEmptyView(mOfflineEmptyView);
	}

	private void refreshLocalListView() {
		mOfflineLoadingListView.setShowLoading(false);
		mMediaViewListAdapter.setGroup(mOfflineMedias, mSelectedObject);
		if (mOfflineMedias.size() > 0) {
			mActionModeView.setEnable(true);
		} else {
			mActionModeView.setEnable(false);
		}
	}
	
	private void initManagers() {
		if (mOfflineMediaManager == null) {
			mOfflineMediaManager = DKApp.getSingleton(OfflineMediaManager.class);
		}
	}
	
	private void loadOfflineMedia() {
		mOfflineMediaManager.loadOfflineMedia();
	}

	private void doMediaClick(OfflineMedia offlineMedia) {
		if (offlineMedia.status != MediaConstantsDef.OFFLINE_STATE_FINISH) {
			int offlineMediaStatus = offlineMedia.status;
			switch (offlineMediaStatus) {
			case MediaConstantsDef.OFFLINE_STATE_INIT:
				mOfflineMediaManager.pauseOfflineMedia(offlineMedia.mediaId,
						offlineMedia.episode);
				break;
			case MediaConstantsDef.OFFLINE_STATE_IDLE:
				mOfflineMediaManager.startOfflineMedia(offlineMedia.mediaId,
						offlineMedia.episode);
				break;
				
			case MediaConstantsDef.OFFLINE_STATE_PAUSE:
				mOfflineMediaManager.startOfflineMedia(offlineMedia.mediaId,
						offlineMedia.episode);
				break;
			case MediaConstantsDef.OFFLINE_STATE_LOADING:
				mOfflineMediaManager.pauseOfflineMedia(offlineMedia.mediaId,
						offlineMedia.episode);
				break;
				
			case MediaConstantsDef.OFFLINE_STATE_FILE_ERROR:
			case MediaConstantsDef.OFFLINE_STATE_CONNECT_ERROR:
			case MediaConstantsDef.OFFLINE_STATE_SOURCE_ERROR:
				mOfflineMediaManager.startOfflineMedia(offlineMedia.mediaId,
						offlineMedia.episode);
				break;
			default:
				break;
			}
		} else {
			PlaySession playSession = DKApp.getSingleton(PlaySession.class);
			playSession.startPlayerOffline(offlineMedia);
		}
	}
	
//	private int getMediaTotalCount() {
//		int mediaCount = 0;
//		for(int i = 0; i < mOfflineMedias.size(); i++) {
//			Object obj = mAllMedias.get(i);
//			if (obj instanceof OfflineMediaList) {
//				OfflineMediaList offlineMediaList = (OfflineMediaList) obj;
//				mediaCount += offlineMediaList.size();
//			}
//		}
//		return mediaCount;
//	}
		
	private void deleteSelectedMedias() {
		if (mSelectedObject.size() == mOfflineMedias.size()) {
			mActionModeView.setEnable(false);
		}
		List<Integer> selectedOfflineMediaIds = new ArrayList<Integer>();
		for(int i = 0; i < mSelectedObject.size(); i++) {
			Object object = mSelectedObject.get(i);
			if (object instanceof OfflineMediaList) {
				OfflineMediaList offlineMediaList = (OfflineMediaList) object;
				selectedOfflineMediaIds.add(offlineMediaList.getMediaId());
			}
		}
		mOfflineMediaManager.delOfflineMedias(selectedOfflineMediaIds);
	}

	private void refreshOfflineMedia(OfflineMedia offlineMedia) {
		if (offlineMedia != null) {
			for(int i = 0; i < mOfflineMedias.size(); i++) {
				Object object = mOfflineMedias.get(i);
				if (object instanceof OfflineMedia) {
					OfflineMedia tmp = (OfflineMedia) object;
					if (tmp.episode == offlineMedia.episode) {
						object = offlineMedia;
					}
				}
			}
		}
	}
	
	private void reverseMediaViewSelectStatus(MediaView mediaView, Object media) {
		if (mediaView == null || media == null) {
			return;
		}
		
		boolean isSelected = mediaView.isSelected();
		mediaView.setIsSelected(!isSelected);
		if (media instanceof LocalMediaList) {
			LocalMediaList localMediaList = (LocalMediaList) media;
			if (!isSelected) {
				mSelectedObject.add(localMediaList);
			} else {
				mSelectedObject.remove(localMediaList);
			}
		} else if (media instanceof OfflineMediaList) {
			OfflineMediaList offlineMediaList = (OfflineMediaList) media;
			if (!isSelected) {
				mSelectedObject.add(offlineMediaList);
			} else {
				mSelectedObject.remove(offlineMediaList);
			}
		}
		
		if (mSelectedObject.size() == 0) {
			mActionModeView.setUISelectAll();
		} else if (mSelectedObject.size() == mOfflineMedias.size()) {
			mActionModeView.setUISelectNone();
		} else {
			mActionModeView.setUiSelectPart();
		}
		refreshActionModeViewTitle();
	}
		
	private OfflineMediaListener mOfflineMediaListener = new OfflineMediaListener() {
		
		@Override
		public void onOfflineMediaChanged() {
			mOfflineLoadingListView.setShowLoading(false);
			mOfflineMedias.clear();
			List<Object> list = mOfflineMediaManager.getOfflineMediaList();
			if (list != null) {
				mOfflineMedias.addAll(list);
			}			
			refreshLocalListView();
		}
		
		@Override
		public void onOfflineMediaUpdate(OfflineMedia offlineMedia) {
			refreshOfflineMedia(offlineMedia);
			refreshLocalListView();
		}
	};
	
	private OnMediaClickListener mOnMediaClickListener = new OnMediaClickListener() {
		
		@Override
		public void onMediaClick(MediaView mediaView, Object media) {
			if (mActionModeView != null && mActionModeView.isEdit()) {
				reverseMediaViewSelectStatus(mediaView, media);
			} else {
				if (media instanceof LocalMediaList) {
					LocalMediaList localMediaList = (LocalMediaList) media;
					if (localMediaList.size() > 1) {
						Intent intent = new Intent();
						intent.setClass(mActivity, LocalDetailDialog.class);
						intent.putExtra(LocalDetailDialog.KEY_LOCAL_MEDIA_LIST, localMediaList);
						mActivity.startActivity(intent);
					} else {
						LocalMedia localMedia = localMediaList.get(0);
						if (localMedia != null) {
							PlaySession playSession = DKApp.getSingleton(PlaySession.class);
							playSession.startPlayerLocal(localMedia);
						}
					}
				} else if (media instanceof OfflineMediaList) {
					OfflineMediaList offlineMediaList = (OfflineMediaList) media;
					if (offlineMediaList.isMultiSetType()) {
						Intent intent = new Intent();
						intent.setClass(mActivity, OfflineMediaDetailDialog.class);
						intent.putExtra(OfflineMediaDetailDialog.KEY_MEDIA_ID, offlineMediaList.getMediaId());
						startActivity(intent);
					} else {
						OfflineMedia offlineMedia = offlineMediaList.get(0);
						doMediaClick(offlineMedia);
					}
				}
			}
			refreshActionModeViewTitle();
		}
	};
	
	private OnMediaLongClickListener mOnMediaLongClickListener = new OnMediaLongClickListener() {
		
		@Override
		public void onMediaLongClick(MediaView mediaView, Object media) {
			if (mActionModeView != null && mActionModeView.isEdit()) {
				return;
			}
			startActionModeView();
			reverseMediaViewSelectStatus(mediaView, media);
		}
	};
	
	@Override
	protected void onStartActionMode() {
		// TODO Auto-generated method stub
		mMediaViewListAdapter.setInEditMode(true);
		refreshActionModeViewTitle();
	}

	@Override
	protected void onExitActionMode() {
		// TODO Auto-generated method stub
		mSelectedObject.clear();
		mMediaViewListAdapter.setInEditMode(false);
	}

	@Override
	protected void initActionModeCallback() {
		// TODO Auto-generated method stub		
		mActionModeCallback = new ActionModeView.Callback() {

			@Override
			public void onCreateBottomMenu(ActionModeBottomMenu menu) {
				ActionModeBottomMenuItem menuItem = new ActionModeBottomMenuItem(mActivity, false);
				menuItem.setIcon(R.drawable.icon_delete_dark);
				menuItem.setText(R.string.delete);
				menuItem.setTag(mMenuItemDeleteTag);
				menu.addItem(menuItem);
			}

			@Override
			public void onActionItemClick(ActionModeBottomMenuItem menuItem) {
				Object obj = menuItem.getTag();
				if (obj instanceof Integer) {
					Integer tag = (Integer) obj;
					if (tag == mMenuItemDeleteTag) {
						deleteSelectedMedias();
					}
				}
				exitActionModeView();
			}

			@Override
			public void onActionCancelClick() {
				exitActionModeView();
			}

			@Override
			public void onActionSelectAllClick(boolean selectAll) {
				mSelectedObject.clear();
				if (selectAll) {
					mSelectedObject.addAll(mOfflineMedias);
				}
				refreshLocalListView();
				refreshActionModeViewTitle();
			}

			@Override
			public void onActionEditClick() {
				// TODO Auto-generated method stub
				startActionModeView();				
			}
		};
	
		
	}}
