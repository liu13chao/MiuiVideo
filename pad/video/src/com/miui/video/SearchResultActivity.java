package com.miui.video;

import java.util.ArrayList;
import java.util.HashMap;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewFlipper;
import com.miui.video.adapter.MediaViewListAdapter;
import com.miui.video.adapter.MediaViewSingleRowAdapter;
import com.miui.video.api.DKApi;
import com.miui.video.base.BaseFragmentActivity;
import com.miui.video.datasupply.SearchMediaInfoSupply;
import com.miui.video.datasupply.SearchMediaInfoSupply.CategoryDetailInfo;
import com.miui.video.datasupply.SearchMediaInfoSupply.SearchMediaInfoListener;
import com.miui.video.dialog.MediaDetailDialogFragment;
import com.miui.video.statistic.ComUserDataStatisticInfo;
import com.miui.video.statistic.ComUserDataTypeValueDef;
import com.miui.video.statistic.SearchStatisticInfo;
import com.miui.video.statistic.SourceTagValueDef;
import com.miui.video.type.MediaInfo;
import com.miui.video.util.Util;
import com.miui.video.widget.ListViewEx;
import com.miui.video.widget.ListViewEx.OnLoadMoreListener;
import com.miui.video.widget.LoadingListView;
import com.miui.video.widget.RetryView;
import com.miui.video.widget.RetryView.OnRetryLoadListener;
import com.miui.video.widget.bg.OnlineBg;
import com.miui.video.widget.media.MediaView;
import com.miui.video.widget.media.MediaView.OnMediaClickListener;
import com.miui.video.widget.searchbox.SearchBox;
import com.miui.video.widget.searchbox.SearchHintPopWindow;
import com.miui.video.widget.searchbox.SearchHintPopWindow.OnPerformSearchListener;

/**
 *@author tangfuling
 *
 */

public class SearchResultActivity extends BaseFragmentActivity {
	
	public static String SEARCH_KEY_WORD_TAG = "search_key_word";
	public static String SEARCH_KEY_SOURCE_TAG = "search_key_source";
	public static String SEARCH_KEY_POSITION_TAG = "search_key_position";
	
	//UI
	private HorizontalScrollView mHScrollView;
	private LinearLayout mBtnWrapper;
	private int mBtnTextSize;
	private int mBtnWidth;
	private int mBtnHeight;
	
	private ViewFlipper mViewFlipper;
	
	private View mRecommendView;
	private LoadingListView mRecommendLoadingListView;
	private ListViewEx mRecommendListView;
	private View mRecommendEmptyView;
	private MediaViewSingleRowAdapter mRecommendAdapter;
	
	private View mResultView;
	private LoadingListView mResultLoadingListView;
	private ListViewEx mResultListView;
	private View mResultLoadingView;
	private RetryView mResultRetryView;
	private View mLoadMoreView;
	private MediaViewListAdapter mResultAdapter;
	
	private TextView mTopName;
	private TextView mTopStatus;
	private View mTitleTop;
	
	private SearchBox mSearchBox;
	private SearchHintPopWindow mSearchHintView;
	
	//received data
	private String mSearchKey;
	private String mKeySource;
	private int mPosition;
	
	//data from network
	private ArrayList<Object> mRecommends;
	
	private HashMap<String, CategoryDetailInfo> mCategoryDetailInfoMap;
	private ArrayList<String> mCategoryNames;
	private String mCurrentCategoryName;
	private String mCategoryNameAll;
	
	//data supply
	SearchMediaInfoSupply mSearchMediaInfoSupply;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_result);
		
		init();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		mSearchHintView.refreshDefaultSearchHint();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	//init
	private void init() {
		getReceiveData();

		initResources();
		initUI();
		initDataSupply();
		initData();
	}
	
	private void initResources() {
		mBtnTextSize = getResources().getDimensionPixelSize(R.dimen.font_size_15);
		mBtnWidth = getResources().getDimensionPixelSize(R.dimen.search_result_btn_width);
		mBtnHeight = getResources().getDimensionPixelSize(R.dimen.search_result_btn_height);
		mCategoryNameAll = getResources().getString(R.string.all);
	}
	
	private void initUI() {
		initDecorView();
		initSearchBox();
		initTextViews();
		initViewFlipper();
	}
	
	private void initViewFlipper() {
		mViewFlipper = (ViewFlipper) findViewById(R.id.search_result_view_flipper);
		initSearchResultUI();
		initSearchRecommendUI();
	}
	
	private void initSearchResultUI() {
		mResultView = findViewById(R.id.search_result_result);
		initBtns();
		
		mResultLoadingListView = (LoadingListView) findViewById(R.id.search_result_list);
		mResultListView = mResultLoadingListView.getListView();
		mResultListView.setVerticalFadingEdgeEnabled(true);
		mResultListView.setFadingEdgeLength(getResources().getDimensionPixelSize(R.dimen.video_fade_edge_length));
		
		mLoadMoreView = View.inflate(this, R.layout.load_more_view, null);
		mResultListView.setLoadMoreView(mLoadMoreView);
		mResultListView.setCanLoadMore(true);
		mResultListView.setOnLoadMoreListener(mOnLoadMoreListener);
		
		mResultAdapter = new MediaViewListAdapter(this);
		mResultAdapter.setOnMediaClickListener(mOnMediaClickListener);
		mResultListView.setAdapter(mResultAdapter);
		
		mResultLoadingView = View.inflate(this, R.layout.load_view, null);
		mResultLoadingListView.setLoadingView(mResultLoadingView);
		
		mResultRetryView = new RetryView(this);
		mResultRetryView.setOnRetryLoadListener(new OnRetryLoadListener() {
			@Override
			public void OnRetryLoad(View vClicked) {
				getSearchMediaInfos(null);
			}
		});
	}
	
	private void initSearchRecommendUI() {
		mRecommendView = findViewById(R.id.search_result_recommend);
		
		mRecommendLoadingListView = (LoadingListView) findViewById(R.id.search_result_recommend_list);
		mRecommendListView = mRecommendLoadingListView.getListView();
		mRecommendListView.setVerticalFadingEdgeEnabled(true);
		mRecommendListView.setFadingEdgeLength(getResources().getDimensionPixelSize(R.dimen.video_fade_edge_length));
		
		View headView = new View(this);
		int height = (int) getResources().getDimension(R.dimen.video_common_list_top_padding);
		AbsListView.LayoutParams headViewParams = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, height);
		headView.setLayoutParams(headViewParams);
		mRecommendListView.addHeaderView(headView);
		
		mRecommendAdapter = new MediaViewSingleRowAdapter(this);
		mRecommendAdapter.setOnMediaClickListener(mOnMediaClickListener);
		mRecommendListView.setAdapter(mRecommendAdapter);
		
		mRecommendEmptyView = View.inflate(this, R.layout.empty_view, null);
		TextView emptyHint = (TextView) mRecommendEmptyView.findViewById(R.id.empty_hint);
		emptyHint.setText(getResources().getString(R.string.search_result_recommend_empty));
	}
	
	private void initDecorView() {
		ViewGroup decorView = (ViewGroup) getWindow().getDecorView();
		OnlineBg onlineBg = new OnlineBg(this);
		LayoutParams onlineBgParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		decorView.addView(onlineBg, 0, onlineBgParams);
	}
	
	private void initSearchBox() {
		mSearchBox = (SearchBox) findViewById(R.id.search_result_search_box);
		mSearchBox.setText(mSearchKey);
		if(mSearchHintView == null) {
			mSearchHintView = new SearchHintPopWindow(this, mSearchBox);
			mSearchHintView.addListener(mOnPerformSearchListener);
		}
	}
	
	private void initTextViews() {
		mTopName = (TextView) findViewById(R.id.title_top_name);
		mTopStatus = (TextView) findViewById(R.id.title_top_status);
		refreshTextViews();
	}
	
	private void initBtns() {
		mTitleTop = findViewById(R.id.title_top);
		mTitleTop.setOnClickListener(mOnClickListener);
		
		mHScrollView = (HorizontalScrollView) findViewById(R.id.search_result_h_scroll);
		mHScrollView.setHorizontalFadingEdgeEnabled(true);
		mHScrollView.setFadingEdgeLength(getResources().getDimensionPixelSize(R.dimen.video_fade_edge_length));
		
		mBtnWrapper = (LinearLayout) findViewById(R.id.search_result_btn_wrapper);
		refreshBtnWrapper();
	}
	
	private void initDataSupply() {
		mSearchMediaInfoSupply = new SearchMediaInfoSupply();
		mSearchMediaInfoSupply.addListener(mSearchMediaInfoListener);
	}
	
	private void initData() {
		mCurrentCategoryName = mCategoryNameAll;
		getSearchMediaInfos(prepareSearchStatisticInfo());
	}
	
	//get data
	private void getSearchMediaInfos(String statisticInfo) {
		CategoryDetailInfo categoryDetailInfo = getCurrentCategoryDetailInfo();
		if(categoryDetailInfo != null && categoryDetailInfo.canLoadMore == false) {
			showSearchResult(false);
			return;
		}
		
		if(categoryDetailInfo == null || categoryDetailInfo.mediaInfoList.size() == 0) {
			resetResultListView();
			mResultLoadingListView.setShowLoading(true);
		}
		showSearchResult(false);
		
		mSearchMediaInfoSupply.getSearchMediaInfolist(mSearchKey, mCurrentCategoryName, statisticInfo);
	}
	
	//packaged method
	private void getReceiveData() {
		Intent intent = getIntent();
		mSearchKey = intent.getStringExtra(SEARCH_KEY_WORD_TAG);
		mKeySource = intent.getStringExtra(SEARCH_KEY_SOURCE_TAG);
		mPosition = intent.getIntExtra(SEARCH_KEY_POSITION_TAG, -1);
	}
	
	private void resetResultListView() {
		ArrayList<Object> arrayList = null;
		mResultAdapter.setGroup(arrayList);
	}
	
	private CategoryDetailInfo getCurrentCategoryDetailInfo() {
		if(mCategoryDetailInfoMap == null) {
			return null;
		}
		 return mCategoryDetailInfoMap.get(mCurrentCategoryName);
	}
	
	private int getCurrentCategoryResultSize() {
		CategoryDetailInfo categoryDetailInfo = getCurrentCategoryDetailInfo();
		if(categoryDetailInfo != null) {
			return categoryDetailInfo.mediaCount;
		}
		return 0;
	}
	
	private void resetData() {
		mCurrentCategoryName = mCategoryNameAll;
		mCategoryDetailInfoMap.clear();
		if(mCategoryNames != null) {
			mCategoryNames.clear();
		}
		mSearchMediaInfoSupply = new SearchMediaInfoSupply();
		mSearchMediaInfoSupply.addListener(mSearchMediaInfoListener);
	}
	
	private void refreshTextViews() {
		String topStatus = getResources().getString(R.string.count_get_result);
		topStatus = String.format(topStatus, getCurrentCategoryResultSize());
		mTopStatus.setText(topStatus);
		
		String searchResultHint = "";
		if(Util.isEmpty(mSearchKey)) {
			searchResultHint = getResources().getString(R.string.search_result_input_hint);
		} else {
			searchResultHint = getResources().getString(R.string.search_result_hint);
			searchResultHint = String.format(searchResultHint, mSearchKey);
		}
		mTopName.setText(searchResultHint);
	}
	
	private void refreshViewFlipper(boolean isError) {
		CategoryDetailInfo categoryDetailInfo = getCurrentCategoryDetailInfo();
		if(categoryDetailInfo.mediaInfoList != null && categoryDetailInfo.mediaInfoList.size() > 0) {
			showSearchResult(isError);
		} else {
			if(isError) {
				showSearchResult(isError);
			} else {
				showSearchRecommend(isError);
			}
		}
	}
	
	private void showSearchResult(boolean isError) {
		refreshResultListView(isError);
		if(mViewFlipper.getCurrentView() != mResultView) {
			mViewFlipper.showNext();
		}
	}
	
	private void showSearchRecommend(boolean isError) {
		refreshRecommendListView(isError);
		if(mViewFlipper.getCurrentView() != mRecommendView) {
			mViewFlipper.showPrevious();
		}
	}
	
	private void refreshResultListView(boolean isError) {
		CategoryDetailInfo categoryDetailInfo = getCurrentCategoryDetailInfo();
		if(categoryDetailInfo != null) {
			mResultListView.setCanLoadMore(categoryDetailInfo.canLoadMore);
			mResultAdapter.setGroup(categoryDetailInfo.mediaInfoList);
			if(categoryDetailInfo.mediaInfoList != null && categoryDetailInfo.mediaInfoList.size() > 0) {
				return;
			}
		}
		if(isError){
			mResultLoadingListView.setEmptyView(mResultRetryView);
		}
	}
	
	private void refreshRecommendListView(boolean isError) {
		mRecommendAdapter.setGroup(mRecommends);
		if(mRecommends != null && mRecommends.size() > 0) {
			return;
		}
		mRecommendLoadingListView.setEmptyView(mRecommendEmptyView);
	}
	
	private void resetBtnWrapper() {
		mBtnWrapper.removeAllViews();
	}
	
	private void refreshBtnWrapper() {
		int childCount = mBtnWrapper.getChildCount();
		if(childCount > 1) {
			for(int i = 0; i < childCount; i++) {
				View view = mBtnWrapper.getChildAt(i);
				view.setSelected(false);
				Object obj = view.getTag();
				if(obj instanceof String) {
					String tag = (String) obj;
					if(mCurrentCategoryName != null && tag.equals(mCurrentCategoryName)) {
						view.setSelected(true);
					}
				}
			}
			return;
		}
		
		resetBtnWrapper();
		if(mCategoryNames != null && mCategoryNames.size() > 1) {
			for(int i = 0; i < mCategoryNames.size(); i++) {
				String categoryName = mCategoryNames.get(i);
				CategoryDetailInfo categoryDetailInfo = mCategoryDetailInfoMap.get(categoryName);
				if(categoryDetailInfo != null) {
					Button button = (Button) View.inflate(this, R.layout.button, null);
					if(i == 0) {
						button.setBackgroundResource(R.drawable.btn_channel_left_bg);
					} else if(i == mCategoryNames.size() - 1) {
						button.setBackgroundResource(R.drawable.btn_channel_right_bg);
					} else {
						button.setBackgroundResource(R.drawable.btn_channel_mid_bg);
					}
					button.setTextSize(TypedValue.COMPLEX_UNIT_PX, mBtnTextSize);
					button.setText(getBtnText(categoryName));
					button.setTag(categoryDetailInfo.categoryName);
					button.setOnClickListener(mOnClickListener);
					if(categoryName.equals(mCurrentCategoryName)) {
						button.setSelected(true);
					} else {
						button.setSelected(false);
					}
					LayoutParams params = new LayoutParams(mBtnWidth, mBtnHeight);
					button.setLayoutParams(params);
					mBtnWrapper.addView(button);
				}
			}
		}
	}
	
	private String getBtnText(String categoryName) {
		StringBuilder sb = new StringBuilder();
		sb.append(categoryName);
		sb.append("(");
		CategoryDetailInfo categoryDetailInfo = mCategoryDetailInfoMap.get(categoryName);
		if(categoryDetailInfo != null) {
			sb.append(categoryDetailInfo.mediaCount);
		} else {
			sb.append(0);
		}
		sb.append(")");
		return sb.toString();
	}
	
	//UI callback
	private OnLoadMoreListener mOnLoadMoreListener = new OnLoadMoreListener() {
		
		@Override
		public void onLoadMore(ListView listView) {
			getSearchMediaInfos(null);
		}
	};
	
	private OnClickListener mOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(v.getId() == R.id.title_top) {
				SearchResultActivity.this.finish();
				return;
			}
			Object tag = v.getTag();
			if(tag instanceof String) {
				String tagName = (String) tag;
				if(!mCurrentCategoryName.equals(tagName)) {
					mCurrentCategoryName = tagName;
					getSearchMediaInfos(null);
				}
			}
			refreshBtnWrapper();
			refreshTextViews();
		}
	};
	
	private OnMediaClickListener mOnMediaClickListener = new OnMediaClickListener() {
		
		@Override
		public void onMediaClick(MediaView mediaView, Object media) {
			if(media instanceof MediaInfo) {
				Intent intent = new Intent();
				intent.setClass(SearchResultActivity.this, MediaDetailDialogFragment.class);
				intent.putExtra(MediaDetailDialogFragment.KEY_MEDIA_INFO, (MediaInfo)media);
				intent.putExtra(MediaDetailDialogFragment.KEY_SOURCE_PATH, SourceTagValueDef.PAD_SEARCH_RESULT_VALUE);
				SearchResultActivity.this.startActivity(intent);
			}
		}
	};
	
	private OnPerformSearchListener mOnPerformSearchListener = new OnPerformSearchListener() {

		@Override
		public void onPerformSearch(String keyWord, String keySource,
				int position) {
			resetData();
			resetBtnWrapper();
			
			mSearchKey = keyWord;
			mKeySource = keySource;
			mPosition = position;
			getSearchMediaInfos(prepareSearchStatisticInfo());
		}
	};
	
	//Data callback
	private SearchMediaInfoListener mSearchMediaInfoListener = new SearchMediaInfoListener() {

		@Override
		public void onSearchMediaInfoDone(
				HashMap<String, CategoryDetailInfo> categoryDetailInfoMap,
				ArrayList<String> categoryNames, ArrayList<Object> recommends,
				boolean isError) {
			//upload statistic
			if(mCategoryDetailInfoMap == null || mCategoryDetailInfoMap.size() == 0) {
				if(!isError) {
					mCategoryDetailInfoMap = categoryDetailInfoMap;
					CategoryDetailInfo categoryDetailInfo = getCurrentCategoryDetailInfo();
					if(categoryDetailInfo != null && categoryDetailInfo.mediaInfoList != null 
							&& categoryDetailInfo.mediaInfoList.size() > 0) {
						uploadSearchResultStatisticInfo(true);
					} else {
						uploadSearchResultStatisticInfo(false);
					}
				}
			}
			
			mResultLoadingListView.setShowLoading(false);
			mCategoryDetailInfoMap = categoryDetailInfoMap;
			mCategoryNames = categoryNames;
			mRecommends = recommends;
			refreshBtnWrapper();
			refreshTextViews();
			refreshViewFlipper(isError);
		}
	};
	
	//statistic
	private String prepareSearchStatisticInfo() {
		SearchStatisticInfo  searchStatisticInfo = new SearchStatisticInfo();
		searchStatisticInfo.searchKey = mSearchKey;
		searchStatisticInfo.searchKeySource = mKeySource;
		searchStatisticInfo.searchKeyPosition = mPosition;
		return searchStatisticInfo.formatToJson();
	}
	
	private void uploadSearchResultStatisticInfo(boolean searchResultHit) {
		ComUserDataStatisticInfo statisticInfo = new ComUserDataStatisticInfo();
		statisticInfo.searchKey = mSearchKey;
		statisticInfo.comUserDataType = ComUserDataTypeValueDef.COM_USER_DATA_TYPE_SEARCH_RESULT;
		statisticInfo.searchKeySource = mKeySource;
		statisticInfo.searchKeyPosition = mPosition;
		statisticInfo.searchResultHit = searchResultHit;
		DKApi.uploadComUserData(statisticInfo.formatToJson());
	}
}
