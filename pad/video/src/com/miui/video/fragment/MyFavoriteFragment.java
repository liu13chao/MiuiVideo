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
import com.miui.video.dialog.MediaDetailDialogFragment;
import com.miui.video.local.Favorite;
import com.miui.video.local.FavoriteManager;
import com.miui.video.local.OnlineFavorite;
import com.miui.video.local.FavoriteManager.OnFavoriteChangedListener;
import com.miui.video.statistic.SourceTagValueDef;
import com.miui.video.type.MediaInfo;
import com.miui.video.util.AlertMessage;
import com.miui.video.widget.ListViewEx;
import com.miui.video.widget.LoadingListView;
import com.miui.video.widget.actionmode.ActionModeBottomMenu;
import com.miui.video.widget.actionmode.ActionModeBottomMenuItem;
import com.miui.video.widget.actionmode.ActionModeView;
import com.miui.video.widget.media.MediaView;
import com.miui.video.widget.media.MediaView.OnMediaClickListener;
import com.miui.video.widget.media.MediaView.OnMediaLongClickListener;
import com.xiaomi.mipush.sdk.MiPushClient;

/**
 *@author dz
 *
 */

public class MyFavoriteFragment extends EditableFragment {
	//UI
	private View mContentView;
	
	private LoadingListView mFavoriteLoadingListView;
	private ListViewEx mFavoriteListView;
//	private View mFavoriteLoadingView;
	private View mFavoriteEmptyView;
	private MediaViewListAdapter mMediaViewListAdapter;
	
	//data
	private ArrayList<Object> mFavoriteMedias = new ArrayList<Object>();
//	private ArrayList<Favorite> mSelectedMedias = new ArrayList<Favorite>();
	
	//manager
	private FavoriteManager mFavoriteManager;
	
	//tag
	private int mMenuItemUnfavoriteTag = 0;
	
	
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
		mFavoriteManager.addListener(mOnFavoriteChangedListener);
		mFavoriteManager.loadFavorite();
		mFavoriteLoadingListView.setShowLoading(true);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		mFavoriteManager.removeListener(mOnFavoriteChangedListener);
	}
		
	//init
	private void init() {
		initManagers();
		initUI();
	}
	
	private void initUI() {
		initLocalListView();
		refresh();
	}
	
	private void initLocalListView() {
		mFavoriteLoadingListView = (LoadingListView) mContentView.findViewById(R.id.loading_list);
		mFavoriteListView = mFavoriteLoadingListView.getListView();
		mFavoriteListView.setVerticalFadingEdgeEnabled(true);
		mFavoriteListView.setFadingEdgeLength(getResources().getDimensionPixelSize(R.dimen.video_fade_edge_length));
		
		mMediaViewListAdapter = new MediaViewListAdapter(mActivity);
		mMediaViewListAdapter.setOnMediaClickListener(mOnMediaClickListener);
		mMediaViewListAdapter.setOnMediaLongClickListener(mOnMediaLongClickListener);
		mFavoriteListView.setAdapter(mMediaViewListAdapter);
		
//		mFavoriteLoadingView = View.inflate(mActivity, R.layout.load_view, null);
//		mFavoriteLoadingListView.setLoadingView(mFavoriteLoadingView);
		mFavoriteLoadingListView.setShowLoading(true);
		
		mFavoriteEmptyView = View.inflate(mActivity, R.layout.empty_view_media, null);
		TextView emptyHint = (TextView) mFavoriteEmptyView.findViewById(R.id.empty_hint);
		emptyHint.setText(getResources().getString(R.string.local_favorite_empty_hint));
		ImageView emptyIcon = (ImageView) mFavoriteEmptyView.findViewById(R.id.empty_icon);
		emptyIcon.setBackgroundResource(R.drawable.empty_icon_media);		

		mFavoriteLoadingListView.setEmptyView(mFavoriteEmptyView);
	}

	private void initManagers() {
		if (mFavoriteManager == null) {
			mFavoriteManager = DKApp.getSingleton(FavoriteManager.class);
		}
	}
	
	private void getMyFavoriteMedia() {
		List<Favorite> favoriteList = mFavoriteManager.getFavoriteList();
		prepareFavorateMedias(favoriteList);
		
		refresh();
	}

	private void refresh() {
		mFavoriteLoadingListView.setShowLoading(false);
//		mSelectedObject.clear();
//		mSelectedObject.addAll(mSelectedMedias);
		mMediaViewListAdapter.setGroup(mFavoriteMedias, mSelectedObject);
		if (mFavoriteMedias.size() > 0) {
			mActionModeView.setEnable(true);
		} else {
			mActionModeView.setEnable(false);
		}
	}
	
	private void prepareFavorateMedias(List<Favorite> favoriteList) {
		ArrayList<Favorite> selectedMedias = new ArrayList<Favorite>();
		mFavoriteMedias.clear();
		mFavoriteMedias.addAll(favoriteList);
		
		if (favoriteList != null) {
			for(int i = 0; i < favoriteList.size(); i++) {
				Favorite favorite = favoriteList.get(i);
				if (isSelectedMedia(favorite)) {
					selectedMedias.add(favorite);
				}
			}
		}
		//restore selected medias
		mSelectedObject.clear();
		mSelectedObject.addAll(selectedMedias);
	}
	
	private boolean isSelectedMedia(Favorite favorite) {
		if (favorite == null) {
			return false;
		}
		for(int i = 0; i < mSelectedObject.size(); i++) {
			Favorite selectedMedia = (Favorite) mSelectedObject.get(i);
			if (selectedMedia != null && selectedMedia.getId().equals(favorite.getId())) {
				return true;
			}
		}
		return false;
	}
		
	
	private void deleteSelectedMedias() {
		List<MediaInfo> mediaList = new ArrayList<MediaInfo>();
		if (mSelectedObject != null && mSelectedObject.size() > 0) {
			if (mSelectedObject.size() == mFavoriteMedias.size()) {
				mActionModeView.setEnable(false);
			}
			AlertMessage.show(R.string.cancel_favorite_success);
			MediaInfo mediaInfo;
			Favorite favorite;
			for(int i = 0; i < mSelectedObject.size(); i++) {
				favorite = (Favorite) mSelectedObject.get(i);
				if (favorite instanceof OnlineFavorite) {
					mediaInfo = ((OnlineFavorite)favorite).getMediaInfo();
					if (mediaInfo != null) {
						mediaList.add(mediaInfo);
						MiPushClient.unsubscribe(DKApp.getAppContext(), String.valueOf(mediaInfo.mediaid), null);
					}
				}
			}
			mFavoriteManager.delFavorite(mediaList);
		}
	}
	
	private void reverseMediaViewSelectStatus(MediaView mediaView, Object media) {
		if (mediaView == null || media == null || mActionModeView == null) {
			return;
		}
		
		if (media instanceof Favorite) {
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
		} else if (mSelectedObject.size() == mFavoriteMedias.size()) {
			mActionModeView.setUISelectNone();
		} else {
			mActionModeView.setUiSelectPart();
		}
		refreshActionModeViewTitle();
	}
		
	private OnMediaClickListener mOnMediaClickListener = new OnMediaClickListener() {
		
		@Override
		public void onMediaClick(MediaView mediaView, Object media) {
			if (mActionModeView != null && mActionModeView.isEdit()) {
				reverseMediaViewSelectStatus(mediaView, media);
			} else {
				if (media instanceof OnlineFavorite) {
					MediaInfo mdiaInfo = ((OnlineFavorite)media).getMediaInfo();
					Intent intent = new Intent();
					intent.setClass(mActivity, MediaDetailDialogFragment.class);
					intent.putExtra(MediaDetailDialogFragment.KEY_MEDIA_INFO, mdiaInfo);
					intent.putExtra(MediaDetailDialogFragment.KEY_SOURCE_PATH, SourceTagValueDef.PAD_MY_FAVORITE_VALUE);
					mActivity.startActivity(intent);
				}
			}
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
	
	//data callback
	private OnFavoriteChangedListener mOnFavoriteChangedListener = new OnFavoriteChangedListener() {
		
		@Override
		public void onFavoritesChanged(List<Favorite> favList) {
			getMyFavoriteMedia();
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
				menuItem.setIcon(R.drawable.icon_unfavorite);
				menuItem.setText(R.string.unfavorite);
				menuItem.setTag(mMenuItemUnfavoriteTag);
				menu.addItem(menuItem);
			}

			@Override
			public void onActionItemClick(ActionModeBottomMenuItem menuItem) {
				Object obj = menuItem.getTag();
				if (obj instanceof Integer) {
					Integer tag = (Integer) obj;
					if (tag == mMenuItemUnfavoriteTag) {
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
					for(int i = 0; i < mFavoriteMedias.size(); i++) {
						Object obj = mFavoriteMedias.get(i);
						if (obj instanceof Favorite) {
							mSelectedObject.add((Favorite)obj);
						}
					}
				}
				mMediaViewListAdapter.refresh();
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
