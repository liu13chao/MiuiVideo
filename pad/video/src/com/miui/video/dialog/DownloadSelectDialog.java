package com.miui.video.dialog;

import java.util.List;

import com.miui.video.DKApp;
import com.miui.video.R;
import com.miui.video.adapter.StatusButtonAdapter;
import com.miui.video.adapter.VarietyAdapter;
import com.miui.video.api.DKApi;
import com.miui.video.api.def.MediaConstantsDef;
import com.miui.video.base.BaseDialog;
import com.miui.video.datasupply.MediaDetailInfoSupply;
import com.miui.video.datasupply.MediaDetailInfoSupply.MediaDetailInfoDoneListener;
import com.miui.video.offline.OfflineMedia;
import com.miui.video.offline.OfflineMediaHelper;
import com.miui.video.offline.OfflineMediaList;
import com.miui.video.offline.OfflineMediaManager;
import com.miui.video.offline.OfflineMediaManager.OfflineMediaListener;
import com.miui.video.statistic.ComUserDataStatisticInfo;
import com.miui.video.statistic.ComUserDataTypeValueDef;
import com.miui.video.statistic.MediaFeeDef;
import com.miui.video.statistic.SourceTagValueDef;
import com.miui.video.type.MediaDetailInfo;
import com.miui.video.type.MediaDetailInfo2;
import com.miui.video.type.MediaSetInfo;
import com.miui.video.util.AlertMessage;
import com.miui.video.widget.ListViewEx;
import com.miui.video.widget.LoadingListView;
import com.miui.video.widget.RetryView;
import com.miui.video.widget.RetryView.OnRetryLoadListener;
import com.miui.video.widget.statusbtn.StatusBtn;
import com.miui.video.widget.statusbtn.StatusBtn.OnStatusBtnClickListener;
import com.miui.video.widget.statusbtn.StatusBtnItem;
import com.miui.video.widget.statusbtn.StatusBtnItemList;
import com.miui.video.widget.statusbtn.StatusBtnLong;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 *@author tangfuling
 *
 */

public class DownloadSelectDialog extends BaseDialog {
	
	public static final String KEY_MEDIA_DETAIL_INFO2 = "key_media_detail_info2";
	public static final String KEY_MEDIA_ID = "key_media_id";
	
	//UI
	private LoadingListView mLoadingListView;
	private ListViewEx mListViewEx;
	private View mLoadView;
	private RetryView mRetryView;
	private StatusButtonAdapter mStatusBtnAdapter;
	private VarietyAdapter mVarietyAdapter;
	
	private Button mBtnBack;
	private Button mBtnManage;
	private TextView mTitleTop;
	
	//received data
	private MediaDetailInfo2 mMediaDetailInfo2;
	private List<MediaSetInfo> mMediaSetInfoList;
	private int[] mAllowAbleSources;
	
	//data from local
	private OfflineMediaList mOfflineMediaList;
	private StatusBtnItemList mStatusBtnItemListBtn = new StatusBtnItemList();
	private StatusBtnItemList mStatusBtnItemListVariety = new StatusBtnItemList();
	
	private MediaDetailInfoSupply mMediaDetailInfoSupply;
	
	//manager
	private OfflineMediaManager mOfflineMediaManager;
	
	private int mMediaId;
	private int mPreferSource = -1;
	
	//flags
	private boolean mInManageMode;
	
	private int mStyle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.download_select);
		init();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mOfflineMediaManager.removeOfflineMediaListener(mOfflineMediaListener);
	}
	
	//init
	private void init() {
		initReceivedData();
		initUI();
		initManager();
		
		if(mMediaDetailInfo2 != null) {
			initData();
		} else {
			getMediaDetailInfo();
		}
	}
	
	private void initReceivedData() {
		Intent intent = getIntent();
		Object obj = intent.getSerializableExtra(KEY_MEDIA_DETAIL_INFO2);
		if(obj instanceof MediaDetailInfo2) {
			mMediaDetailInfo2 = (MediaDetailInfo2) obj;
			if(mMediaDetailInfo2.mediaciinfo != null) {
				mMediaId = mMediaDetailInfo2.mediainfo.mediaid;
			}
		} else {
			mMediaId = intent.getIntExtra(KEY_MEDIA_ID, -1);
		}
	}
	
	private void initManager() {
		mOfflineMediaManager = DKApp.getSingleton(OfflineMediaManager.class);
		mOfflineMediaManager.addOfflineMediaListener(mOfflineMediaListener);
	}
	
	private void initUI() {
		mBtnBack = (Button) findViewById(R.id.download_select_back);
		mBtnBack.setOnClickListener(mOnClickListener);
		mBtnManage = (Button) findViewById(R.id.download_select_manage);
		mBtnManage.setOnClickListener(mOnClickListener);
		mTitleTop = (TextView) findViewById(R.id.download_select_top_title);
		
		initListView();
	}
	
	private void initListView() {
		mLoadingListView = (LoadingListView) findViewById(R.id.download_select_list);
		mListViewEx = mLoadingListView.getListView();
		mStatusBtnAdapter = new StatusButtonAdapter(this);
		mStatusBtnAdapter.setOnStatusBtnClickListener(mOnStatusBtnClickListener);
		mVarietyAdapter = new VarietyAdapter(this);
		mListViewEx.setOnItemClickListener(mOnItemClickListener);
		
		mLoadView = View.inflate(this, R.layout.load_view_black, null);
		mLoadingListView.setLoadingView(mLoadView);
		
		mRetryView = new RetryView(this, RetryView.STYLE_BLACK);
		mRetryView.setOnRetryLoadListener(new OnRetryLoadListener() {
			@Override
			public void OnRetryLoad(View vClicked) {
				getMediaDetailInfo();
			}
		});
	}
	
	private void initData() {
		initMediaSetInfoList();
		loadOfflineMedia();
	}
	
	private void initMediaSetInfoList() {
		if(mMediaDetailInfo2 == null || mMediaDetailInfo2.mediaciinfo == null) {
			return;
		}
		if(mMediaDetailInfo2.mediainfo != null) {
			mAllowAbleSources = mMediaDetailInfo2.mediainfo.media_available_download_source;
		}
		mStyle = mMediaDetailInfo2.mediaciinfo.style;
		mMediaSetInfoList = mMediaDetailInfo2.mediaciinfo.getAvailableCiList();
		
		if(mStyle == MediaConstantsDef.MEDIA_TYPE_VARIETY) {
			mListViewEx.setAdapter(mVarietyAdapter);
		} else if(mStyle == MediaConstantsDef.MEDIA_TYPE_TELEPLAY){
			mListViewEx.setAdapter(mStatusBtnAdapter);
		}
	}
	
	//get data
	private void loadOfflineMedia() {
		mOfflineMediaManager.loadOfflineMedia();
	}
	
	private void getMediaDetailInfo() {
		mLoadingListView.setShowLoading(true);
		if(mMediaDetailInfoSupply == null) {
			mMediaDetailInfoSupply = new MediaDetailInfoSupply(this);
			mMediaDetailInfoSupply.addMediaDetailInfoDoneListener(mMediaDetailInfoListener);
		}
		mMediaDetailInfoSupply.getMediaDetailInfo(mMediaId, true, 
				MediaFeeDef.MEDIA_ALL, "");
	}
	
	//packaged method
	private void refreshEpisodeListView() {
		refreshEpisodeListView(false);
	}
	
	private void refreshEpisodeListView(boolean ignoreOfflineMediaChange) {
		if(mMediaDetailInfo2 != null && mMediaDetailInfo2.mediainfo != null 
				&& mMediaDetailInfo2.mediaciinfo != null) {
			refreshStatusBtnItemList(ignoreOfflineMediaChange);
			if(mStyle == MediaConstantsDef.MEDIA_TYPE_VARIETY) {
				mVarietyAdapter.setData(mStatusBtnItemListVariety);
			} else if(mStyle == MediaConstantsDef.MEDIA_TYPE_TELEPLAY){
				mStatusBtnAdapter.setData(mStatusBtnItemListBtn);
			}
		} else {
			mLoadingListView.setEmptyView(mRetryView);
		}
	}
	
	private void refreshStatusBtnItemList(boolean ignoreOfflineMediaChange) {
		if(mMediaDetailInfo2 == null) {
			return;
		}
		
		if(mStyle == MediaConstantsDef.MEDIA_TYPE_TELEPLAY) {
			MediaDetailInfo mediaDetailInfo = mMediaDetailInfo2.mediainfo;
			if(mediaDetailInfo != null) {
				for(int i = 1; i <= mediaDetailInfo.setnow; i++) {
					StatusBtnItem statusBtnItem = mStatusBtnItemListBtn.getStatusBtnItem(i);
					if(statusBtnItem == null) {
						statusBtnItem = new StatusBtnItem();
						mStatusBtnItemListBtn.add(statusBtnItem);
					}
					statusBtnItem.isShowDownloadStatus = true;
					statusBtnItem.episode = i;
					
					MediaSetInfo mediaSetInfo = getMediaSetInfoByCi(statusBtnItem.episode);
					if(mediaSetInfo != null) {
						statusBtnItem.clickable = true;
					} else {
						statusBtnItem.clickable = false;
					}
					
					refreshStatusBtnItem(statusBtnItem, ignoreOfflineMediaChange);
				}
				return;
			}
		} else if(mStyle == MediaConstantsDef.MEDIA_TYPE_VARIETY) {
			MediaDetailInfo mediaDetailInfo = mMediaDetailInfo2.mediainfo;
			if(mediaDetailInfo != null) {
				for(int i = 1; i <= mediaDetailInfo.setnow; i++) {
					MediaSetInfo mediaSetInfo = getMediaSetInfoByCi(i);
					if(mediaSetInfo != null) {
						StatusBtnItem statusBtnItem = mStatusBtnItemListVariety.getStatusBtnItem(i);
						if(statusBtnItem == null) {
							statusBtnItem = new StatusBtnItem();
							mStatusBtnItemListVariety.add(statusBtnItem);
						}
						statusBtnItem.isShowDownloadStatus = true;
						statusBtnItem.episode = i;
						statusBtnItem.clickable = true;
						statusBtnItem.date = mediaSetInfo.date;
						statusBtnItem.videoName = mediaSetInfo.videoname;
						refreshStatusBtnItem(statusBtnItem, ignoreOfflineMediaChange);
					}
				}
				return;
			}
		}
		mStatusBtnItemListBtn.clear();
		mStatusBtnItemListVariety.clear();
	}
	
	private void refreshStatusBtnItem(StatusBtnItem statusBtnItem, boolean ignoreOfflineMediaChange) {
		if(statusBtnItem == null) {
			return;
		}
		OfflineMedia offlineMedia = OfflineMediaHelper.getOfflineMediaByCi(
				mOfflineMediaList, statusBtnItem.episode);
		statusBtnItem.offlineMedia = offlineMedia;
		statusBtnItem.refreshUiStatus(ignoreOfflineMediaChange);
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
	
	//处理单个点击事件
	private void doStatusBtnClick(StatusBtn statusBtn) {
		if(statusBtn == null || statusBtn.getStatusBtnItem() == null) {
			return;
		}
		
		StatusBtnItem statusBtnItem = statusBtn.getStatusBtnItem();
		int ci = statusBtnItem.episode;
		OfflineMedia offlineMedia = OfflineMediaHelper.getOfflineMediaByCi(
				mOfflineMediaList, ci);
		if(offlineMedia == null) {
			if(!statusBtnItem.isInEditMode) {
				addToDownloadList(ci, -1);
				//如果这里不更新UI，那么需要在add成功之后，才会更新UI，会有延迟
				statusBtnItem.uiStatus = StatusBtn.UI_STATUS_WAITING;
			}
		} else {
			if(!statusBtnItem.isInEditMode) {
				int uiStatus = statusBtnItem.uiStatus;
				switch (uiStatus) {
				case StatusBtn.UI_STATUS_DOWNLOAD:
					pauseOfflineMedia(offlineMedia.mediaId, offlineMedia.episode);
					break;
				case StatusBtn.UI_STATUS_WAITING:
					startOfflineMedia(offlineMedia.mediaId, offlineMedia.episode);
					break;
				case StatusBtn.UI_STATUS_PAUSE:
					startOfflineMedia(offlineMedia.mediaId, offlineMedia.episode);
					break;
				case StatusBtn.UI_STATUS_CONNECT:
					startOfflineMedia(offlineMedia.mediaId, offlineMedia.episode);
					break;
				case StatusBtn.UI_STATUS_ERROR:
					startOfflineMedia(offlineMedia.mediaId, offlineMedia.episode);
					break;
				
				case StatusBtn.UI_STATUS_DONE:
					AlertMessage.show(R.string.download_is_success);
					break;
					
				default:
					break;
				} 
			} else {
				int uiStatus = statusBtnItem.uiStatus;
				if(uiStatus == StatusBtn.UI_STATUS_DELETE) {
					delFromDownloadList(ci);
					//如果这里不更新UI，那么需要在delete成功之后，才会更新UI，会有延迟
					statusBtnItem.uiStatus = StatusBtn.UI_STATUS_TEXT_ONLY;
				}
			}
		}
		
		statusBtn.setStatusBtnItem(statusBtnItem);
	}
	
	private void doItemClick(StatusBtnLong statusBtnLong) {
		if(statusBtnLong == null || statusBtnLong.getStatusBtnItem() == null) {
			return;
		}
		
		StatusBtnItem statusBtnItem = statusBtnLong.getStatusBtnItem();
		int ci = statusBtnItem.episode;
		OfflineMedia offlineMedia = OfflineMediaHelper.getOfflineMediaByCi(
				mOfflineMediaList, ci);
		if(offlineMedia == null) {
			if(!statusBtnItem.isInEditMode) {
				addToDownloadList(ci, -1);
				//如果这里不更新UI，那么需要在add成功之后，才会更新UI，会有延迟
				statusBtnItem.uiStatus = StatusBtn.UI_STATUS_WAITING;
			}
		} else {
			if(!statusBtnItem.isInEditMode) {
				int uiStatus = statusBtnItem.uiStatus;
				switch (uiStatus) {
				case StatusBtn.UI_STATUS_DOWNLOAD:
					pauseOfflineMedia(offlineMedia.mediaId, offlineMedia.episode);
					break;
				case StatusBtn.UI_STATUS_WAITING:
					startOfflineMedia(offlineMedia.mediaId, offlineMedia.episode);
					break;
				case StatusBtn.UI_STATUS_PAUSE:
					startOfflineMedia(offlineMedia.mediaId, offlineMedia.episode);
					break;
				case StatusBtn.UI_STATUS_CONNECT:
					startOfflineMedia(offlineMedia.mediaId, offlineMedia.episode);
					break;
				case StatusBtn.UI_STATUS_ERROR:
					startOfflineMedia(offlineMedia.mediaId, offlineMedia.episode);
					break;
					
				case StatusBtn.UI_STATUS_DONE:
					AlertMessage.show(R.string.download_is_success);
					break;
					
				default:
					break;
				} 
			} else {
				int uiStatus = statusBtnItem.uiStatus;
				if(uiStatus == StatusBtn.UI_STATUS_DELETE) {
					delFromDownloadList(ci);
					//如果这里不更新UI，那么需要在delete成功之后，才会更新UI，会有延迟
					statusBtnItem.uiStatus = StatusBtn.UI_STATUS_TEXT_ONLY;
				}
			}
		}
		
		statusBtnLong.setStatusBtnItem(statusBtnItem);
	}
	
	private void switchDialogMode() {
		if(mStatusBtnItemListBtn == null || mMediaDetailInfo2 == null) {
			return;
		}
		mInManageMode = !mInManageMode;
		if(mInManageMode) {
			mBtnManage.setText(R.string.done);
			mTitleTop.setText(R.string.manage_offline_media);
			mBtnBack.setVisibility(View.INVISIBLE);
		} else {
			mBtnManage.setText(R.string.manage);
			mTitleTop.setText(R.string.select_offline_media);
			mBtnBack.setVisibility(View.VISIBLE);
		}
		
		if(mStyle == MediaConstantsDef.MEDIA_TYPE_TELEPLAY) {
			for(int i = 0; i < mStatusBtnItemListBtn.size(); i++) {
				StatusBtnItem statusBtnItem = mStatusBtnItemListBtn.get(i);
				if(statusBtnItem != null) {
					statusBtnItem.isInEditMode = mInManageMode;
					statusBtnItem.refreshUiStatus();
				}
			}
		} else if(mStyle == MediaConstantsDef.MEDIA_TYPE_VARIETY) {
			for(int i = 0; i < mStatusBtnItemListVariety.size(); i++) {
				StatusBtnItem statusBtnItem = mStatusBtnItemListVariety.get(i);
				if(statusBtnItem != null) {
					statusBtnItem.isInEditMode = mInManageMode;
					statusBtnItem.refreshUiStatus();
				}
			}
		}
		
		refreshEpisodeListView();
	}
	
	private void addToDownloadList(int ci, int preferenceSource) {
		mOfflineMediaManager.addOfflineMedia(mMediaDetailInfo2, ci, preferenceSource, mAllowAbleSources);
		AlertMessage.show(R.string.download_add_success);
		
		uploadOfflineStatistic(ci);
	}
	
	private void delFromDownloadList(int ci) {
		if(mMediaDetailInfo2.mediainfo != null) {
			mOfflineMediaManager.delOfflineMedia(mMediaDetailInfo2.mediainfo.mediaid, ci);
		}
	}
	
	private void pauseOfflineMedia(int mediaId, int episode) {
		mOfflineMediaManager.pauseOfflineMedia(mediaId, episode);
	}
	
	private void startOfflineMedia(int mediaId, int episode) {
		mOfflineMediaManager.startOfflineMedia(mediaId, episode);
	}
	
	private void buildOfflineMediaList(List<Object> list) {
		if(list == null || mMediaDetailInfo2 == null) {
			return;
		}
		for(int i = 0; i < list.size(); i++) {
			Object obj = list.get(i);
			if(obj instanceof OfflineMediaList) {
				OfflineMediaList offlineMediaList = (OfflineMediaList) obj;
				MediaDetailInfo mediaDetailInfo = mMediaDetailInfo2.mediainfo;
				if(mediaDetailInfo != null && offlineMediaList.getMediaId() == mediaDetailInfo.mediaid) {
					mOfflineMediaList = offlineMediaList;
					return;
				}
			}
		}
	}

	//UI callback
	private OnStatusBtnClickListener mOnStatusBtnClickListener = new OnStatusBtnClickListener() {

		@Override
		public void onStatusBtnClick(StatusBtn statusBtn) {
			doStatusBtnClick(statusBtn);
		}
	};
	
	private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if(view instanceof StatusBtnLong) {
				StatusBtnLong statusBtnLong = (StatusBtnLong) view;
				doItemClick(statusBtnLong);
			}
		}
	};
	
	private OnClickListener mOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			int id = v.getId();
			switch (id) {
			case R.id.download_select_back:
				DownloadSelectDialog.this.finish();
				break;
			case R.id.download_select_manage:
				switchDialogMode();
				break;

			default:
				break;
			}
		}
	};
	
	//data callback
	private OfflineMediaListener mOfflineMediaListener = new OfflineMediaListener() {
		
		@Override
		public void onOfflineMediaChanged() {
			List<Object> list = mOfflineMediaManager.getOfflineMediaList();
			buildOfflineMediaList(list);
			if(mMediaDetailInfo2 != null) {
				refreshEpisodeListView();
			}
		}
		
		@Override
		public void onOfflineMediaUpdate(OfflineMedia offlineMedia) {
			if(mMediaDetailInfo2 != null) {
				refreshEpisodeListView(true);
			}
		}
	};
	
	private MediaDetailInfoDoneListener mMediaDetailInfoListener = new MediaDetailInfoDoneListener() {
		
		@Override
		public void onMediaDetailInfoDone(MediaDetailInfo2 mediaDetailInfo,
				boolean isError) {
			mLoadingListView.setShowLoading(false);
			if(mediaDetailInfo != null) {
				mMediaDetailInfo2 = mediaDetailInfo;
				initData();
			} else {
				refreshEpisodeListView();
			}
		}
	};
	
	//statistic
	private void uploadOfflineStatistic(int ci) {
		if(mMediaDetailInfo2 == null || mMediaDetailInfo2.mediainfo == null) {
			return;
		}
		ComUserDataStatisticInfo statisticInfo = new ComUserDataStatisticInfo();
		statisticInfo.comUserDataType = ComUserDataTypeValueDef.COM_USER_DATA_TYPE_OFFLINE;
		statisticInfo.mediaId = mMediaDetailInfo2.mediainfo.mediaid;
		statisticInfo.ci = ci;
		statisticInfo.mediaSource = mPreferSource;
		statisticInfo.sourcePath = SourceTagValueDef.PAD_DOWNLOAD_SELECT_VALUE;
		DKApi.uploadComUserData(statisticInfo.formatToJson());
	}
}
