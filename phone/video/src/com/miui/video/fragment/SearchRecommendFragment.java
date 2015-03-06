package com.miui.video.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.miui.video.DKApp;
import com.miui.video.MediaDetailActivity;
import com.miui.video.R;
import com.miui.video.datasupply.SearchMediaInfoSupply;
import com.miui.video.datasupply.SearchMediaInfoSupply.CategoryDetailInfo;
import com.miui.video.datasupply.SearchMediaInfoSupply.SearchMediaInfoListener;
import com.miui.video.model.AppSettings;
import com.miui.video.statistic.SearchStatisticInfo;
import com.miui.video.statistic.SourceTagValueDef;
import com.miui.video.type.BaseMediaInfo;
import com.miui.video.type.MediaInfo;
import com.miui.video.widget.SearchGridView;
import com.miui.video.widget.SearchGridView.OnGridItemClickListener;
import com.miui.video.widget.media.MediaViewGrid;

public class SearchRecommendFragment extends Fragment {

	public final String TAG = SearchRecommendFragment.class.getName();
	
	private Context mContext;
	private View mContentView;
	
	//UI
	private SearchGridView mSearchRecommendWordView; 
	private View mRecommendMediaView;
	private MediaViewGrid mMediaViewGrid;
	
	//data supply
	private SearchMediaInfoSupply mSearchMediaInfoSupply;
	
	//data from network
	private ArrayList<BaseMediaInfo> mRecommends;
	
	private List<String> mRecommendKeyWords = new ArrayList<String>();
	
	//constant data
	private String mSearchKey = "";
	private String mCategoryName = "";
	private int mSearchKeyPosition = -1;
	
	//received data
	private String mKeySource;
	
	private OnSearchWordClickListener mOnSearchWordClickListener;
	
	@SuppressLint("InflateParams")
    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContentView = inflater.inflate(R.layout.search_recommend, null);
		return mContentView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mContext = getActivity();
		init();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if(mRecommends == null || mRecommends.size() == 0) {
			getRecommendInfos(prepareSearchStatisticInfo());
		}
	}
	
	public void setData(String keySource) {
		this.mKeySource = keySource;
	}
	
	public void setOnSearchWordClickListener(OnSearchWordClickListener onSearchWordClickListener) {
		this.mOnSearchWordClickListener = onSearchWordClickListener;
	}
	
	//init
	private void init() {
		initDataSupply();
		initRecommendKeyWord();
		
		initUI();
		refresh();
	}
	
	private void initDataSupply() {
		mSearchMediaInfoSupply = new SearchMediaInfoSupply();
		mSearchMediaInfoSupply.addListener(mSearchMediaInfoListener);
	}
	
	private void initRecommendKeyWord() {
		mRecommendKeyWords.clear();
		Set<String> set = DKApp.getSingleton(AppSettings.class).getSearchRecommend();;
		if(set != null) {
			mRecommendKeyWords.addAll(set);
		}
	}
	
	private void initUI() {
		initSearchRecommendMediaView();
		initSearchRecommendWordView();
	}
	
	private void initSearchRecommendMediaView() {
		mRecommendMediaView = mContentView.findViewById(R.id.search_recommend_media);
		mMediaViewGrid = (MediaViewGrid) mContentView.findViewById(R.id.search_recommend_media_view_grid);
		mMediaViewGrid.setOnItemClickListener(mOnItemClickListener);
	}
	
	private void initSearchRecommendWordView() {
		mSearchRecommendWordView = (SearchGridView) mContentView.findViewById(R.id.search_recommend_word_view);
		mSearchRecommendWordView.setOnGridItemClickListener(mOnGridItemClickListener);
		mSearchRecommendWordView.setItems(mRecommendKeyWords);
		if(mRecommendKeyWords.size() == 0){
		    mContentView.findViewById(R.id.search_recommended_words_root).setVisibility(View.GONE);
		}else{
		    mContentView.findViewById(R.id.search_recommended_words_root).setVisibility(View.VISIBLE);
		}
	}
	
	//packaged method
	private void refresh() {
		refreshRecommendMediaView();
	}
	
	private void refreshRecommendMediaView() {
		if(mRecommends != null && mRecommends.size() > 0) {
			mMediaViewGrid.setGroup(mRecommends);
			mRecommendMediaView.setVisibility(View.VISIBLE);
			return;
		}
		mRecommendMediaView.setVisibility(View.INVISIBLE);
	}
	
	//get data
	private void getRecommendInfos(String statisticInfo) {
		mSearchMediaInfoSupply.getSearchMediaInfolist(mSearchKey, mCategoryName, statisticInfo);
	}
	
	//UI callback
	private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Object obj = parent.getItemAtPosition(position);
			if(obj instanceof MediaInfo) {
				Intent intent = new Intent();
				intent.setClass(mContext, MediaDetailActivity.class);
				intent.putExtra(MediaDetailActivity.KEY_MEDIA_INFO, (MediaInfo)obj);
				intent.putExtra(MediaDetailActivity.KEY_SOURCE_PATH, SourceTagValueDef.PHONE_V6_SEARCH_RESULT_VALUE);
				mContext.startActivity(intent);
			}
		}
	};
	
	private OnGridItemClickListener mOnGridItemClickListener = new OnGridItemClickListener() {
		
		@Override
		public void onGridItemClick(String itemName) {
			if(mOnSearchWordClickListener != null) {
				mOnSearchWordClickListener.onSearchWordClick(itemName);
			}
		}
	};
	
	//Data callback
	private SearchMediaInfoListener mSearchMediaInfoListener = new SearchMediaInfoListener() {

		@Override
		public void onSearchMediaInfoDone(
				HashMap<String, CategoryDetailInfo> categoryDetailInfoMap,
				ArrayList<CategoryDetailInfo> categoryDetailInfos,
				ArrayList<BaseMediaInfo> recommends, boolean isError) {
			mRecommends = recommends;
			refresh();
		}
	};
	
	//statistic
	private String prepareSearchStatisticInfo() {
		SearchStatisticInfo  searchStatisticInfo = new SearchStatisticInfo();
		searchStatisticInfo.searchKey = mSearchKey;
		searchStatisticInfo.searchKeySource = mKeySource;
		searchStatisticInfo.searchKeyPosition = mSearchKeyPosition;
		return searchStatisticInfo.formatToJson();
	}
	
	//self def class
	public interface OnSearchWordClickListener {
		public void onSearchWordClick(String keyWord);
	}
}
