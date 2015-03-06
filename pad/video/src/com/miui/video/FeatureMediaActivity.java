package com.miui.video;

import java.util.ArrayList;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.FrameLayout.LayoutParams;

import com.miui.video.adapter.MediaViewListAdapter;
import com.miui.video.base.BaseFragmentActivity;
import com.miui.video.datasupply.FeatureMediaSupply;
import com.miui.video.datasupply.FeatureMediaSupply.FeatureMediaListener;
import com.miui.video.dialog.MediaDetailDialogFragment;
import com.miui.video.statistic.GetSubjectMediaListStatisticInfo;
import com.miui.video.statistic.SourceTagValueDef;
import com.miui.video.type.MediaInfo;
import com.miui.video.type.SpecialSubject;
import com.miui.video.widget.ListViewEx;
import com.miui.video.widget.LoadingListView;
import com.miui.video.widget.RetryView;
import com.miui.video.widget.RetryView.OnRetryLoadListener;
import com.miui.video.widget.bg.OnlineBg;
import com.miui.video.widget.media.MediaView;
import com.miui.video.widget.media.MediaView.OnMediaClickListener;

/**
 *@author tangfuling
 *
 */

public class FeatureMediaActivity extends BaseFragmentActivity {
	
	//UI
	private LoadingListView mFeatureMediaLoadingListView;
	private ListViewEx mFeatureMediaListView;
	private View mFeatureMediaLoadingView;
	private View mFeatureMediaEmptyView;
	private RetryView mFeatureMediaRetryView;
	private MediaViewListAdapter mMediaViewListAdapter;
	
	private TextView mTitleTopName;
	private TextView mTitleTopStatus;
	private View mTitleTop;
	
	private TextView mFeatureMediaDesc;
	
	//data supply
	private FeatureMediaSupply mFeatureMediaSupply;
	
	//received data
	private SpecialSubject mSpecialSubject;
	private String mSourcePath;
	
	//data from network
	private ArrayList<Object> mFeatureMedias = new ArrayList<Object>();
	
	public static String KEY_FEATURE = "subject";
	public static String KEY_SOURCE_PATH = "enterPath";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feature_media);
		init();
	}
	
	//init
	private void init() {
		initReceivedData();
		
		initUI();
		initData();
	}
	
	private void initReceivedData() {
		Intent intent = getIntent();
		mSourcePath = intent.getStringExtra(KEY_SOURCE_PATH);
		Object obj = intent.getSerializableExtra(KEY_FEATURE);
		if(obj instanceof SpecialSubject) {
			mSpecialSubject = (SpecialSubject) obj;
		}
	}
	
	private void initUI() {
		initDecorView();
		initFeatureMediaListView();
		initTitleTop();
		refreshFeatureMediaListView(false);
		refreshTitleTop();
	}
	
	private void initDecorView() {
		ViewGroup decorView = (ViewGroup) getWindow().getDecorView();
		OnlineBg onlineBg = new OnlineBg(this);
		LayoutParams onlineBgParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		decorView.addView(onlineBg, 0, onlineBgParams);
	}
	
	private void initFeatureMediaListView() {
		mFeatureMediaLoadingListView = (LoadingListView) findViewById(R.id.feature_media_list);
		mFeatureMediaListView = mFeatureMediaLoadingListView.getListView();
		mFeatureMediaListView.setVerticalFadingEdgeEnabled(true);
		mFeatureMediaListView.setFadingEdgeLength(getResources().getDimensionPixelSize(R.dimen.video_fade_edge_length));
		
		View headView = new View(this);
		int height = (int) getResources().getDimension(R.dimen.video_common_list_top_padding);
		AbsListView.LayoutParams headViewParams = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, height);
		headView.setLayoutParams(headViewParams);
		mFeatureMediaListView.addHeaderView(headView);
		
		mMediaViewListAdapter = new MediaViewListAdapter(this);
		mMediaViewListAdapter.setOnMediaClickListener(mOnMediaClickListener);
		mFeatureMediaListView.setAdapter(mMediaViewListAdapter);
		
		mFeatureMediaLoadingView = View.inflate(this, R.layout.load_view, null);
		mFeatureMediaLoadingListView.setLoadingView(mFeatureMediaLoadingView);
		
		mFeatureMediaEmptyView = View.inflate(this, R.layout.empty_view_media, null);
		TextView emptyHint = (TextView) mFeatureMediaEmptyView.findViewById(R.id.empty_hint);
		emptyHint.setText(getResources().getString(R.string.feature_media_empty_hint));
		ImageView emptyIcon = (ImageView) mFeatureMediaEmptyView.findViewById(R.id.empty_icon);
		emptyIcon.setBackgroundResource(R.drawable.empty_icon_media);
		
		mFeatureMediaRetryView = new RetryView(this);
		mFeatureMediaRetryView.setOnRetryLoadListener(new OnRetryLoadListener() {
			@Override
			public void OnRetryLoad(View vClicked) {
				getFeatureMedias();
			}
		});
	}
	
	private void initTitleTop() {
		mFeatureMediaDesc = (TextView) findViewById(R.id.feature_media_desc);
		mTitleTopName = (TextView) findViewById(R.id.title_top_name);
		mTitleTopStatus = (TextView) findViewById(R.id.title_top_status);
		mTitleTop = findViewById(R.id.title_top);
		mTitleTop.setOnClickListener(mOnClickListener);
		
		if(mSpecialSubject != null) {
			mFeatureMediaDesc.setText(mSpecialSubject.desc);
			mTitleTopName.setText(mSpecialSubject.name);
		}
	}
	
	private void initData() {
		initDataSupply();
		getFeatureMedias();
	}
	
	private void initDataSupply() {
		mFeatureMediaSupply = new FeatureMediaSupply();
		mFeatureMediaSupply.addListener(mFeatureMediaListener);
	}
	
	//get data
	private void getFeatureMedias() {
		if(mSpecialSubject != null) {
			mFeatureMediaLoadingListView.setShowLoading(true);
			mFeatureMediaSupply.getFeatureList(mSpecialSubject.id, prepareFeatureMediaStatisticInfo());
		}
	}
	
	//packaged method
	private void refreshFeatureMediaListView(boolean isError) {
		mMediaViewListAdapter.setGroup(mFeatureMedias);
		
		if(mFeatureMedias.size() > 0) {
			return;
		}
		if(isError) {
			mFeatureMediaLoadingListView.setEmptyView(mFeatureMediaRetryView);
		} else {
			mFeatureMediaLoadingListView.setEmptyView(mFeatureMediaEmptyView);
		}
	}
	
	private void refreshTitleTop() {
		String str = getString(R.string.count_ge_feature_media);
		str = String.format(str, mFeatureMedias.size());
		mTitleTopStatus.setText(str);
	}
	
	//UI callback
	private OnClickListener mOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			int id = v.getId();
			if(id == mTitleTop.getId()) {
				FeatureMediaActivity.this.finish();
			}
		}
	};
	
	private OnMediaClickListener mOnMediaClickListener = new OnMediaClickListener() {
		
		@Override
		public void onMediaClick(MediaView mediaView, Object media) {
			if(media instanceof MediaInfo) {
				Intent intent = new Intent();
				intent.setClass(FeatureMediaActivity.this, MediaDetailDialogFragment.class);
				intent.putExtra(MediaDetailDialogFragment.KEY_MEDIA_INFO, (MediaInfo)media);
				intent.putExtra(MediaDetailDialogFragment.KEY_IS_BANNER, mediaView.isBanner());
				intent.putExtra(MediaDetailDialogFragment.KEY_SOURCE_PATH, SourceTagValueDef.PAD_FEATURE_MEDIA_VALUE);
				FeatureMediaActivity.this.startActivity(intent);
			} 
		}
	};
	
	//data callback
	private FeatureMediaListener mFeatureMediaListener = new FeatureMediaListener() {
		
		@Override
		public void onFeatureMediaDone(ArrayList<Object> featureMedias,
				boolean isError) {
			mFeatureMediaLoadingListView.setShowLoading(false);
			mFeatureMedias.clear();
			if(featureMedias != null) {
				mFeatureMedias.addAll(featureMedias);
			}
			refreshFeatureMediaListView(isError);
			refreshTitleTop();
		}
	};
	
	//statistic
	private String prepareFeatureMediaStatisticInfo() {
		GetSubjectMediaListStatisticInfo  statisticInfo = new GetSubjectMediaListStatisticInfo();
		statisticInfo.sourcePath = mSourcePath;
		statisticInfo.specialListId = getSpecialListId();
		return statisticInfo.formatToJson();
	}
	
	private String getSpecialListId() {
		if(mSpecialSubject != null) {
			StringBuilder specialId = new StringBuilder();
			specialId.append(mSpecialSubject.name);
			specialId.append("(");
			specialId.append(mSpecialSubject.id);
			specialId.append(")");
			return specialId.toString();
		}
		return "";
	}
}
