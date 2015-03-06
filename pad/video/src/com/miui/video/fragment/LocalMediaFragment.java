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
import com.miui.video.base.EditableFragment;
import com.miui.video.common.PlaySession;
import com.miui.video.dialog.LocalDetailDialog;
import com.miui.video.model.loader.LocalMediaLoader;
import com.miui.video.model.loader.LocalMediaLoader.OnLocalMediaLoadListener;
import com.miui.video.type.LocalMedia;
import com.miui.video.type.LocalMediaList;
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

public class LocalMediaFragment extends EditableFragment {
	//UI
	private View mContentView;

	private LoadingListView mLocalLoadingListView;
	private ListViewEx mLocalListView;
//	private View mLocalLoadingView;
	private View mLocalEmptyView;
	private MediaViewListAdapter mMediaViewListAdapter;
	
	//data from local
	private ArrayList<Object> mLocalMedias = new ArrayList<Object>();
	//manager
	private LocalMediaLoader mLocalMediaLoader;
	
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
		getLocalMedia();
		mLocalLoadingListView.setShowLoading(true);
	}
	
	@Override
	public void onPause() {
		super.onPause();
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
		mLocalLoadingListView = (LoadingListView) mContentView.findViewById(R.id.loading_list);
		mLocalListView = mLocalLoadingListView.getListView();
		mLocalListView.setVerticalFadingEdgeEnabled(true);
		mLocalListView.setFadingEdgeLength(getResources().getDimensionPixelSize(R.dimen.video_fade_edge_length));
		
		mMediaViewListAdapter = new MediaViewListAdapter(mActivity);
		mMediaViewListAdapter.setOnMediaClickListener(mOnMediaClickListener);
		mMediaViewListAdapter.setOnMediaLongClickListener(mOnMediaLongClickListener);
		mLocalListView.setAdapter(mMediaViewListAdapter);
		
//		mLocalLoadingView = View.inflate(mActivity, R.layout.load_view, null);
//		mLocalLoadingListView.setLoadingView(mLocalLoadingView);
		mLocalLoadingListView.setShowLoading(true);
		
		mLocalEmptyView = View.inflate(mActivity, R.layout.empty_view_media, null);
		TextView emptyHint = (TextView) mLocalEmptyView.findViewById(R.id.empty_hint);
		emptyHint.setText(getResources().getString(R.string.local_media_empty_hint));
		ImageView emptyIcon = (ImageView) mLocalEmptyView.findViewById(R.id.empty_icon);
		emptyIcon.setBackgroundResource(R.drawable.empty_icon_media);

		mLocalLoadingListView.setEmptyView(mLocalEmptyView);
	}

	private void refreshLocalListView() {
		mLocalLoadingListView.setShowLoading(false);
		mMediaViewListAdapter.setGroup(mLocalMedias, mSelectedObject);
		if (mLocalMedias.size() > 0) {
			mActionModeView.setEnable(true);
		} else {
			mActionModeView.setEnable(false);
		}
	}
	
	private void initManagers() {
		mLocalMediaLoader = LocalMediaLoader.getInstance();
	}

	private void getLocalMedia() {
		mLocalLoadingListView.setShowLoading(true);
		mLocalMediaLoader.getLocalMedias(mOnLocalMediaLoadListener, false);
	}
		
//	private int getMediaTotalCount() {
//		int mediaCount = 0;
//		for(int i = 0; i < mLocalMedias.size(); i++) {
//			Object obj = mLocalMedias.get(i);
//			if(obj instanceof LocalMediaList) {
//				LocalMediaList localMediaList = (LocalMediaList) obj;
//				mediaCount += localMediaList.size();
//			} else if(obj instanceof OfflineMediaList) {
//				OfflineMediaList offlineMediaList = (OfflineMediaList) obj;
//				mediaCount += offlineMediaList.size();
//			}
//		}
//		return mediaCount;
//	}
		
	private void deleteSelectedMedias() {
		if (mSelectedObject.size() == mLocalMedias.size()) {
			mActionModeView.setEnable(false);
		}
		List<LocalMediaList> selectedLocalMedias = new ArrayList<LocalMediaList>();
		for(int i = 0; i < mSelectedObject.size(); i++) {
			Object object = mSelectedObject.get(i);
			if(object instanceof LocalMediaList) {
				LocalMediaList localMediaList = (LocalMediaList) object;
				selectedLocalMedias.add(localMediaList);
			}
		}
		mLocalMediaLoader.delLocalMediaLists(selectedLocalMedias);
	}
	
	private void reverseMediaViewSelectStatus(MediaView mediaView, Object media) {
		if(mediaView == null || media == null) {
			return;
		}
		boolean isSelected = mediaView.isSelected();
		mediaView.setIsSelected(!isSelected);
		if(media instanceof LocalMediaList) {
			LocalMediaList localMediaList = (LocalMediaList) media;
			if(!isSelected) {
				mSelectedObject.add(localMediaList);
			} else {
				mSelectedObject.remove(localMediaList);
			}
		}

		if(mSelectedObject.size() == 0) {
			mActionModeView.setUISelectAll();
		} else if(mSelectedObject.size() == mLocalMedias.size()) {
			mActionModeView.setUISelectNone();
		} else {
			mActionModeView.setUiSelectPart();
		}
		refreshActionModeViewTitle();
	}
	
	private OnLocalMediaLoadListener mOnLocalMediaLoadListener = new OnLocalMediaLoadListener() {
		
		@Override
		public void onLocalMediaDone(ArrayList<LocalMediaList> localMedias) {
			mLocalLoadingListView.setShowLoading(false);
			mLocalMedias.clear();
			if(localMedias != null) {
				mLocalMedias.addAll(localMedias);
			}
			refreshLocalListView();
		}
	};
		
	private OnMediaClickListener mOnMediaClickListener = new OnMediaClickListener() {
		
		@Override
		public void onMediaClick(MediaView mediaView, Object media) {
			if(mActionModeView != null && mActionModeView.isEdit()) {
				reverseMediaViewSelectStatus(mediaView, media);
			} else {
				if(media instanceof LocalMediaList) {
					LocalMediaList localMediaList = (LocalMediaList) media;
					if(localMediaList.isDirType()) {
						Intent intent = new Intent();
						intent.setClass(mActivity, LocalDetailDialog.class);
						intent.putExtra(LocalDetailDialog.KEY_LOCAL_MEDIA_LIST, localMediaList);
						mActivity.startActivity(intent);
					} else {
						LocalMedia localMedia = localMediaList.get(0);
						if(localMedia != null) {
							PlaySession playSession = DKApp.getSingleton(PlaySession.class);
							playSession.startPlayerLocal(localMedia);
						}
					}
				}
			}
			refreshActionModeViewTitle();
		}
	};
	
	private OnMediaLongClickListener mOnMediaLongClickListener = new OnMediaLongClickListener() {
		
		@Override
		public void onMediaLongClick(MediaView mediaView, Object media) {
			if(mActionModeView != null && mActionModeView.isEdit()) {
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
				if(obj instanceof Integer) {
					Integer tag = (Integer) obj;
					if(tag == mMenuItemDeleteTag) {
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
				if(selectAll) {
					mSelectedObject.addAll(mLocalMedias);
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
