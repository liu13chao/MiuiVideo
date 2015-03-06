package com.miui.video;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.miui.video.adapter.MediaViewListVAdapter;
import com.miui.video.base.BaseFragmentActivity;
import com.miui.video.datasupply.FeatureMediaSupply;
import com.miui.video.datasupply.FeatureMediaSupply.FeatureMediaListener;
import com.miui.video.statistic.GetSubjectMediaListStatisticInfo;
import com.miui.video.statistic.SourceTagValueDef;
import com.miui.video.type.MediaInfo;
import com.miui.video.type.SpecialSubject;
import com.miui.video.widget.ListViewEx;
import com.miui.video.widget.LoadingListView;
import com.miui.video.widget.RetryView;
import com.miui.video.widget.RetryView.OnRetryLoadListener;

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
	private MediaViewListVAdapter mMediaViewListVAdapter;
	
	private TextView mTitleTopName;
	private View mTitleTop;
	
	private View mHeadView;
	private TextView mHeadViewDesc;
	
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
		setContentView(R.layout.com_media_list);
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
		initFeatureMediaListView();
		initTitleTop();
		refreshHeadView();
		refreshFeatureMediaListView(false);
	}
	
	private void initFeatureMediaListView() {
		mFeatureMediaLoadingListView = (LoadingListView) findViewById(R.id.com_media_list);
		mFeatureMediaListView = mFeatureMediaLoadingListView.getListView();
        int paddingLeft = getResources().getDimensionPixelSize(R.dimen.page_margin);
        int paddingTop = getResources().getDimensionPixelSize(R.dimen.page_margin_top);
        mFeatureMediaListView.setPadding(paddingLeft, paddingTop,  paddingLeft, paddingTop);
        mFeatureMediaListView.setVerticalScrollBarEnabled(false);
        mFeatureMediaListView.setSelector(R.drawable.transparent);
        mFeatureMediaListView.setClipToPadding(false);
        
		mHeadView = View.inflate(this, R.layout.feature_media_header, null);
		mHeadViewDesc = (TextView) mHeadView.findViewById(R.id.feature_media_desc);
		mFeatureMediaListView.addHeaderView(mHeadView);
		
		mMediaViewListVAdapter = new MediaViewListVAdapter(this);
		mFeatureMediaListView.setAdapter(mMediaViewListVAdapter);
		mFeatureMediaListView.setOnItemClickListener(mOnItemClickListener);
		
		mFeatureMediaLoadingView = View.inflate(this, R.layout.load_view, null);
		mFeatureMediaLoadingListView.setLoadingView(mFeatureMediaLoadingView);
		
		mFeatureMediaEmptyView = View.inflate(this, R.layout.empty_view_media, null);
		TextView emptyTitle = (TextView) mFeatureMediaEmptyView.findViewById(R.id.empty_title);
		emptyTitle.setText(getResources().getString(R.string.error_empty_title));
		ImageView emptyIcon = (ImageView) mFeatureMediaEmptyView.findViewById(R.id.empty_icon);
		emptyIcon.setBackgroundResource(R.drawable.empty_icon_error);
		
		mFeatureMediaRetryView = new RetryView(this);
		mFeatureMediaRetryView.setOnRetryLoadListener(new OnRetryLoadListener() {
			@Override
			public void OnRetryLoad(View vClicked) {
				getFeatureMedias();
			}
		});
	}
	
	private void initTitleTop() {
		mTitleTopName = (TextView) findViewById(R.id.title_top_name);
		mTitleTop = findViewById(R.id.title_top);
		mTitleTop.setOnClickListener(mOnClickListener);
		
		if(mSpecialSubject != null) {
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
	private void refreshHeadView() {
		if(mSpecialSubject != null) {
			mHeadViewDesc.setText(mSpecialSubject.desc);
		}
	}
	
	private void refreshFeatureMediaListView(boolean isError) {
		mMediaViewListVAdapter.setGroup(mFeatureMedias);
		
		if(mFeatureMedias.size() > 0) {
			return;
		}
		
		int emptyViewTopMargin = getResources().getDimensionPixelSize(R.dimen.video_common_empty_top_margin);
		if(isError) {
			mFeatureMediaLoadingListView.setEmptyView(mFeatureMediaRetryView, emptyViewTopMargin);
		} else {
			mFeatureMediaLoadingListView.setEmptyView(mFeatureMediaEmptyView, emptyViewTopMargin);
		}
	}
	
	//UI callback
	private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if(parent != null) {
				Object obj = parent.getItemAtPosition(position);
				if(obj instanceof MediaInfo) {
					Intent intent = new Intent();
					intent.setClass(FeatureMediaActivity.this, MediaDetailActivity.class);
					intent.putExtra(MediaDetailActivity.KEY_MEDIA_INFO, (MediaInfo)obj);
					intent.putExtra(MediaDetailActivity.KEY_SOURCE_PATH, SourceTagValueDef.PHONE_V6_FEATURE_MEDIA_VALUE);
					FeatureMediaActivity.this.startActivity(intent);
				}
			}
		}
	};
	private OnClickListener mOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			int id = v.getId();
			if(id == mTitleTop.getId()) {
				FeatureMediaActivity.this.finish();
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
