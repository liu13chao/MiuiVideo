package com.miui.video.dialog;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.miui.video.DKApp;
import com.miui.video.R;
import com.miui.video.adapter.MediaViewListAdapter;
import com.miui.video.base.BaseDialog;
import com.miui.video.common.PlaySession;
import com.miui.video.model.loader.LocalMediaLoader;
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
 *@author tangfuling
 *
 */

public class LocalDetailDialog extends BaseDialog implements ActionModeView.Callback {
	
	public static String KEY_LOCAL_MEDIA_LIST = "local_media_list";
	
	//UI
	private ActionModeView mActionModeView;
	private TextView mTitleTop; 
	private LoadingListView mLoadingListView;
	private ListViewEx mListViewEx;
	private MediaViewListAdapter mLocalDetailAdapter;

	private Button mBtnMore;
	
	//received data
	private ArrayList<Object> mLocalMedias = new ArrayList<Object>();
	private String mTitleName = "";
	
	private int mPosterW;
	private int mPosterH;
	
	//
	protected ArrayList<Object> mSelectedObject = new ArrayList<Object>();
	private int mMenuItemDeleteTag = 0;

	private LocalMediaLoader mLocalMediaLoader;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.local_detail);
		init();
	}
	
	//init
	private void init() {
		mLocalMediaLoader = LocalMediaLoader.getInstance();
		initReceivedData();
		initDimen();
		initUI();
	}
	
	private void initReceivedData() {
		Intent intent = getIntent();
		Object obj = intent.getSerializableExtra(KEY_LOCAL_MEDIA_LIST);
		if(obj instanceof LocalMediaList) {
			LocalMediaList localMediaList = (LocalMediaList) obj;
			mTitleName = localMediaList.getName();
			mLocalMedias.addAll(localMediaList.getLocalMediaList());
		}
	}
	
	private void initUI() {
		ViewGroup rootView = (ViewGroup) findViewById(R.id.action_mode);
		if (rootView != null) {
			mActionModeView = new ActionModeView(this, this, rootView, false);
		}
		
		mTitleTop = (TextView) findViewById(R.id.local_detail_title);
		mBtnMore = (Button) findViewById(R.id.local_detail_more);
		mBtnMore.setVisibility(View.GONE);
		
		initListView();
		refresh();
	}
	
	private void initDimen() {
		mPosterW = this.getResources().getDimensionPixelSize(R.dimen.local_detail_media_width);
		mPosterH = this.getResources().getDimensionPixelSize(R.dimen.local_detail_media_height);
	}
	
	private void initListView() {
		mLoadingListView = (LoadingListView) findViewById(R.id.local_detail_list);
		FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mLoadingListView.getLayoutParams();
		params.bottomMargin = this.getResources().getDimensionPixelSize(R.dimen.local_detail_list_bottom_margin);
		mLoadingListView.setLayoutParams(params);
		mListViewEx = mLoadingListView.getListView();
		mLocalDetailAdapter = new MediaViewListAdapter(this);
		mLocalDetailAdapter.setOnMediaClickListener(mOnMediaClickListener);
		mLocalDetailAdapter.setOnMediaLongClickListener(mOnMediaLongClickListener);
		mListViewEx.setAdapter(mLocalDetailAdapter);
		int nameViewColor = this.getResources().getColor(R.color.p_80_black);
		int statusViewColor = this.getResources().getColor(R.color.p_50_black);
		mLocalDetailAdapter.setInfoViewColor(nameViewColor, statusViewColor);
		mLocalDetailAdapter.setPosterSize(mPosterW, mPosterH);
	}
	
	//packaged method
	private void refresh() {
		mTitleTop.setText(mTitleName);
//		String str = this.getResources().getString(R.string.local_detail_more);
//		str = String.format(str, mLocalMedias.size());
//		mBtnMore.setText(str);
		mLocalDetailAdapter.setGroup(mLocalMedias, mSelectedObject);
	}
	
	private void refreshActionModeViewTitle() {
		String str = getResources().getString(R.string.select_count_xiang);
		str = String.format(str, mSelectedObject.size());
		mActionModeView.setTitle(str);
	}
	
	private void deleteSelectedMedias() {
		if (mSelectedObject.size() == mLocalMedias.size()) {
			mActionModeView.setEnable(false);
		}
		LocalMediaList mediaList = new LocalMediaList();
		for(int i = 0; i < mSelectedObject.size(); i++) {
			Object object = mSelectedObject.get(i);
			if(object instanceof LocalMedia) {
				LocalMedia localMedia = (LocalMedia) object;
				mediaList.add(localMedia);
				mLocalMedias.remove(object);
			}
		}
		List<LocalMediaList> selectedLocalMedias = new ArrayList<LocalMediaList>();
		selectedLocalMedias.add(mediaList);
		mLocalMediaLoader.delLocalMediaLists(selectedLocalMedias);
	}
	
	private void reverseMediaViewSelectStatus(MediaView mediaView, Object media) {
		if(mediaView == null || media == null) {
			return;
		}
		
		boolean isSelected = mediaView.isSelected();
		mediaView.setIsSelected(!isSelected);
		if(media instanceof LocalMedia) {
			LocalMedia localMedia = (LocalMedia) media;
			if(!isSelected) {
				mSelectedObject.add(localMedia);
			} else {
				mSelectedObject.remove(localMedia);
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
	
	//UI callback
	private OnMediaClickListener mOnMediaClickListener = new OnMediaClickListener() {
		
		@Override
		public void onMediaClick(MediaView mediaView, Object media) {
			if(media instanceof LocalMedia) {
				if(mActionModeView != null && mActionModeView.isEdit()) {
					reverseMediaViewSelectStatus(mediaView, media);
				} else {
					LocalMedia localMedia = (LocalMedia) media;
					PlaySession playSession = DKApp.getSingleton(PlaySession.class);
					playSession.startPlayerLocal(localMedia);
				}
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
	
	private void adjustList() {
		FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mLoadingListView.getLayoutParams();		
		if (mActionModeView != null && mActionModeView.isEdit()) {
			params.bottomMargin = this.getResources().getDimensionPixelSize(R.dimen.offline_detail_list_bottom_margin);
		} else {
			params.bottomMargin = this.getResources().getDimensionPixelSize(R.dimen.local_detail_list_bottom_margin);
		}
		mLoadingListView.setLayoutParams(params);
	}
	
	protected void startActionModeView() {
		if (mActionModeView != null && !mActionModeView.isEdit()) {
			mActionModeView.startActionMode();
			mLocalDetailAdapter.setInEditMode(true);
			mTitleTop.setVisibility(View.INVISIBLE);
			refreshActionModeViewTitle();
			adjustList();
			if (mSelectedObject.size() == 0) {
				mActionModeView.setUISelectAll();
			}
		}
	}	

	protected void exitActionModeView() {
		if (mActionModeView != null) {
			mActionModeView.exitActionMode();
			mLocalDetailAdapter.setInEditMode(false);
			mTitleTop.setVisibility(View.VISIBLE);
			mSelectedObject.clear();
			adjustList();
		}
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
			mSelectedObject.addAll(mLocalMedias);
		}
		mLocalDetailAdapter.refresh();
		refreshActionModeViewTitle();
	}

	@Override
	public void onActionEditClick() {
		// TODO Auto-generated method stub
		startActionModeView();
	}

}
