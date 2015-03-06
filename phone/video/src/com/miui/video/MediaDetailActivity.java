package com.miui.video;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.miui.video.api.DKApi;
import com.miui.video.base.BaseActivity;
import com.miui.video.controller.PlaySession;
import com.miui.video.datasupply.MediaRecommendSupply;
import com.miui.video.datasupply.MediaRecommendSupply.MediaRecommendListener;
import com.miui.video.datasupply.MediaUrlInfoListSupply;
import com.miui.video.datasupply.MediaUrlInfoListSupply.MediaUrlInfoListListener;
import com.miui.video.db.MediaInfoForPlayerUtil;
import com.miui.video.local.FavoriteManager;
import com.miui.video.local.OnlinePlayHistory;
import com.miui.video.local.PlayHistory;
import com.miui.video.local.PlayHistoryManager;
import com.miui.video.local.PlayHistoryManager.OnHistoryChangedListener;
import com.miui.video.model.AppSettings;
import com.miui.video.model.ImageManager;
import com.miui.video.model.loader.DataLoader;
import com.miui.video.model.loader.DataLoader.LoadListener;
import com.miui.video.model.loader.DetailInfoLoader;
import com.miui.video.offline.OfflineMedia;
import com.miui.video.offline.OfflineMediaManager;
import com.miui.video.popup.SelectSourcePopup;
import com.miui.video.popup.SelectSourcePopup.OnSourceSelectListener;
import com.miui.video.statistic.*;
import com.miui.video.type.*;
import com.miui.video.util.AlertMessage;
import com.miui.video.util.DKLog;
import com.miui.video.widget.RetryView.OnRetryLoadListener;
import com.miui.video.widget.detail.DetailView;
import com.miui.videoplayer.common.AndroidUtils;
import com.miui.videoplayer.download.SourceManager;
import com.xiaomi.mipush.sdk.MiPushClient;
import miui.app.AlertDialog;

import java.util.ArrayList;
import java.util.List;

/**
 *@author tangfuling
 *
 */

public class MediaDetailActivity extends BaseActivity {

    public static final String TAG = "MediaDetailActivity";

    public static String KEY_MEDIA_ID = "mediaId";
    public static String KEY_MEDIA_INFO = "mediaInfo";
    public static String KEY_IS_BANNER = "isBanner";
    public static String KEY_SOURCE_PATH = "enterPathInfo";

    //UI
    private DetailView mDetailView;
    private TextView mFavoriteBtn;
    private TextView mPlayBtn;
    private TextView mDownloadBtn;
    private View mBottomBar;

    private View mTitleView;
    private TextView mTitleName;
    private ImageView mSourceSelectLogo;

    //manager
    private FavoriteManager mFavoriteManager;
    private OfflineMediaManager mOfflineMediaManager;
    private PlayHistoryManager mPlayHistoryManager;

    //received data
    private int mMediaId;
    private MediaInfo mMediaInfo;
    private String mSourcePath;
    //	private boolean mIsBanner = false;

    //data from net
    private MediaDetailInfo2 mMediaDetailInfo2;
    private BaseMediaInfo[] mRecommendations;
    private int[] mAllowAbleSources;

    //data from local
    private PlayHistory mPlayHistory;

    //data supply
    private DetailInfoLoader mDetailLoader;
    private MediaRecommendSupply mMediaRecommendSupply;
    private MediaUrlInfoListSupply mMediaUrlInfoListSupply;
    OfflineSelectEpView mOfflineSelectView;
    private boolean mCurEpisodeLoading = false;
    private int preferenceSource = -1;
    private ArrayList<Integer> mSourceList; 
    private SelectSourcePopup mSelectSourcePopup;
    private RelativeLayout mRoot;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.media_detail);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCurEpisodeLoading = false;
        mPlayHistoryManager.loadPlayHistory();
        refreshBtnStatus();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mPlayHistoryManager != null) {
            mPlayHistoryManager.removeListener(mOnHistoryChangeListener);
        }
        
        if(mMediaUrlInfoListSupply != null){
        	mMediaUrlInfoListSupply.removeListener(mMediaUrlInfoListListener);
        }
    }

    //init
    private void init() {
        initManager();
        initReceivedData();
        initUI();
        initData();
    }

    private void initManager() {
        mFavoriteManager = DKApp.getSingleton(FavoriteManager.class);
        mOfflineMediaManager = DKApp.getSingleton(OfflineMediaManager.class);
        mPlayHistoryManager = DKApp.getSingleton(PlayHistoryManager.class);
        mPlayHistoryManager.addListener(mOnHistoryChangeListener);
    }

    private void initReceivedData() {
        Intent intent = getIntent();
        Uri uri = intent.getData();
        if(uri != null){
            String strMediaId = uri.getQueryParameter("mediaId");
            try {
                mMediaId = Integer.parseInt(strMediaId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            mMediaId = intent.getIntExtra(KEY_MEDIA_ID, -1);
            mSourcePath = intent.getStringExtra(KEY_SOURCE_PATH);
            Object mediaInfo = intent.getSerializableExtra(KEY_MEDIA_INFO);
            if(mediaInfo instanceof MediaInfo) {
                mMediaInfo = (MediaInfo) mediaInfo;
                mMediaId = mMediaInfo.mediaid;
            }
        }
    }

    private void initUI() {
        initView();
        initTitleTop();
    }

    private void initView() {
    	mRoot = (RelativeLayout) findViewById(R.id.root);
        mDetailView = (DetailView) findViewById(R.id.detail_view);
        mDetailView.setOnRetryLoadListener(mOnRetryLoadListener);
        mFavoriteBtn = (TextView) findViewById(R.id.detail_favorite);
        mFavoriteBtn.setOnClickListener(mOnClickListener);
        mPlayBtn = (TextView) findViewById(R.id.detail_play);
        mPlayBtn.setOnClickListener(mOnClickListener);
        mDownloadBtn = (TextView) findViewById(R.id.detail_download);
        mDownloadBtn.setOnClickListener(mOnClickListener);
        mBottomBar = findViewById(R.id.bottom_bar);
        mBottomBar.setVisibility(View.GONE);
    }

    private void initTitleTop() {
        mTitleView = findViewById(R.id.title_top);
        mTitleName = (TextView) mTitleView.findViewById(R.id.title_top_name);
        mTitleName.setOnClickListener(mOnClickListener);        
        mSourceSelectLogo = (ImageView)findViewById(R.id.selectsource);
        mSourceSelectLogo.setOnClickListener(mOnClickListener);    
        if(mMediaInfo != null) {
            mTitleName.setText(mMediaInfo.medianame);
        }
    }

    private void initData() {
        initDataSupply();
        getMediaDetailData();
        getRecommendData();
    }

    private void initDataSupply() {
        if(mMediaRecommendSupply == null) {
            mMediaRecommendSupply = new MediaRecommendSupply();
            mMediaRecommendSupply.addListener(mMediaRecommendListener);
        }
        if(mMediaUrlInfoListSupply == null){
    		mMediaUrlInfoListSupply = new MediaUrlInfoListSupply();
    		mMediaUrlInfoListSupply.addListener(mMediaUrlInfoListListener);
        }
        mDetailLoader = new DetailInfoLoader(mMediaId, prepareMediaDetailStatistic());
        mDetailLoader.addListener(mDetailInfoListener);
    }

    //get data
    private void getMediaDetailData() {
        mDetailView.showLoadingView();
        mDetailLoader.load();
    }

    private void getRecommendData() {
        if(mRecommendations == null) {
            mMediaRecommendSupply.getMediaRecommend(mMediaId, 6);
        }
    }

    private void getMediaUrlInfoList(int mediaId, int ci){
		mMediaUrlInfoListSupply.getMediaUrlInfoList(mediaId, ci, -1);
    }
    
    //packaged method
    private void playCurCi() {
        mCurEpisodeLoading = true;
        mDetailView.playCurCi();
        refreshBtnStatus();
    }

    private void refreshBtnStatus() {
        refreshBtnPlayStatus();
        refreshBtnFavoriteStatus();
        refreshBtnDownloadStatus();
    }

    private void refreshBtnPlayStatus() {
        if(mCurEpisodeLoading) {
            String str = getResources().getString(R.string.connecting);
            mPlayBtn.setText(str);
        } else {
            String str = getResources().getString(R.string.play);
            mPlayBtn.setText(str);
        }
    }

    private void refreshBtnDownloadStatus() {
        if (mAllowAbleSources == null || mAllowAbleSources.length == 0) {
            mDownloadBtn.setEnabled(false);
        } else {
            mDownloadBtn.setEnabled(true);
        }
    }

    private void refreshBtnFavoriteStatus() {
        if(mFavoriteManager.isFavorite(mMediaInfo)) {
            refreshBtnFavoriteStatus(true);
        } else {
            refreshBtnFavoriteStatus(false);
        }
    }

    private void refreshBtnFavoriteStatus(boolean isFavorite) {
        mFavoriteBtn.setSelected(isFavorite);
    }

    private void handlerFavoriteClick() {
        if(mFavoriteManager.isFavorite(mMediaInfo)) {
            mFavoriteManager.delFavorite(mMediaInfo);
            refreshBtnFavoriteStatus(false);
            removeFromMiPush();
            AlertMessage.show(R.string.cancel_favorite_success);
        } else {
            mFavoriteManager.addFavorite(mMediaInfo);
            refreshBtnFavoriteStatus(true);
            addToMiPush();
            AlertMessage.show(R.string.add_favorite_success);
        }
    }

    private void addToMiPush() {
        if(mMediaInfo == null || mMediaInfo.isFinished()) {
            return;
        }
        MiPushClient.subscribe(this, String.valueOf(mMediaInfo.mediaid), null);
    }

    private void removeFromMiPush() {
        if(mMediaInfo == null) {
            return;
        }
        MiPushClient.unsubscribe(this, String.valueOf(mMediaInfo.mediaid), null);
    }

    private void downloadMedia(int ci, int preferenceSource) {
        if(AndroidUtils.isNetworkConncected(this) && !AndroidUtils.isFreeNetworkConnected(this)
                && DKApp.getSingleton(AppSettings.class).isOpenCellularOfflineHint(this)){
            showUseDataStreamDialog(ci);
        }else{
            startDownload(ci);
        }
        uploadOfflineStatistic();
    }

    private void startDownload(int ci) {
        if (mMediaDetailInfo2 == null || mMediaDetailInfo2.mediainfo == null) {
            return;
        }
        if (mAllowAbleSources == null || mAllowAbleSources.length == 0) {
            AlertMessage.show(R.string.offline_no_source);
            return;
        } 
        if (mMediaDetailInfo2.mediainfo.isMultiSetType()) {
            // TODO: use activity to show.
            Intent intent = new Intent(this, OfflineSelectEpView.class);
            intent.putExtra(OfflineSelectEpView.KEY_MEDIA_DETAIL_2, mMediaDetailInfo2);
            if (mPlayHistory instanceof OnlinePlayHistory) {
                intent.putExtra(OfflineSelectEpView.KEY_MEDIA_CI, ((OnlinePlayHistory) mPlayHistory).mediaCi);
            } else {
                intent.putExtra(OfflineSelectEpView.KEY_MEDIA_CI, 1);
            }
            mOfflineSelectView = new OfflineSelectEpView(this, intent);
            mOfflineSelectView.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);
        } else {
            OfflineMedia media = OfflineMedia.from(mMediaDetailInfo2, ci, mAllowAbleSources[0], null);
            if (mOfflineMediaManager.isMediaLoading(media)) {
                AlertMessage.show(R.string.download_is_in_tasklist);
            } else {
                mOfflineMediaManager.addMedia(media);
                AlertMessage.show(R.string.download_add_success);
            }
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event){
        if(event.getAction() == KeyEvent.ACTION_DOWN && 
                event.getKeyCode() == KeyEvent.KEYCODE_BACK){
            if(mOfflineSelectView != null && mOfflineSelectView.isShowing()){
                mOfflineSelectView.dismiss();
                return true;
            }else if(mSelectSourcePopup != null && mSelectSourcePopup.isShowing()){
    			mSelectSourcePopup.dismiss();
    			return true;
    		}
        }
        return super.dispatchKeyEvent(event);
    }
    

    @Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
    	if(mSelectSourcePopup != null && mSelectSourcePopup.isShowing()){
    		mSelectSourcePopup.triggerDismissImmediately();
    	}
		return super.dispatchTouchEvent(ev);
	}

	//alert dialog
    private void showUseDataStreamDialog(final int ci) {
        View contentView = View.inflate(MediaDetailActivity.this, R.layout.download_datastream_hint_view, null);
        String negativeStr = getResources().getString(R.string.datastream_alert_negative_button);
        String positiveStr = getResources().getString(R.string.datastream_alert_positive_button);
        AlertDialog dialog = new AlertDialog.Builder(this, miui.R.style.Theme_Light_Dialog_Alert).create();
        dialog.setTitle(R.string.datastream_alert_title);
        dialog.setView(contentView);
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, negativeStr, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                startSystemSettingActivity();
            }
        } );

        dialog.setButton(AlertDialog.BUTTON_POSITIVE, positiveStr, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                startDownload(ci);
            }
        });
        dialog.setCancelable(false);

        try {
            dialog.show();
        } catch (Exception e) {
            DKLog.e(TAG, e.getLocalizedMessage());
        }
    }

    private void startSystemSettingActivity() {
        Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
        startActivity(intent);	
    }
    
    public void showSelectSourceDialog(){
        if(mSourceList == null || mSourceList.size() == 0){
            return;
        }
    	if(mSelectSourcePopup == null){
    		mSelectSourcePopup = new SelectSourcePopup(this, mSourceList);
    		DKLog.d(TAG, "preferenceSource:" + preferenceSource);
    		mSelectSourcePopup.setCurrentSource(preferenceSource);
    		mSelectSourcePopup.setOnSourceSelectListener(new OnSourceSelectListener() {
				@Override
				public void onSourceSelect(int position, int source) {
					preferenceSource = source;
					refreshSelectedSource(preferenceSource);
					mSelectSourcePopup.dismiss();
				}
			});
    	}
    	mSelectSourcePopup.show(mRoot, mTitleView);
    	mTitleView.bringToFront();
    }
    
    //UI callback
    private OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
        	DKLog.d(TAG, "onClick ........");
            if(v == mPlayBtn) {
                OfflineMedia media =  mOfflineMediaManager.getOfflineMedia(mMediaId, mDetailView.getCurCi());
                if (media != null && media.isFinished()) {
                    new PlaySession(MediaDetailActivity.this).startPlayerOffline(media);
                }else{
                    playCurCi();
                }
            } else if(v == mFavoriteBtn) {
                handlerFavoriteClick();
            } else if(v == mDownloadBtn) {
                downloadMedia(mDetailView.getCurCi(), -1);
            } else if(v == mTitleName) {
            	DKLog.d(TAG, "mTitleName ........");
                MediaDetailActivity.this.finish();
            } else if(v == mSourceSelectLogo){
            	DKLog.d(TAG, "mSourceSelectLogo ........");
            	showSelectSourceDialog();
            }
        }
    };

    private OnRetryLoadListener mOnRetryLoadListener = new OnRetryLoadListener() {

        @Override
        public void OnRetryLoad(View vClicked) {
            getMediaDetailData();
        }
    };

//    //Data callback
//    private MediaDetailInfoDoneListener mMediaDetailInfoDoneListener = new MediaDetailInfoDoneListener() {
//
//        @Override
//        public void onMediaDetailInfoDone(MediaDetailInfo2 mediaDetailInfo,
//                boolean isError) {
//            MediaInfoForPlayerUtil.getInstance().set(mMediaDetailInfo2);
//        }
//    };
    
    private LoadListener mDetailInfoListener = new LoadListener() {
        @Override
        public void onLoadFinish(DataLoader loader) {
            mMediaDetailInfo2 = mDetailLoader.getDetailInfo();
            if(mMediaDetailInfo2 != null && mMediaDetailInfo2.mediainfo != null) {
                mBottomBar.setVisibility(View.VISIBLE);
                mMediaInfo = mMediaDetailInfo2.mediainfo;
                mMediaId = mMediaInfo.mediaid;
                mAllowAbleSources = mMediaDetailInfo2.mediainfo.media_available_download_source;
                mDetailView.showContentView();
                mDetailView.setData(mMediaDetailInfo2);
                mTitleName.setText(mMediaInfo.medianame);
                MediaInfoForPlayerUtil.getInstance().set(mMediaDetailInfo2);
                refreshBtnStatus();
                if(mDetailView != null && mDetailView.getCurCi() > 0){
                    getMediaUrlInfoList(mMediaId, mDetailView.getCurCi());
                }else{
                    getMediaUrlInfoList(mMediaId, 1);
                }
            }else{
                mDetailView.showRetryView();
            }
        }
        
        @Override
        public void onLoadFail(DataLoader loader) {
            mDetailView.showRetryView();
        }
    };

    private OnHistoryChangedListener mOnHistoryChangeListener = new OnHistoryChangedListener() {

        @Override
        public void onHistoryChanged(List<PlayHistory> historyList) {
            if(mMediaInfo != null) {
                PlayHistory playHistory = mPlayHistoryManager.getPlayHistoryById(mMediaInfo.mediaid);
                mPlayHistory = playHistory;
                mDetailView.setPlayHistory(mPlayHistory);
            }
        }
    };

    private MediaRecommendListener mMediaRecommendListener = new MediaRecommendListener() {

        @Override
        public void onMediaRecommendDone(BaseMediaInfo[] recommendations, boolean isError) {
            mRecommendations = recommendations;
            mDetailView.setRecommend(mRecommendations);
        }
    };

    private MediaUrlInfoListListener mMediaUrlInfoListListener = new MediaUrlInfoListListener(){

		@Override
		public void onMediaUrlInfoListDone(MediaUrlInfoList mediaUrlInfoList,
				boolean isError) {
			refreshSelectedSource(mediaUrlInfoList);
		}
    };
    
	private void refreshSelectedSource(int preferenceSource) {
		this.preferenceSource = preferenceSource;
		mSourceSelectLogo.setImageBitmap(null);
		SourceInfo sourceInfo = DKApp.getSingleton(SourceManager.class).getSourceInfo(preferenceSource);
		if(sourceInfo != null){
			ImageUrlInfo urlInfo = new ImageUrlInfo(sourceInfo.posterurl, sourceInfo.md5, null);
			ImageManager.getInstance().fetchImage(ImageManager.createTask(urlInfo, null), mSourceSelectLogo);
		}
		mDetailView.setPreferenceSource(preferenceSource);
	}

	private void refreshSelectedSource(MediaUrlInfoList mediaUrlInfoList) {
		mSourceList = mMediaUrlInfoListSupply.getSourceList(mediaUrlInfoList);
		if(mSourceList != null && mSourceList.size() > 0) {
			this.preferenceSource = mSourceList.get(0);
			refreshSelectedSource(preferenceSource);
		}
	}
    
    //	//auth callback
    //	private AuthenticateAccountManager mAuthenticateAccountManager = new AuthenticateAccountManager(this) {
    //		@Override
    //		protected void onAuthSuccess() {
    //		}
    //
    //		@Override
    //		protected void onAuthFailed(String failedReason) {
    //			AlertMessage.show(failedReason);
    //		}
    //
    //		@Override
    //		protected void onAuthNoAccount() {
    //		}
    //		
    //	};

    //statistic
    private void uploadOfflineStatistic() {
        if(mMediaInfo == null) {
            return;
        }
        ComUserDataStatisticInfo statisticInfo = new ComUserDataStatisticInfo();
        statisticInfo.comUserDataType = ComUserDataTypeValueDef.COM_USER_DATA_TYPE_OFFLINE;
        statisticInfo.mediaId = mMediaInfo.mediaid;
        statisticInfo.ci = mDetailView.getCurCi();
        statisticInfo.sourcePath = SourceTagValueDef.PHONE_V6_DETAIL_VALUE;
        DKApi.uploadComUserData(statisticInfo.formatToJson());
    }

    private String prepareMediaDetailStatistic() {
        OpenMediaStatisticInfo  openMediaStatisticInfo = new OpenMediaStatisticInfo();
        if(mMediaInfo != null) {
            openMediaStatisticInfo.mediaId = mMediaInfo.mediaid;
        }
        openMediaStatisticInfo.sourcePath = mSourcePath;
        openMediaStatisticInfo.mediaSetType = MediaSetTypeDef.getMediaSetType(this, mMediaInfo);
        return openMediaStatisticInfo.formatToJson();
    }
}
