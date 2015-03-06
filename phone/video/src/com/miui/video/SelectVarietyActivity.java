package com.miui.video;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.miui.video.adapter.SelectVarietyAdapter;
import com.miui.video.api.def.MediaConstantsDef;
import com.miui.video.base.BaseActivity;
import com.miui.video.controller.PlaySession;
import com.miui.video.local.OnlinePlayHistory;
import com.miui.video.local.PlayHistory;
import com.miui.video.local.PlayHistoryManager;
import com.miui.video.local.PlayHistoryManager.OnHistoryChangedListener;
import com.miui.video.offline.OfflineMedia;
import com.miui.video.statistic.SourceTagValueDef;
import com.miui.video.type.MediaDetailInfo;
import com.miui.video.type.MediaDetailInfo2;
import com.miui.video.type.MediaSetInfo;
import com.miui.video.type.MediaSetInfoList;
import com.miui.video.widget.ListViewEx;
import com.miui.video.widget.LoadingListView;
import com.miui.video.widget.detail.ep.DetailEpPlayManager;
import com.miui.video.widget.detail.ep.SetInfoStatusVariety;

/**
 *@author tangfuling
 *
 */

public class SelectVarietyActivity extends BaseActivity {
	
	public static final String KEY_PLAY_HISTORY = "play_history";
	public static final String KEY_MEDIA_DETAIL_INFO_2 = "media_detail_info_2";
	
	//received data
	private MediaDetailInfo mMediaDetailInfo;
	private MediaSetInfoList mMediaCiInfo;
	private MediaSetInfo[] mSetList;
	private PlayHistory mPlayHistory;
	
	//data from local
//	private OfflineMediaList mOfflineMediaList;
	
	//data for adapter
	private List<SetInfoStatusVariety> mSetInfoStatusVarietys = new ArrayList<SetInfoStatusVariety>();
	
	//UI
	private TextView mTitleTopName;
	private View mTitleTop;
	private LoadingListView mEpisodeLoadingListView;
	private ListViewEx mEpisodeListView;
	private SelectVarietyAdapter mVarietyAdapter;
	
	//manager
//	private OfflineMediaManager mOfflineMediaManager;
	private DetailEpPlayManager mDetailEpPlayManager;
	private PlayHistoryManager mPlayHistoryManager;
	
	private int mCi;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.com_media_list);
		init();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
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
		if(intent.getSerializableExtra(KEY_MEDIA_DETAIL_INFO_2) instanceof MediaDetailInfo2){
		      MediaDetailInfo2 mediaDetailInfo2 = (MediaDetailInfo2) intent.getSerializableExtra(KEY_MEDIA_DETAIL_INFO_2);
		      if(mediaDetailInfo2.mediaciinfo != null){
		          mSetList = mediaDetailInfo2.mediaciinfo.videos;
		      }
		      mMediaCiInfo = mediaDetailInfo2.mediaciinfo;
		      mMediaDetailInfo = mediaDetailInfo2.mediainfo;
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
		mEpisodeLoadingListView = (LoadingListView) findViewById(R.id.com_media_list);
		mEpisodeListView = mEpisodeLoadingListView.getListView();
		int paddingLeft = getResources().getDimensionPixelSize(R.dimen.page_margin);
		int paddingTop = getResources().getDimensionPixelSize(R.dimen.page_margin_top);
		mEpisodeListView.setPadding(paddingLeft, paddingTop,  paddingLeft, paddingTop);
		mEpisodeListView.setVerticalScrollBarEnabled(false);
		mEpisodeListView.setSelector(R.drawable.transparent);
		mEpisodeListView.setClipToPadding(false);
		
		mVarietyAdapter = new SelectVarietyAdapter(this);
		mEpisodeListView.setAdapter(mVarietyAdapter);
		mEpisodeListView.setOnItemClickListener(mOnItemClickListener);
	}
	
	//packaged method
	private void refresh() {
		refreshCi();
		refreshEpisodeListView();
	}
	
	private void refreshCi() {
		if(mPlayHistory instanceof OnlinePlayHistory) {
			OnlinePlayHistory onlinePlayHistory = (OnlinePlayHistory) mPlayHistory;
			mCi = onlinePlayHistory.mediaCi;
		} else {
		    if(mSetList != null && mSetList.length > 0){
		        if(mMediaCiInfo != null && mMediaCiInfo.isVariety()){
		            mCi = mSetList[mSetList.length - 1].ci;
		        }else{
		            mCi = mSetList[0].ci;
		        }
		    }
		}
	}
	
	private void refreshEpisodeListView() {
		if(mMediaDetailInfo != null) {
			refreshSetInfoStatusVarietys();
			mVarietyAdapter.setGroup(mSetInfoStatusVarietys);
		}
	}
	
	private void refreshSetInfoStatusVarietys() {
	    if(mSetList == null){
	        return;
	    }
        mSetInfoStatusVarietys.clear();
	    for(int i = mSetList.length - 1; i >=0; i--){
            MediaSetInfo mediaSetInfo = mSetList[i];
            if(mediaSetInfo != null) {
                SetInfoStatusVariety setInfoStatusVariety = new SetInfoStatusVariety();
                mSetInfoStatusVarietys.add(setInfoStatusVariety);
                setInfoStatusVariety.setInfo = mediaSetInfo;
                setInfoStatusVariety.episode = i;
                setInfoStatusVariety.date = mediaSetInfo.date;
                setInfoStatusVariety.videoName = mediaSetInfo.videoname;
                if(setInfoStatusVariety.episode == mCi) {
                    setInfoStatusVariety.isSelected = true;
                } else {
                    setInfoStatusVariety.isSelected = false;
                }
//              OfflineMedia offlineMedia = OfflineMediaHelper.getOfflineMediaByCi(
//                      mOfflineMediaList, setInfoStatusVariety.episode);
//              setInfoStatusVariety.offlineMedia = offlineMedia;
            }
	    }
	}
	
//	private MediaSetInfo getMediaSetInfoByCi(int ci) {
//		if(mMediaSetInfoList != null) {
//			for(int i = 0; i < mMediaSetInfoList.size(); i++) {
//				MediaSetInfo mediaSetInfo = mMediaSetInfoList.get(i);
//				if(mediaSetInfo != null && mediaSetInfo.ci == ci) {
//					return mediaSetInfo;
//				}
//			}
//		}
//		return null;
//	}
	
//	private SetInfoStatusVariety getSetInfoStatusVarietyByCi(int ci) {
//		for(int i = 0; i < mSetInfoStatusVarietys.size(); i++) {
//			SetInfoStatusVariety setInfoStatusVariety = mSetInfoStatusVarietys.get(i);
//			if(setInfoStatusVariety != null && setInfoStatusVariety.episode == ci) {
//				return setInfoStatusVariety;
//			}
//		}
//		return null;
//	}
	
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
	    if(setInfo == null){
	        return;
	    }
		this.mCi = setInfo.ci;
		if(offlineMedia != null && offlineMedia.isFinished()) {
			new PlaySession(this).startPlayerOffline(offlineMedia);
		} else {
			mDetailEpPlayManager.playMedia(mMediaDetailInfo, MediaConstantsDef.MEDIA_TYPE_VARIETY, 
			        setInfo, SourceTagValueDef.PHONE_V6_DETAIL_SELECT_VARIETY_VALUE);
		}
	}
	
	//UI callback
	private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if(parent != null) {
				Object obj = parent.getItemAtPosition(position);
				if(obj instanceof SetInfoStatusVariety) {
					SetInfoStatusVariety setInfoStatusVariety = (SetInfoStatusVariety) obj;
					playMedia(setInfoStatusVariety.setInfo, setInfoStatusVariety.offlineMedia);
				}
			}
		}
	};
	
	private OnClickListener mOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(v == mTitleTop) {
				SelectVarietyActivity.this.finish();
			}
		}
	};
	
//	private OfflineMediaListener mOfflineMediaListener = new OfflineMediaListener() {
//		
//		@Override
//		public void onOfflineMediaChanged() {
//			List<Object> offlineMediaLists = mOfflineMediaManager.getOfflineMediaList();
//			buildOfflineMedias(offlineMediaLists);
//			refreshEpisodeListView();
//		}
//
//		@Override
//		public void onOfflineMediaUpdate(OfflineMedia offlineMedia) {
//			refreshEpisodeListView();
//		}
//	};
	
	private OnHistoryChangedListener mOnHistoryChangeListener = new OnHistoryChangedListener() {
		
		@Override
		public void onHistoryChanged(List<PlayHistory> historyList) {
			if(mMediaDetailInfo != null) {
				PlayHistory playHistory = mPlayHistoryManager.getPlayHistoryById(mMediaDetailInfo.mediaid);
				mPlayHistory = playHistory;
				refresh();
			}
		}
	};
}
