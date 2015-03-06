package com.miui.video;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.miui.video.adapter.SelectEpAdapter;
import com.miui.video.api.def.MediaConstantsDef;
import com.miui.video.base.BaseActivity;
import com.miui.video.controller.PlaySession;
import com.miui.video.local.OnlinePlayHistory;
import com.miui.video.local.PlayHistory;
import com.miui.video.local.PlayHistoryManager;
import com.miui.video.local.PlayHistoryManager.OnHistoryChangedListener;
import com.miui.video.offline.OfflineMedia;
import com.miui.video.statistic.SourceTagValueDef;
import com.miui.video.type.MediaDetailInfo2;
import com.miui.video.type.MediaSetInfo;
import com.miui.video.widget.ExpandableListViewEx;
import com.miui.video.widget.ExpandableLoadingListView;
import com.miui.video.widget.detail.ep.DetailEpPlayManager;
import com.miui.video.widget.detail.ep.SetInfoStatusEp;
import com.xiaomi.mitv.common.json.JsonSerializer;

/**
 *@author tangfuling
 *
 */

public class SelectEpActivity extends BaseActivity {

	public static final String KEY_PLAY_HISTORY = "play_history";
	public static final String KEY_MEDIA_DETAIL_INFO2 = "media_detail_info2";
	
	//received data
	private MediaDetailInfo2 mMediaDetailInfo;
	private MediaSetInfo[] mSetList;
	private PlayHistory mPlayHistory;
	
	//data from local
//	private OfflineMediaList mOfflineMediaList;
	
	//data for adapter
	private List<String> mGroupData = new ArrayList<String>();
	private List<List<SetInfoStatusEp>> mChildData = new ArrayList<List<SetInfoStatusEp>>();
	
	//UI
	private TextView mTitleTopName;
	private View mTitleTop;
	private ExpandableLoadingListView mLoadingListView;
	private ExpandableListViewEx mListView;
	private SelectEpAdapter mSelectEpAdapter;
	
	//manager
//	private OfflineMediaManager mOfflineMediaManager;
	private DetailEpPlayManager mDetailEpPlayManager;
	private PlayHistoryManager mPlayHistoryManager;
	
	private int mCurCi;
	private boolean mCurCiLoading = false;
	private int mCurGroup = 0;
	
	private int mItemMaxEp = 80;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.com_media_expandable_list);
		init();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		mCurCiLoading = false;
		refresh();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if(mPlayHistoryManager != null) {
			mPlayHistoryManager.removeListener(mOnHistoryChangeListener);
		}
//		if (mOfflineMediaManager != null) {
//			mOfflineMediaManager.removeOfflineMediaListener(mOfflineMediaListener);
//		}
	}
	
	//init
	private void init() {
		initReceivedData();
		initManager();
		initUI();
	}
	
	private void initReceivedData() {
		Intent intent = getIntent();
		if(intent.getSerializableExtra(KEY_MEDIA_DETAIL_INFO2) instanceof MediaDetailInfo2){
		      mMediaDetailInfo = (MediaDetailInfo2) intent.getSerializableExtra(KEY_MEDIA_DETAIL_INFO2);
		      if(mMediaDetailInfo.mediaciinfo != null){
		          mSetList = mMediaDetailInfo.mediaciinfo.videos;
		      }
		}
	}
	
	private void initManager() {
//		mOfflineMediaManager = DKApp.getSingleton(OfflineMediaManager.class);
//		mOfflineMediaManager.addOfflineMediaListener(mOfflineMediaListener);
//		mOfflineMediaManager.loadOfflineMedia();
		mDetailEpPlayManager = new DetailEpPlayManager(this);
		mPlayHistoryManager = DKApp.getSingleton(PlayHistoryManager.class);
		mPlayHistoryManager.addListener(mOnHistoryChangeListener);
		mPlayHistoryManager.loadPlayHistory();
	}
	
	private void initUI() {
		mTitleTopName = (TextView) findViewById(R.id.title_top_name);
		mTitleTopName.setText(R.string.all_media);
		mTitleTop = findViewById(R.id.title_top);
		mTitleTop.setOnClickListener(mOnClickListener);
		initListView();
	}
	
	private void initListView() {
		mLoadingListView = (ExpandableLoadingListView) findViewById(R.id.com_media_expandable_list);
		mListView = mLoadingListView.getListView();
        int paddingLeft = getResources().getDimensionPixelSize(R.dimen.page_margin);
        int paddingBottom = getResources().getDimensionPixelSize(R.dimen.page_margin_top);
        int paddingTop = getResources().getDimensionPixelSize(R.dimen.size_5);
        mListView.setPadding(paddingLeft, paddingTop,  paddingLeft, paddingBottom);
        mListView.setVerticalScrollBarEnabled(false);
        mListView.setClipToPadding(false);
        mListView.setSelector(R.drawable.transparent);
		mListView.setGroupIndicator(getResources().getDrawable(R.drawable.transparent));
		
		mSelectEpAdapter = new SelectEpAdapter(this);
		mSelectEpAdapter.setOnItemClickListener(mOnItemClickListener);
		mListView.setAdapter(mSelectEpAdapter);
	}
	
	//packaged method
	private void refresh() {
		refreshCi();
		buildData();
		refreshListView();
	}
	
	private void refreshCi() {
		if(mPlayHistory instanceof OnlinePlayHistory) {
			OnlinePlayHistory onlinePlayHistory = (OnlinePlayHistory) mPlayHistory;
			mCurCi = onlinePlayHistory.mediaCi;
		} else {
			mCurCi = 1;
		}
	}
	
	private void refreshCurEp() {
		for(int i = 0; i < mChildData.size(); i++) {
			List<SetInfoStatusEp> list = mChildData.get(i);
			if(list == null) {
				break;
			}
			for(int j = 0; j < list.size(); j++) {
				SetInfoStatusEp setInfoStatusEp = list.get(j);
				if(setInfoStatusEp != null && (setInfoStatusEp.episode == mCurCi)) {
					mCurGroup = i;
					if(mCurCiLoading) {
						setInfoStatusEp.isLoading = true;
					} else {
						setInfoStatusEp.isLoading = false;
					}
				}
			}
		}
		refreshListView();
	}
	
	private void refreshListView() {
		mSelectEpAdapter.setData(mGroupData, mChildData);
		for(int i = 0; i < mSelectEpAdapter.getGroupCount(); i++) {
			if(i == mCurGroup) {
				mListView.expandGroup(i);
			} else {
				mListView.collapseGroup(i);
			}
		}
	}
	
	private void buildData() {
	    if(mSetList == null || mSetList.length == 0) {
	        return;
	    }
	    mGroupData.clear();
	    mChildData.clear();
	    int setSize = mSetList.length;
		int groupSize = (int) Math.ceil(setSize / (float)mItemMaxEp);
		for(int group = 0; group < groupSize; group++) {
			int startIndex = Math.min(group  * mItemMaxEp, setSize - 1);
			int endIndex = Math.min(startIndex + mItemMaxEp - 1, setSize - 1);
			String str = getResources().getString(R.string.ji);
			StringBuffer sb = new StringBuffer();
			if(startIndex == endIndex){
		         sb.append(mSetList[startIndex].ci);
			}else{
			    sb.append(mSetList[startIndex].ci);
			    sb.append('-');
			    sb.append(mSetList[endIndex].ci);
			}
			sb.append(str);
			mGroupData.add(sb.toString());
			List<SetInfoStatusEp> list = new ArrayList<SetInfoStatusEp>();
			mChildData.add(list);
			for(int ep = startIndex; ep <= endIndex; ep++) {
				SetInfoStatusEp setInfoStatusEp = new SetInfoStatusEp();
				MediaSetInfo setInfo = mSetList[ep];
				if(setInfo == null){
				    continue;
				}
				setInfoStatusEp.episode = setInfo.ci;
				setInfoStatusEp.setInfo = setInfo;
				if(setInfoStatusEp.episode == mCurCi) {
					mCurGroup = group;
					setInfoStatusEp.isSelected = true;
					if(mCurCiLoading) {
						setInfoStatusEp.isLoading = true;
					} else {
						setInfoStatusEp.isLoading = false;
					}
				} else {
					setInfoStatusEp.isSelected = false;
				}
				list.add(setInfoStatusEp);
			}
		}
	}
	
//	private void buildOfflineMedias(List<Object> offlineMediaLists) {
//		if(offlineMediaLists == null) {
//			return;
//		}
//		for(int i = 0; i < offlineMediaLists.size(); i++) {
//			Object object = offlineMediaLists.get(i);
//			if(object instanceof OfflineMediaList) {
//				OfflineMediaList offlineMediaList = (OfflineMediaList) object;
//				if(mMediaDetailInfo != null && offlineMediaList.getMediaId() == mMediaDetailInfo.mediaid) {
//					mOfflineMediaList = offlineMediaList;
//					return;
//				}
//			}
//		}
//	}
	
	private void playMedia(MediaSetInfo setInfo, OfflineMedia offlineMedia) {
	    if(setInfo == null || mMediaDetailInfo == null){
	        return;
	    }
		mCurCi = setInfo.ci;
		if(offlineMedia != null && offlineMedia.isFinished()) {
			new PlaySession(SelectEpActivity.this).startPlayerOffline(offlineMedia);
		} else {
			mCurCiLoading = true;
			mDetailEpPlayManager.playMedia(mMediaDetailInfo.mediainfo, MediaConstantsDef.MEDIA_TYPE_SERIES, 
			        setInfo, SourceTagValueDef.PHONE_V6_DETAIL_SELECT_EP_VALUE);
		}
	}
	
	//UI callback
//	private OfflineMediaListener mOfflineMediaListener = new OfflineMediaListener() {
//		
//		@Override
//		public void onOfflineMediaChanged() {
//			List<Object> offlineMediaLists = mOfflineMediaManager.getOfflineMediaList();
//			buildOfflineMedias(offlineMediaLists);
//			refreshListView();
//		}
//		
//		@Override
//		public void onOfflineMediaUpdate(OfflineMedia offlineMedia) {
//			refreshListView();
//		}
//	};
	
	private OnHistoryChangedListener mOnHistoryChangeListener = new OnHistoryChangedListener() {
		
		@Override
		public void onHistoryChanged(List<PlayHistory> historyList) {
			if(mMediaDetailInfo != null && mMediaDetailInfo.mediainfo != null) {
				PlayHistory playHistory = mPlayHistoryManager.getPlayHistoryById(
				        mMediaDetailInfo.mediainfo.mediaid);
				mPlayHistory = playHistory;
				refresh();
			}
		}
	};
	
	private OnClickListener mOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(v == mTitleTop) {
				SelectEpActivity.this.finish();
			}
		}
	};
	
	private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Object obj = parent.getItemAtPosition(position);
			Log.d("111111111", "TEST" + JsonSerializer.getInstance().serialize(obj));
			if(obj instanceof SetInfoStatusEp) {
				SetInfoStatusEp setInfoStatusEp = (SetInfoStatusEp) obj;
				mCurCi = setInfoStatusEp.episode;
				playMedia(setInfoStatusEp.setInfo, null);
				refreshCurEp();
			}
		}
	};
}
