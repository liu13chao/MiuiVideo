package com.miui.video.fragment;

import java.util.ArrayList;
import java.util.List;

import com.miui.video.DKApp;
import com.miui.video.R;
import com.miui.video.WebMediaActivity;
import com.miui.video.adapter.StatusButtonAdapter;
import com.miui.video.adapter.VarietyAdapter;
import com.miui.video.api.def.MediaConstantsDef;
import com.miui.video.common.PlaySession;
import com.miui.video.datasupply.MediaDetailInfoSupply;
import com.miui.video.datasupply.MediaUrlInfoListSupply;
import com.miui.video.datasupply.MediaUrlInfoListSupply.MediaUrlInfoListListener;
import com.miui.video.datasupply.MediaDetailInfoSupply.MediaDetailInfoDoneListener;
import com.miui.video.dialog.MediaDetailDialogFragment;
import com.miui.video.local.OnlinePlayHistory;
import com.miui.video.local.PlayHistory;
import com.miui.video.local.PlayHistoryManager;
import com.miui.video.local.PlayHistoryManager.OnHistoryChangedListener;
import com.miui.video.offline.OfflineMedia;
import com.miui.video.offline.OfflineMediaHelper;
import com.miui.video.offline.OfflineMediaList;
import com.miui.video.offline.OfflineMediaManager;
import com.miui.video.offline.OfflineMediaManager.OfflineMediaListener;
import com.miui.video.statistic.MediaFeeDef;
import com.miui.video.statistic.SourceTagValueDef;
import com.miui.video.type.MediaDetailInfo;
import com.miui.video.type.MediaDetailInfo2;
import com.miui.video.type.MediaInfo;
import com.miui.video.type.MediaSetInfo;
import com.miui.video.type.MediaUrlInfo;
import com.miui.video.type.MediaUrlInfoList;
import com.miui.video.util.Util;
import com.miui.video.widget.ListViewEx;
import com.miui.video.widget.LoadingListView;
import com.miui.video.widget.RetryView;
import com.miui.video.widget.RetryView.OnRetryLoadListener;
import com.miui.video.widget.statusbtn.StatusBtn;
import com.miui.video.widget.statusbtn.StatusBtn.OnStatusBtnClickListener;
import com.miui.video.widget.statusbtn.StatusBtnItem;
import com.miui.video.widget.statusbtn.StatusBtnItemList;
import com.miui.video.widget.statusbtn.StatusBtnLong;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/**
 *@author tangfuling
 *
 */

public class DetailEpisodeFragment extends Fragment {
	
	private Context mContext;
	
	//UI
	private LoadingListView mEpisodeLoadingListView;
	private ListViewEx mEpisodeListView;
	private View mEpisodeLoadView;
	private View mEpisodeEmptyView;
	private RetryView mEpisodeRetryView;
	private StatusButtonAdapter mStatusButtonAdapter;
	private VarietyAdapter mVarietyAdapter;
	private View mHeaderView;
	
	//received data
	private MediaInfo mMediaInfo;
		
	//data from network
	private MediaDetailInfo2 mMediaDetailInfo;
	private MediaUrlInfo mMediaUrlInfo;
	private MediaUrlInfoList mMediaUrlInfoList;
	private List<MediaSetInfo> mMediaSetInfoList;
	
	//data from local
	private OfflineMediaList mOfflineMediaList;
	private PlayHistory mPlayHistory;
	
	//data for adapter
	private StatusBtnItemList mStatusBtnItemListBtn = new StatusBtnItemList();
	private StatusBtnItemList mStatusBtnItemListVariety = new StatusBtnItemList();
	
	//data supply
	private MediaDetailInfoSupply mMediaDetailInfoSupply;
	private MediaUrlInfoListSupply mMediaUrlInfoListSupply;
	
	//flags
	private boolean mCanPlay = false;
	private boolean mCurEpisodeLoading = false;
	
	private int mStyle = MediaConstantsDef.MEDIA_TYPE_VARIETY;
	private int mCi = 1;
	
	//listener
	private ArrayList<OnMediaPlayListener> listeners = new ArrayList<OnMediaPlayListener>();
	
	//manager
	private OfflineMediaManager mOfflineMediaManager;
	private PlayHistoryManager mPlayHistoryManager;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getActivity();
		Bundle bundle = getArguments();
		if(bundle != null) {
			Object mediaInfo = bundle.getSerializable(MediaDetailDialogFragment.KEY_MEDIA_INFO);
			if(mediaInfo instanceof MediaInfo) {
				this.mMediaInfo = (MediaInfo) mediaInfo;
			}
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mEpisodeLoadingListView = new LoadingListView(mContext);
		return mEpisodeLoadingListView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		init();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		mOfflineMediaManager.loadOfflineMedia();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if(mMediaDetailInfoSupply != null) {
			mMediaDetailInfoSupply.removeMediaDetailInfoDoneListener(mMediaDetailInfoDoneListener);
		}
		if(mPlayHistoryManager != null) {
			mPlayHistoryManager.removeListener(mOnHistoryChangeListener);
		}
		if (mOfflineMediaManager != null) {
			mOfflineMediaManager.removeOfflineMediaListener(mOfflineMediaListener);
		}
	}
	
	//public method
	public void addMediaPlayListener(OnMediaPlayListener onMediaPlayListener) {
		if(onMediaPlayListener != null && !listeners.contains(onMediaPlayListener)) {
			listeners.add(onMediaPlayListener);
		}
	}
	
	public void removeMediaPlayListener(OnMediaPlayListener onMediaPlayListener) {
		if(onMediaPlayListener != null) {
			listeners.remove(onMediaPlayListener);
		}
	}
	
	//init
	private void init() {
		initManager();
		initUI();
		initData();
	}
	
	private void initManager() {
		mOfflineMediaManager = DKApp.getSingleton(OfflineMediaManager.class);
		mOfflineMediaManager.addOfflineMediaListener(mOfflineMediaListener);
		mPlayHistoryManager = DKApp.getSingleton(PlayHistoryManager.class);
		mPlayHistoryManager.addListener(mOnHistoryChangeListener);
	}
	
	private void initUI() {
		mEpisodeListView = mEpisodeLoadingListView.getListView();
		mStatusButtonAdapter = new StatusButtonAdapter(mContext);
		mStatusButtonAdapter.setOnStatusBtnClickListener(mOnStatusBtnClickListener);
		mVarietyAdapter = new VarietyAdapter(mContext);
		mEpisodeListView.setOnItemClickListener(mOnItemClickListener);
		
		mHeaderView = new View(mContext);
		int height = mContext.getResources().getDimensionPixelSize(R.dimen.detail_episode_top_margin);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, height);
		mHeaderView.setLayoutParams(params);
		mEpisodeListView.addHeaderView(mHeaderView);
		
		mEpisodeLoadView = View.inflate(mContext, R.layout.load_view_black, null);
		mEpisodeLoadingListView.setLoadingView(mEpisodeLoadView);
		
		mEpisodeEmptyView = View.inflate(mContext, R.layout.empty_view_black, null);
		TextView emptyHint = (TextView) mEpisodeEmptyView.findViewById(R.id.empty_hint);
		emptyHint.setText(mContext.getResources().getString(R.string.detail_episode_empty_hint));
		
		mEpisodeRetryView = new RetryView(mContext, RetryView.STYLE_BLACK);
		mEpisodeRetryView.setOnRetryLoadListener(new OnRetryLoadListener() {
			@Override
			public void OnRetryLoad(View vClicked) {
				getMediaDetailData();
			}
		});
	}
	
	private void initData() {
		initDataSupply();
		mEpisodeLoadingListView.setShowLoading(true);
	}
	
	private void initDataSupply() {
		if(mMediaDetailInfoSupply == null) {
			mMediaDetailInfoSupply = DKApp.getSingleton(MediaDetailInfoSupply.class);
			mMediaDetailInfoSupply.addMediaDetailInfoDoneListener(mMediaDetailInfoDoneListener);
		}
		if(mMediaUrlInfoListSupply == null) {
			mMediaUrlInfoListSupply = new MediaUrlInfoListSupply();
			mMediaUrlInfoListSupply.addListener(mMediaUrlInfoListListener);
		}
	}
	
	//get data
	private void getMediaDetailData() {
		if(mMediaDetailInfo == null) {
			if(mMediaInfo != null) {
				boolean getAll = true;
				mEpisodeLoadingListView.setShowLoading(true);
				mMediaDetailInfoSupply.getMediaDetailInfo(mMediaInfo.mediaid, getAll, 
							MediaFeeDef.MEDIA_ALL, null);
			}
		}
	}
	
	private void getMediaUrlInfoList(int ci) {
		if(mMediaInfo != null) {
			if(mMediaUrlInfo != null && mCi == ci) {
				startWebMediaActivity();
			} else {
				if(mCi != ci) {
					mCurEpisodeLoading = true;
					mCi = ci;
				}
				mMediaUrlInfoListSupply.getMediaUrlInfoList(mMediaInfo.mediaid, mCi, -1);
			}
		}
	}
	
	//packaged method
	private void refreshEpisodeListView(boolean isError) {
		if(mMediaDetailInfo != null && mMediaDetailInfo.mediainfo != null 
				&& mMediaDetailInfo.mediaciinfo != null) {
			refreshStatusBtnItemList();
			if(mStyle == MediaConstantsDef.MEDIA_TYPE_VARIETY) {
				mEpisodeListView.removeHeaderView(mHeaderView);
				if(mEpisodeListView.getAdapter() == null) {
					mEpisodeListView.setAdapter(mVarietyAdapter);
				}
				mVarietyAdapter.setData(mStatusBtnItemListVariety);
			} else {
				if(mEpisodeListView.getAdapter() == null) {
					mEpisodeListView.setAdapter(mStatusButtonAdapter);
				}
				mStatusButtonAdapter.setData(mStatusBtnItemListBtn);
			}
			return;
		}
		
		if(isError){
			mEpisodeLoadingListView.setEmptyView(mEpisodeRetryView);
		} else{
			mEpisodeLoadingListView.setEmptyView(mEpisodeEmptyView);
		}
	}
	
	private void refreshEpisodeListView() {
		if(mMediaDetailInfo != null && mMediaDetailInfo.mediainfo != null 
				&& mMediaDetailInfo.mediaciinfo != null) {
			refreshStatusBtnItemList();
			if(mStyle == MediaConstantsDef.MEDIA_TYPE_VARIETY) {
				mEpisodeListView.removeHeaderView(mHeaderView);
				if(mEpisodeListView.getAdapter() == null) {
					mEpisodeListView.setAdapter(mVarietyAdapter);
				}
				mVarietyAdapter.setData(mStatusBtnItemListVariety);
			} else {
				if(mEpisodeListView.getAdapter() == null) {
					mEpisodeListView.setAdapter(mStatusButtonAdapter);
				}
				mStatusButtonAdapter.setData(mStatusBtnItemListBtn);
			}
		}
	}
	
	private void refreshStatusBtn(StatusBtn statusBtn, StatusBtnItem statusBtnItem) {
		if(statusBtn == null || statusBtnItem == null) {
			return;
		}
		refreshStatusBtnItem(statusBtnItem);
		statusBtn.setStatusBtnItem(statusBtnItem);
	}
	
	private void refreshStatusBtnLong(StatusBtnLong statusBtnLong, StatusBtnItem statusBtnItem) {
		if(statusBtnLong == null || statusBtnItem == null) {
			return;
		}
		refreshStatusBtnItem(statusBtnItem);
		statusBtnLong.setStatusBtnItem(statusBtnItem);
	}
	
	private void refreshStatusBtnItemList() {
		if(mStyle == MediaConstantsDef.MEDIA_TYPE_TELEPLAY) {
			if(mMediaDetailInfo != null) {
				MediaDetailInfo mediaDetailInfo = mMediaDetailInfo.mediainfo;
				if(mediaDetailInfo != null) {
					for(int i = 1; i <= mediaDetailInfo.setnow; i++) {
						StatusBtnItem statusBtnItem = mStatusBtnItemListBtn.getStatusBtnItem(i);
						if(statusBtnItem == null) {
							statusBtnItem = new StatusBtnItem();
							mStatusBtnItemListBtn.add(statusBtnItem);
						}
						statusBtnItem.showIconOnly = true;
						statusBtnItem.isShowDownloadStatus = false;
						statusBtnItem.episode = i;
						
						MediaSetInfo mediaSetInfo = getMediaSetInfoByCi(statusBtnItem.episode);
						if(mediaSetInfo != null) {
							statusBtnItem.clickable = true;
						} else {
							statusBtnItem.clickable = false;
						}
						
						refreshStatusBtnItem(statusBtnItem);
					}
					return;
				}
			}
		} else if(mStyle == MediaConstantsDef.MEDIA_TYPE_VARIETY) {
			if(mMediaDetailInfo != null) {
				MediaDetailInfo mediaDetailInfo = mMediaDetailInfo.mediainfo;
				if(mediaDetailInfo != null) {
					for(int i = 1; i <= mediaDetailInfo.setnow; i++) {
						MediaSetInfo mediaSetInfo = getMediaSetInfoByCi(i);
						if(mediaSetInfo != null) {
							StatusBtnItem statusBtnItem = mStatusBtnItemListVariety.getStatusBtnItem(i);
							if(statusBtnItem == null) {
								statusBtnItem = new StatusBtnItem();
								mStatusBtnItemListVariety.add(statusBtnItem);
							}
							statusBtnItem.isShowDownloadStatus = false;
							statusBtnItem.episode = i;
							statusBtnItem.clickable = true;
							statusBtnItem.date = mediaSetInfo.date;
							statusBtnItem.videoName = mediaSetInfo.videoname;
							refreshStatusBtnItem(statusBtnItem);
						}
					}
					return;
				}
			}
		}
		mStatusBtnItemListBtn.clear();
		mStatusBtnItemListVariety.clear();
	}
	
	private void refreshStatusBtnItem(StatusBtnItem statusBtnItem) {
		if(statusBtnItem == null) {
			return;
		}
		if(mCi == statusBtnItem.episode) {
			if(mCurEpisodeLoading) {
				statusBtnItem.isLoading = true;
				statusBtnItem.isPlaying = false;
			} else {
				statusBtnItem.isLoading = false;
				statusBtnItem.isPlaying = true;
			}
		} else {
			statusBtnItem.isPlaying = false;
			statusBtnItem.isLoading = false;
		}
		
		OfflineMedia offlineMedia = OfflineMediaHelper.getOfflineMediaByCi(
				mOfflineMediaList, statusBtnItem.episode);
		statusBtnItem.offlineMedia = offlineMedia;
		statusBtnItem.refreshUiStatus();
	}
	
	private void buildMediaSetInfoList() {
		if(mMediaDetailInfo == null || mMediaDetailInfo.mediaciinfo == null) {
			return;
		}

		mStyle = mMediaDetailInfo.mediaciinfo.style;
		mMediaSetInfoList = mMediaDetailInfo.mediaciinfo.getAvailableCiList();
		
		if(mPlayHistory instanceof OnlinePlayHistory) {
			OnlinePlayHistory onlinePlayHistory = (OnlinePlayHistory) mPlayHistory;
			mCi = onlinePlayHistory.mediaCi;
		} else {
			if(mStyle == MediaConstantsDef.MEDIA_TYPE_TELEPLAY) {
				mCi = 1;
			} else if(mStyle == MediaConstantsDef.MEDIA_TYPE_VARIETY) {
				if(mMediaSetInfoList != null) {
					mCi = mMediaSetInfoList.size();
				}
			}
		}
	}
	
	private MediaSetInfo getMediaSetInfoByCi(int ci) {
		if(mMediaSetInfoList != null) {
			for(int i = 0; i < mMediaSetInfoList.size(); i++) {
				MediaSetInfo mediaSetInfo = mMediaSetInfoList.get(i);
				if(mediaSetInfo != null && mediaSetInfo.ci == ci) {
					return mediaSetInfo;
				}
			}
		}
		return null;
	}
	
	private void notifyMediaPlay(int ci) {
		for(OnMediaPlayListener listener : listeners) {
			listener.onMediaPlay(ci);
		}
	}
	
	private void playMedia(int ci, OfflineMedia offlineMedia) {
		mCanPlay = true;
		if(offlineMedia != null && offlineMedia.isFinish()) {
			PlaySession playSession = DKApp.getSingleton(PlaySession.class);
			playSession.startPlayerOffline(offlineMedia);
			mCi = ci;
		} else {
			getMediaUrlInfoList(ci);
		}
		notifyMediaPlay(ci);
	}
	
	private String formatUrl(String url) {
		if (!Util.isEmpty(url)) {
			int pos = url.lastIndexOf("http://");
			if (pos >= 0) {
				url = url.substring(pos, url.length());
			}
		}
		return url;
	}
	
	private void startWebMediaActivity() {
		if(mMediaUrlInfo != null && mCanPlay) {
			Intent intent = new Intent(mContext, WebMediaActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.putExtra(WebMediaActivity.KEY_MEDIA_INFO, mMediaInfo);
			intent.putExtra(WebMediaActivity.KEY_SOURCE_PATH, SourceTagValueDef.PAD_DETAIL_VALUE);
			intent.putExtra(WebMediaActivity.KEY_CI, mCi);
			intent.putExtra(WebMediaActivity.KEY_CLARITY, mMediaUrlInfo.clarity);
			intent.putExtra(WebMediaActivity.KEY_SOURCE, mMediaUrlInfo.mediaSource);
			intent.putExtra(WebMediaActivity.KEY_URL, formatUrl(mMediaUrlInfo.mediaUrl));
			intent.putExtra(WebMediaActivity.KEY_MEDIA_SET_STYLE, mStyle);
			mContext.startActivity(intent);
		}
	}
	
	private void buildOfflineMedias(List<Object> offlineMediaLists) {
		if(offlineMediaLists == null) {
			return;
		}
		for(int i = 0; i < offlineMediaLists.size(); i++) {
			Object object = offlineMediaLists.get(i);
			if(object instanceof OfflineMediaList) {
				OfflineMediaList offlineMediaList = (OfflineMediaList) object;
				if(mMediaInfo != null && offlineMediaList.getMediaId() == mMediaInfo.mediaid) {
					mOfflineMediaList = offlineMediaList;
					return;
				}
			}
		}
	}

	//UI callback
	private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if(view instanceof StatusBtnLong) {
				StatusBtnLong statusBtnLong = (StatusBtnLong) view;
				StatusBtnItem statusBtnItem = statusBtnLong.getStatusBtnItem();
				if(statusBtnItem != null) {
					playMedia(statusBtnItem.episode, statusBtnItem.offlineMedia);
					refreshStatusBtnLong(statusBtnLong, statusBtnItem);
				}
			}
		}
	};

	private OnStatusBtnClickListener mOnStatusBtnClickListener = new OnStatusBtnClickListener() {

		@Override
		public void onStatusBtnClick(StatusBtn statusBtn) {
			if(statusBtn != null) {
				StatusBtnItem statusBtnItem = statusBtn.getStatusBtnItem();
				if(statusBtnItem != null) {
					playMedia(statusBtnItem.episode, statusBtnItem.offlineMedia);
					refreshStatusBtn(statusBtn, statusBtnItem);
				}
			}
		}
	};
	
	//data callback
	private MediaDetailInfoDoneListener mMediaDetailInfoDoneListener = new MediaDetailInfoDoneListener() {
		
		@Override
		public void onMediaDetailInfoDone(MediaDetailInfo2 mediaDetailInfo,
				boolean isError) {
			mEpisodeLoadingListView.setShowLoading(false);
			
			mMediaDetailInfo = mediaDetailInfo;
			buildMediaSetInfoList();
			if(mMediaInfo != null && mMediaDetailInfo != null) {
				if(mMediaDetailInfo.mediainfo != null) {
					mMediaInfo.smallImageURL = mMediaDetailInfo.mediainfo.smallImageURL;
				}
			}
			refreshEpisodeListView(isError);
		}
	};

	private MediaUrlInfoListListener mMediaUrlInfoListListener = new MediaUrlInfoListListener() {
		
		@Override
		public void onMediaUrlInfoListDone(MediaUrlInfoList mediaUrlInfoList,
				boolean isError) {
			if(!isError) {
				mCurEpisodeLoading = false;
			}
			refreshEpisodeListView();
			mMediaUrlInfoList = mediaUrlInfoList;
			mMediaUrlInfo = mMediaUrlInfoListSupply.filterMediaUrlInfoList(mMediaUrlInfoList, -1);
			startWebMediaActivity();
		}
	};
	
	private OnHistoryChangedListener mOnHistoryChangeListener = new OnHistoryChangedListener() {
		
		@Override
		public void onHistoryChanged(List<PlayHistory> historyList) {
			if(mMediaInfo != null) {
				PlayHistory playHistory = mPlayHistoryManager.getPlayHistoryById(mMediaInfo.mediaid);
				mPlayHistory = playHistory;
				if(mPlayHistory instanceof OnlinePlayHistory) {
					OnlinePlayHistory onlinePlayHistory = (OnlinePlayHistory) mPlayHistory;
					mCi = onlinePlayHistory.mediaCi;
					notifyMediaPlay(mCi);
				}
			}
		}
	};
	
	private OfflineMediaListener mOfflineMediaListener = new OfflineMediaListener() {
		
		@Override
		public void onOfflineMediaChanged() {
			List<Object> offlineMediaLists = mOfflineMediaManager.getOfflineMediaList();
			buildOfflineMedias(offlineMediaLists);
			refreshEpisodeListView();
		}
		
		@Override
		public void onOfflineMediaUpdate(OfflineMedia offlineMedia) {
			refreshEpisodeListView();
		}
	
	};
	
	//self def class
	public interface OnMediaPlayListener {
		public void onMediaPlay(int ci);
	}
}
