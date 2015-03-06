package com.miui.video;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout.LayoutParams;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.miui.video.adapter.OfflineSelectEpAdapter;
import com.miui.video.model.loader.DataLoader;
import com.miui.video.model.loader.DataLoader.LoadListener;
import com.miui.video.model.loader.DetailInfoLoader;
import com.miui.video.offline.OfflineMedia;
import com.miui.video.offline.OfflineMediaManager;
import com.miui.video.offline.OfflineMediaManager.OfflineMediasChangeListener;
import com.miui.video.type.MediaDetailInfo2;
import com.miui.video.type.MediaSetInfo;
import com.miui.video.util.AlertMessage;
import com.miui.video.util.DKLog;
import com.miui.video.util.Util;
import com.miui.video.widget.ButtonPair;
import com.miui.video.widget.ButtonPair.OnPairClickListener;
import com.miui.video.widget.LoadingGridView;
import com.miui.video.widget.RetryView;
import com.miui.video.widget.RetryView.OnRetryLoadListener;

public class OfflineSelectEpView extends PopupWindow {
	public static final String TAG = "OfflineSelectEpActivity";
	
	public static String KEY_MEDIA_DETAIL_2 = "detail";
	public static String KEY_MEDIA_ID = "id";
	public static String KEY_MEDIA_CI = "ci";

	private MediaDetailInfo2 mMediaDetailInfo2;
	private int mMediaID;
	private int mCi;
	
	private DetailInfoLoader mDetailInfoLoader;
	
	private OfflineMediaManager mOfflineMediaManager;
	
	private TextView mTextView;
	private LoadingGridView mLoadingGridView;
	private View mLoadingView;
	private GridView mGridView;
	private OfflineSelectEpAdapter mAdapter;
	private List<OfflineMedia> mMedias;
	private RetryView mRetryView;
	private ButtonPair mButtonPair;
	private RelativeLayout mRoot;
	private LinearLayout mContent;
	
	private boolean mNeedResizeView = true;
	
	private Intent mIntent;
	private Context mContext;
	
	@SuppressLint("InflateParams")
    public OfflineSelectEpView(Context context, Intent intent){
		super(LayoutInflater.from(context).inflate(R.layout.offline_select_ep, null), 
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		WindowManager.LayoutParams lp = ((Activity)context).getWindow().getAttributes();
		lp.alpha = 0.5f;
		((Activity)context).getWindow().setAttributes(lp);
		setAnimationStyle(R.style.vertical_popup_anim_style);
		mContext = context;
		mIntent = intent;
		init();
	}
	
	private void init() {
		initManager();
		initReceivedData();
		refreshUI();
		registerListeners();
	}
	
	public void initReceivedData() {
		if (mIntent != null) {
			if (mIntent.getSerializableExtra(KEY_MEDIA_DETAIL_2) instanceof MediaDetailInfo2) {
				mMediaDetailInfo2 = (MediaDetailInfo2) mIntent.getSerializableExtra(KEY_MEDIA_DETAIL_2);
			}else{
				mMediaID = mIntent.getIntExtra(KEY_MEDIA_ID, -1);
				initDataSupply();
				getMediaDetailData();
			}
			mCi = mIntent.getIntExtra(KEY_MEDIA_CI, 1);
		}
	}
	
	private void initDataSupply() {
	    mDetailInfoLoader = new DetailInfoLoader(mMediaID, "");
	    mDetailInfoLoader.addListener(mDetailListener);
	}
	
	private void registerListeners(){
	    mOfflineMediaManager.registerUnfinishedMediasChangeListener(mMediasChangeListener);
	    mOfflineMediaManager.registerFinishedMediasChangeListener(mMediasChangeListener);
	    setOnDismissListener(new OnDismissListener() {
	        @Override
	        public void onDismiss() {
	            WindowManager.LayoutParams lp = ((Activity)mContext).getWindow().getAttributes();
	            lp.alpha = 1f;
	            ((Activity)mContext).getWindow().setAttributes(lp);
	            mOfflineMediaManager.unregisterUnfinishedMediasChangeListener(mMediasChangeListener);
	            mOfflineMediaManager.unregisterFinishedMediasChangeListener(mMediasChangeListener);
	        }
	    });
	}
	
	private void getMediaDetailData() {
		if(mLoadingGridView != null){
			mLoadingGridView.setShowLoading(true);
		}
		if(mDetailInfoLoader != null){
		    mDetailInfoLoader.load();
		}
	}
	
	private LoadListener mDetailListener = new LoadListener() {
        @Override
        public void onLoadFinish(DataLoader loader) {
            mMediaDetailInfo2 = mDetailInfoLoader.getDetailInfo();
            refreshUI();
        }
        
        @Override
        public void onLoadFail(DataLoader loader) {
            refreshUI();
        }
    };
	
	private void initManager() {
		mOfflineMediaManager = DKApp.getSingleton(OfflineMediaManager.class);
	}
	
	private void refreshUI() {
		boolean showEpDetail = false;
		if(mMediaDetailInfo2 != null && mMediaDetailInfo2.mediainfo != null && 
		        mMediaDetailInfo2.mediainfo.category != null){
			showEpDetail = mMediaDetailInfo2.mediainfo.category.equals("综艺");
		}
		if(mLoadingGridView != null){
			mLoadingGridView.setShowLoading(false);
		}
		if(mRoot == null){
			mRoot = (RelativeLayout) getContentView().findViewById(R.id.offline_select_ep_root);
			mContent = (LinearLayout) View.inflate(mContext, R.layout.offline_select_ep_widget, null);
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
					mContext.getResources().getDimensionPixelOffset(R.dimen.size_988));
			lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			mContent.setLayoutParams(lp);
			mRoot.addView(mContent);
			mTextView = (TextView) mContent.findViewById(R.id.offline_select_ep_subtitle);
			refreshStorage();
			
			mLoadingGridView = (LoadingGridView) mContent.findViewById(R.id.offline_select_ep_grids);
			mGridView = mLoadingGridView.getGridView();
			int gapV = mContext.getResources().getDimensionPixelOffset(R.dimen.size_29);
			mGridView.setPadding(0, gapV, 0, gapV);
			mGridView.setClipToPadding(false);
			mGridView.setSelector(R.color.transparent);
			mGridView.setOverScrollMode(View.OVER_SCROLL_NEVER);
			mRetryView = new RetryView(mContext);
			mRetryView.setOnRetryLoadListener(new OnRetryLoadListener() {
				@Override
				public void OnRetryLoad(View vClicked) {
					getMediaDetailData();
				}
			});
			mRetryView.setTitle(R.string.reload_title_ep);
			mLoadingGridView.setEmptyView(mRetryView);
			mLoadingView = View.inflate(mContext, R.layout.load_view, null);
			mLoadingGridView.setLoadingView(mLoadingView);
			mButtonPair = (ButtonPair) mContent.findViewById(R.id.offline_select_ep_pair);
			mButtonPair.setOnPairClickListener(new OnPairClickListener() {
				@Override
				public void onRightClick() {
					// TODO: notification
				    List<OfflineMedia> medias = mAdapter.getSelectedItems();
				    if(medias != null && medias.size() > 0){
				        for (OfflineMedia media : medias) {
				            mOfflineMediaManager.addMedia(media);
				        }
				        dismiss();
				        AlertMessage.show(R.string.download_add_success);
				    }else{
				        AlertMessage.show(R.string.offline_no_selected_item_hint);
				    }
				}
				@Override
				public void onLeftClick() {
					dismiss();
				}
			});
		}
		if(mMediaDetailInfo2 == null || mMediaDetailInfo2.mediainfo == null || mMediaDetailInfo2.mediaciinfo == null){
			mButtonPair.setRightButtonEnable(false);
			mButtonPair.setRightText(mContext.getResources().getString(R.string.offline));
		}else{
			if(showEpDetail){
				if(mNeedResizeView){
					mNeedResizeView = false;
					mRoot.removeAllViews();
					RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
							mContext.getResources().getDimensionPixelOffset(R.dimen.size_1306));
					lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
					mContent.setLayoutParams(lp);
					mRoot.addView(mContent);
				}
				int itemPadding = mContext.getResources().getDimensionPixelOffset(R.dimen.size_40);
				mGridView.setVerticalSpacing(itemPadding);
				mGridView.setNumColumns(1);
			}else{
				int itemPadding = mContext.getResources().getDimensionPixelOffset(R.dimen.size_30);
				mGridView.setVerticalSpacing(itemPadding);
				mGridView.setNumColumns(4);
			}
			mAdapter = new OfflineSelectEpAdapter(mContext);
			mMedias = OfflineMedia.from(mMediaDetailInfo2,  getDownloadSource());
			refreshOfflineStatus(mOfflineMediaManager.getOfflineMediaList());
			mAdapter.setGroup(mMedias);
			if(showEpDetail){
				ArrayList<String> details = new ArrayList<String>();
				for(MediaSetInfo video : mMediaDetailInfo2.mediaciinfo.videos){
					details.add(video.date + " " + video.videoname);
				}
				mAdapter.showEpDetail(details);
			}
			mAdapter.setCurrentEpisode(mCi);
			mGridView.setAdapter(mAdapter);
			mButtonPair.setRightButtonEnable(false);
			setRightButtonPair();
			mGridView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					DKLog.e(TAG, "clicked position: " + position);
					mAdapter.toggleSelectItem(position);
					refreshSelectedCount();
				}
			});
		}
	}
	
	private int getDownloadSource(){
	    if(mMediaDetailInfo2 != null && mMediaDetailInfo2.mediainfo != null &&
	            mMediaDetailInfo2.mediainfo.media_available_download_source != null && 
	            mMediaDetailInfo2.mediainfo.media_available_download_source.length > 0){
	        return mMediaDetailInfo2.mediainfo.media_available_download_source[0];
	    }
	    return -1;
	}
	
	private void refreshSelectedCount() {
		setRightButtonPair();
	}
	
	private void setRightButtonPair(){
		int mediaCount = mAdapter.getSelectedCount();
		if(mediaCount > 0){
			mButtonPair.setRightText(mContext.getResources().getString(
					R.string.offline_with, mAdapter.getSelectedCount()));
	         mButtonPair.setRightButtonEnable(true);
		}else{
			mButtonPair.setRightText(R.string.offline);
			mButtonPair.setRightButtonEnable(false);
		}
	}
	
	private void refreshStorage() {
		StringBuilder sb = new StringBuilder();
		sb.append(mContext.getResources().getString(R.string.storage_remain)).
			append(Util.convertToFormateSize(Util.getSDAvailaleSize()));
		sb.append(" / ");
		sb.append(mContext.getResources().getString(R.string.storage_total)).
			append(Util.convertToFormateSize(Util.getSDAllSize()));
		mTextView.setText(sb.toString());
	}
	
	private void refreshOfflineStatus(List<OfflineMedia> medias){
        if(mMedias == null){
            return;
        }
        for (int i = 0; i < mMedias.size(); i++) {
            final OfflineMedia item = mMedias.get(i);
            for (OfflineMedia media : medias) {
                if (item != null && media != null && item.mediaId == media.mediaId && item.episode == media.episode) {
                    mMedias.set(i, media);
                }
            }
        }
        mAdapter.setGroup(mMedias);
	}
	
	private OfflineMediasChangeListener mMediasChangeListener = new OfflineMediasChangeListener() {
		@Override
		public void onOfflineMediasChange(List<OfflineMedia> medias) {
		    refreshOfflineStatus(medias);
		}
	};
}
