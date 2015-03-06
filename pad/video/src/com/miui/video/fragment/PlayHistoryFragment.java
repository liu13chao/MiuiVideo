package com.miui.video.fragment;

import java.util.ArrayList;
import java.util.HashMap;
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
import com.miui.video.adapter.PlayHistoryAdapter;
import com.miui.video.base.EditableFragment;
import com.miui.video.common.PlaySession;
import com.miui.video.dialog.LocalDetailDialog;
import com.miui.video.dialog.MediaDetailDialogFragment;
import com.miui.video.local.LocalPlayHistory;
import com.miui.video.local.OnlinePlayHistory;
import com.miui.video.local.PlayHistory;
import com.miui.video.local.PlayHistoryManager;
import com.miui.video.local.PlayHistoryManager.OnHistoryChangedListener;
import com.miui.video.statistic.SourceTagValueDef;
import com.miui.video.type.LocalMedia;
import com.miui.video.type.LocalMediaList;
import com.miui.video.type.MediaInfo;
import com.miui.video.util.DKLog;
import com.miui.video.util.Util;
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

public class PlayHistoryFragment extends EditableFragment {
	private final static String TAG = PlayHistoryFragment.class.getSimpleName();
		
	private View mContentView;
	
//	private View mLineTime;
	
	private LoadingListView mPlayHisLoadingListView;
	private ListViewEx mPlayHisListView;
	private View mEmptyView;
//	private View mLoadingView;
	private PlayHistoryAdapter mPlayHisAdapter;
		
	private int mMenuItemDeleteTag = 0;
	
	//date from play his
	private List<String> mPlayHisDateList = new ArrayList<String>();
	
	//manager
	private PlayHistoryManager mPlayHisManager;

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
		mPlayHisManager.addListener(mOnHistoryChangedListener);
		mPlayHisManager.loadPlayHistory();
		mPlayHisLoadingListView.setShowLoading(true);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		if (mPlayHisManager != null) {
			mPlayHisManager.removeListener(mOnHistoryChangedListener);
		}
	}
	
	//init
	private void init() {
		initManagers();
		initUI();
	}
	
	private void initUI() {
//		mLineTime = mContentView.findViewById(R.id.local_video_line_time);
		initPlayHisListView();
	}
	
	private void initPlayHisListView() {
		mPlayHisLoadingListView = (LoadingListView) mContentView.findViewById(R.id.loading_list);
		mPlayHisListView = mPlayHisLoadingListView.getListView();
		mPlayHisListView.setVerticalFadingEdgeEnabled(true);
		mPlayHisListView.setFadingEdgeLength(getResources().getDimensionPixelSize(R.dimen.video_fade_edge_length));
		
		mPlayHisAdapter = new PlayHistoryAdapter(mActivity);
		mPlayHisAdapter.setOnMediaClickListener(mOnMediaClickListener);
		mPlayHisAdapter.setOnMediaLongClickListener(mOnMediaLongClickListener);
		mPlayHisListView.setAdapter(mPlayHisAdapter);
//		mPlayHisListView.setOnScrollListener(mOnScrollListener);
		
//		mLoadingView = View.inflate(mActivity, R.layout.load_view, null);
//		mPlayHisLoadingListView.setLoadingView(mLoadingView);
		mPlayHisLoadingListView.setShowLoading(true);
		
		mEmptyView = View.inflate(mActivity, R.layout.empty_view_media, null);
		TextView emptyHint = (TextView) mEmptyView.findViewById(R.id.empty_hint);
		emptyHint.setText(getResources().getString(R.string.play_his_empty_hint));
		ImageView emptyIcon = (ImageView) mEmptyView.findViewById(R.id.empty_icon);
		emptyIcon.setBackgroundResource(R.drawable.empty_icon_play_his);

		mPlayHisLoadingListView.setEmptyView(mEmptyView);
	}

	private void initManagers() {
		if (mPlayHisManager == null) {
			mPlayHisManager = DKApp.getSingleton(PlayHistoryManager.class);
		}
	}

	private void getPlayHisMedias() {
		List<String> list = mPlayHisManager.getPlayHisDateList();
		mPlayHisDateList.clear();
		mPlayHisDateList.addAll(list);
		
		restoreSelectedMedias();
		refresh();
	}
	
	private void restoreSelectedMedias() {
		ArrayList<Object> selectedMedias = new ArrayList<Object>();
		HashMap<String, List<PlayHistory>> playHisListMap = mPlayHisManager.getHistoryListMap();
		for(int i = 0; i < mPlayHisDateList.size(); i++) {
			String date = mPlayHisDateList.get(i);
			if (!Util.isEmpty(date)) {
				List<PlayHistory> playHisList = playHisListMap.get(date);
				if (playHisList != null) {
					for(int j = 0; j < playHisList.size(); j++) {
						PlayHistory playHis = playHisList.get(j);
						if (playHis != null) {
							if (isSelectedMedia(playHis)) {
								selectedMedias.add(playHis);
							}
						}
					}
				}
			}
		}
		mSelectedObject.clear();
		mSelectedObject.addAll(selectedMedias);
	}
	
	private boolean isSelectedMedia(PlayHistory playHistory) {
		if (playHistory == null) {
			return false;
		}
		for(int i = 0; i < mSelectedObject.size(); i++) {
			PlayHistory selectedPlayHis = (PlayHistory) mSelectedObject.get(i);
			if (selectedPlayHis != null && selectedPlayHis.equals(playHistory)) {
				return true;
			}
		}
		return false;
	}

	private void deleteSelectedMedias() {
		if (mSelectedObject.size() == mPlayHisManager.getPlayHisList().size()) {
			mActionModeView.setEnable(false);
		}
		List<PlayHistory> selectList = new ArrayList<PlayHistory>();
		for (int i=0; i<mSelectedObject.size(); i++) {
			selectList.add((PlayHistory)mSelectedObject.get(i));
		}
		mPlayHisManager.delPlayHistoryList(selectList);
	}

	private void refresh() {
		mPlayHisLoadingListView.setShowLoading(false);
		mPlayHisAdapter.setData(mPlayHisDateList, mPlayHisManager.getHistoryListMap(), mSelectedObject);
		if (mPlayHisDateList.size() > 0) {
//			mLineTime.setVisibility(View.VISIBLE);
			mActionModeView.setEnable(true);
		} else {
//			mLineTime.setVisibility(View.INVISIBLE);
			mActionModeView.setEnable(false);
		}
	}
	
	private void startMediaDetailDialog(OnlinePlayHistory playHistory) {
		if (playHistory == null) {
			return;
		}
		Object obj = playHistory.getPlayItem();
		if (obj instanceof MediaInfo) {
			MediaInfo mediaInfo = (MediaInfo) obj;
			Intent intent = new Intent();
			intent.setClass(mActivity, MediaDetailDialogFragment.class);
			intent.putExtra(MediaDetailDialogFragment.KEY_MEDIA_INFO, mediaInfo);
			intent.putExtra(MediaDetailDialogFragment.KEY_IS_BANNER, false);
			intent.putExtra(MediaDetailDialogFragment.KEY_SOURCE_PATH, SourceTagValueDef.PAD_PLAY_HIS_VALUE);
			mActivity.startActivity(intent);
		}
	}
	
	private void reverseMediaViewSelectStatus(MediaView mediaView, Object media) {
		if (mediaView == null || media == null) {
			return;
		}
		
		if (media instanceof PlayHistory) {
			boolean isSelected = mediaView.isSelected();
			mediaView.setIsSelected(!isSelected);
			if (!isSelected) {
				mSelectedObject.add(media);
			} else {
				mSelectedObject.remove(media);
			}
		}

		if (mSelectedObject.size() == 0) {
			mActionModeView.setUISelectAll();
		} else if (mSelectedObject.size() == mPlayHisManager.getPlayHisList().size()) {
			mActionModeView.setUISelectNone();
		} else {
			mActionModeView.setUiSelectPart();
		}
		refreshActionModeViewTitle();
	}

	private OnMediaClickListener mOnMediaClickListener = new OnMediaClickListener() {
		
		@Override
		public void onMediaClick(MediaView mediaView, Object media) {
			DKLog.d(TAG, "onMediaClick====================================");
			if (mActionModeView != null && mActionModeView.isEdit()) {
				reverseMediaViewSelectStatus(mediaView, media);
			} else {
				Object contentInfo = media;
				if (contentInfo instanceof OnlinePlayHistory) {
					DKLog.d(TAG, "OnlinePlayHistory");
					OnlinePlayHistory playHistory = (OnlinePlayHistory) contentInfo;
					startMediaDetailDialog(playHistory);
				} else if (contentInfo instanceof LocalPlayHistory) {
					DKLog.d(TAG, "LocalPlayHistory");
					LocalPlayHistory localPlayHistory = (LocalPlayHistory) contentInfo;
					Object content = localPlayHistory.getPlayItem();
					if (content instanceof LocalMediaList) {
						DKLog.d(TAG, "LocalMediaList");
						LocalMediaList localMediaList = (LocalMediaList) content;
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
					} else if (content instanceof LocalMedia) {
						DKLog.d(TAG, "LocalMedia");
						LocalMedia localMedia = (LocalMedia) content;
						PlaySession playSession = DKApp.getSingleton(PlaySession.class);
						playSession.startPlayerLocal(localMedia);
					}
				}
			}
			DKLog.d(TAG, "onMediaClick end ====================================");
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

//	private OnScrollListener mOnScrollListener = new OnScrollListener() {
//		
//		@Override
//		public void onScrollStateChanged(AbsListView view, int scrollState) {
//			// TODO Auto-generated method stub
//			
//		}
//		
//		@Override
//		public void onScroll(AbsListView view, int firstVisibleItem,
//				int visibleItemCount, int totalItemCount) {
//			// TODO Auto-generated method stub
//			if (mPlayHisListView.isBottommost()) {
//				DKLog.d(TAG, "onScroll isBottommost");
//			}
//		}
//	};
	
	//data callback
	private OnHistoryChangedListener mOnHistoryChangedListener = new OnHistoryChangedListener() {
		
		@Override
		public void onHistoryChanged(List<PlayHistory> historyList) {
			getPlayHisMedias();
		}
	};

	@Override
	protected void onStartActionMode() {
		// TODO Auto-generated method stub
		mPlayHisAdapter.setInEditMode(true);
		refreshActionModeViewTitle();
	}

	@Override
	protected void onExitActionMode() {
		// TODO Auto-generated method stub
		mPlayHisAdapter.setInEditMode(false);
		mSelectedObject.clear();
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
					mSelectedObject.addAll(mPlayHisManager.getPlayHisList());
				}
				mPlayHisAdapter.refresh();
				refreshActionModeViewTitle();
			}

			@Override
			public void onActionEditClick() {
				// TODO Auto-generated method stub
				startActionModeView();
			}
		};
	}
}
