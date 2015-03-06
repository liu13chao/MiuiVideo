package com.miui.video.dialog;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.miui.video.DKApp;
import com.miui.video.R;
import com.miui.video.adapter.MediaViewListAdapter;
import com.miui.video.api.def.MediaConstantsDef;
import com.miui.video.base.BaseDialog;
import com.miui.video.common.PlaySession;
import com.miui.video.offline.OfflineMedia;
import com.miui.video.offline.OfflineMediaList;
import com.miui.video.offline.OfflineMediaManager;
import com.miui.video.offline.OfflineMediaManager.OfflineMediaListener;
import com.miui.video.widget.ListViewEx;
import com.miui.video.widget.LoadingListView;
import com.miui.video.widget.actionmode.ActionModeBottomMenu;
import com.miui.video.widget.actionmode.ActionModeBottomMenuItem;
import com.miui.video.widget.actionmode.ActionModeView;
import com.miui.video.widget.media.MediaView;
import com.miui.video.widget.media.MediaView.OnMediaClickListener;
import com.miui.video.widget.media.MediaView.OnMediaLongClickListener;

public class OfflineMediaDetailDialog extends BaseDialog implements ActionModeView.Callback {
	
	public static final String KEY_MEDIA_ID = "mediaId";
	
	//UI
	private ActionModeView mActionModeView;
	private TextView mTitleTop; 
	private LoadingListView mLoadingListView;
	private ListViewEx mListViewEx;
	private MediaViewListAdapter mOfflineMediaDetailAdapter;
	
	private Button mBtnMore;
	
	//received data
	private int mediaId;
	private String mTitleName = "";
	
	//data from local
	private ArrayList<Object> mOfflineMedias = new ArrayList<Object>();
	
	private int mPosterW;
	private int mPosterH;
//	private int mIntervalH;
//	private int mIntervalV;
	
	//
	protected ArrayList<Object> mSelectedObject = new ArrayList<Object>();
	private int mMenuItemDeleteTag = 0;
	
	//manager
	private OfflineMediaManager mOfflineMediaManager;
	
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
		mOfflineMediaManager.removeOfflineMediaListener(mOfflineMediaListener);
	}
	
	//init
	private void init() {
//		getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg);		
		initReceivedData();
		initManager();
		initDimen();
		initUI();
	}
	
	private void initReceivedData() {
		Intent intent = getIntent();
		this.mediaId = intent.getIntExtra(KEY_MEDIA_ID, -1);
	}
	
	private void initManager() {
		mOfflineMediaManager = DKApp.getSingleton(OfflineMediaManager.class);
		mOfflineMediaManager.addOfflineMediaListener(mOfflineMediaListener);
	}
	
	private void initUI() {
		ViewGroup rootView = (ViewGroup) findViewById(R.id.action_mode);
		if (rootView != null) {
			mActionModeView = new ActionModeView(this, this, rootView, false);
		}
		
		mTitleTop = (TextView) findViewById(R.id.local_detail_title);
		mBtnMore = (Button) findViewById(R.id.local_detail_more);
		mBtnMore.setOnClickListener(mOnClickListener);
		
		initListView();
		refresh();
	}
	
	private void initDimen() {
		mPosterW = this.getResources().getDimensionPixelSize(R.dimen.local_detail_media_width);
		mPosterH = this.getResources().getDimensionPixelSize(R.dimen.local_detail_media_height);
//		mIntervalH = this.getResources().getDimensionPixelSize(R.dimen.local_detail_media_intervalH);
//		mIntervalV = this.getResources().getDimensionPixelSize(R.dimen.media_view_list_default_intervalV);
	}
	
	private void initListView() {
		mLoadingListView = (LoadingListView) findViewById(R.id.local_detail_list);
		FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mLoadingListView.getLayoutParams();
		params.bottomMargin = this.getResources().getDimensionPixelSize(R.dimen.offline_detail_list_bottom_margin);
		mLoadingListView.setLayoutParams(params);
		mListViewEx = mLoadingListView.getListView();
		mOfflineMediaDetailAdapter = new MediaViewListAdapter(this);
		mOfflineMediaDetailAdapter.setOnMediaClickListener(mOnMediaClickListener);
		mOfflineMediaDetailAdapter.setOnMediaLongClickListener(mOnMediaLongClickListener);
		mListViewEx.setAdapter(mOfflineMediaDetailAdapter);
		int nameViewColor = this.getResources().getColor(R.color.p_80_black);
		int statusViewColor = this.getResources().getColor(R.color.p_50_black);
		mOfflineMediaDetailAdapter.setInfoViewColor(nameViewColor, statusViewColor);
		mOfflineMediaDetailAdapter.setPosterSize(mPosterW, mPosterH);
	}
	
	//packaged method
	private void getData() {
		mOfflineMediaManager.loadOfflineMedia();
	}
	
	private void refresh() {
		mTitleTop.setText(mTitleName);
		mOfflineMediaDetailAdapter.setGroup(mOfflineMedias, mSelectedObject);
	}
	
	private void doMediaClick(OfflineMedia offlineMedia) {
		if(offlineMedia.status != MediaConstantsDef.OFFLINE_STATE_FINISH) {
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
	
	private void buildOfflineMedias(List<Object> offlineMediaLists) {
		mOfflineMedias.clear();
		if(offlineMediaLists == null) {
			return;
		}
		for(int i = 0; i < offlineMediaLists.size(); i++) {
			Object object = offlineMediaLists.get(i);
			if(object instanceof OfflineMediaList) {
				OfflineMediaList offlineMediaList = (OfflineMediaList) object;
				if(offlineMediaList.getMediaId() == mediaId) {
					mTitleName = offlineMediaList.getName();
					mOfflineMedias.addAll(offlineMediaList);
					return;
				}
			}
		}
	}
	
	private void refreshOfflineMedia(OfflineMedia offlineMedia) {
		if(offlineMedia != null) {
			for(int i = 0; i < mOfflineMedias.size(); i++) {
				Object object = mOfflineMedias.get(i);
				if(object instanceof OfflineMedia) {
					OfflineMedia tmp = (OfflineMedia) object;
					if(tmp.episode == offlineMedia.episode) {
						object = offlineMedia;
					}
				}
			}
		}
	}
	
	private void startDownloadSelectDialog() {
		Intent intent = new Intent();
		intent.setClass(this, DownloadSelectDialog.class);
		intent.putExtra(DownloadSelectDialog.KEY_MEDIA_ID, mediaId);
		this.startActivity(intent);
	}

	private void refreshActionModeViewTitle() {
		String str = getResources().getString(R.string.select_count_xiang);
		str = String.format(str, mSelectedObject.size());
		mActionModeView.setTitle(str);
	}
	
	//UI callback
	private OnClickListener mOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			int id = v.getId();
			if(id == R.id.local_detail_more) {
				startDownloadSelectDialog();
			}
		}
	};
	
	private OnMediaClickListener mOnMediaClickListener = new OnMediaClickListener() {
		
		@Override
		public void onMediaClick(MediaView mediaView, Object media) {
			if(mActionModeView != null && mActionModeView.isEdit()) {
				reverseMediaViewSelectStatus(mediaView, media);
			} else {
				OfflineMedia offlineMedia = (OfflineMedia) media;
				doMediaClick(offlineMedia);
			}
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
	
	protected void startActionModeView() {
		if (mActionModeView != null) {
			mActionModeView.startActionMode();
			mOfflineMediaDetailAdapter.setInEditMode(true);
			mTitleTop.setVisibility(View.INVISIBLE);
			refreshActionModeViewTitle();
			if (mSelectedObject.size() == 0) {
				mActionModeView.setUISelectAll();
			}
		}
	}	

	protected void exitActionModeView() {
		if (mActionModeView != null) {
			mActionModeView.exitActionMode();
			mOfflineMediaDetailAdapter.setInEditMode(false);
			mTitleTop.setVisibility(View.VISIBLE);
			mSelectedObject.clear();
		}
	}
	
	//data callback
	private OfflineMediaListener mOfflineMediaListener = new OfflineMediaListener() {
		
		@Override
		public void onOfflineMediaChanged() {
			List<Object> offlineMediaLists = mOfflineMediaManager.getOfflineMediaList();
			buildOfflineMedias(offlineMediaLists);
			refresh();
		}
		
		@Override
		public void onOfflineMediaUpdate(OfflineMedia offlineMedia) {
			refreshOfflineMedia(offlineMedia);
			refresh();
		}
	};

	private void deleteSelectedMedias() {
		if (mSelectedObject.size() == mOfflineMedias.size()) {
			mActionModeView.setEnable(false);
		}
		for(int i = 0; i < mSelectedObject.size(); i++) {
			Object object = mSelectedObject.get(i);
			if(object instanceof OfflineMedia) {
				OfflineMedia offlineMedia = (OfflineMedia) object;
				mOfflineMedias.remove(object);
				mOfflineMediaManager.delOfflineMedia(mediaId, offlineMedia.episode);
			}
		}

	}
	
	private void reverseMediaViewSelectStatus(MediaView mediaView, Object media) {
		if(mediaView == null || media == null) {
			return;
		}
		
		boolean isSelected = mediaView.isSelected();
		mediaView.setIsSelected(!isSelected);
		if(media instanceof OfflineMedia) {
			OfflineMedia offlineMedia = (OfflineMedia) media;
			if(!isSelected) {
				mSelectedObject.add(offlineMedia);
			} else {
				mSelectedObject.remove(offlineMedia);
			}
		}
		
		if(mSelectedObject.size() == 0) {
			mActionModeView.setUISelectAll();
		} else if(mSelectedObject.size() == mOfflineMedias.size()) {
			mActionModeView.setUISelectNone();
		} else {
			mActionModeView.setUiSelectPart();
		}
		refreshActionModeViewTitle();
	}
	
	//ActionModeView.Callback()
	@Override
	public void onCreateBottomMenu(ActionModeBottomMenu menu) {
		ActionModeBottomMenuItem menuItem = new ActionModeBottomMenuItem(this, true);
		menuItem.setIcon(R.drawable.icon_delete_light);
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
		mOfflineMediaDetailAdapter.refresh();
		refreshActionModeViewTitle();
	}

	@Override
	public void onActionEditClick() {
		// TODO Auto-generated method stub
		startActionModeView();
	}

}