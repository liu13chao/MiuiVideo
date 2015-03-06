package com.miui.video;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.miui.video.api.DKApi;
import com.miui.video.base.BaseFragmentActivity;
import com.miui.video.datasupply.SearchKeyWordSupply;
import com.miui.video.datasupply.SearchKeyWordSupply.SearchKeyWordListener;
import com.miui.video.datasupply.SearchMediaInfoSupply;
import com.miui.video.datasupply.SearchMediaInfoSupply.CategoryDetailInfo;
import com.miui.video.datasupply.SearchMediaInfoSupply.SearchMediaInfoListener;
import com.miui.video.fragment.SearchEmptyFragment;
import com.miui.video.fragment.SearchHintFragment;
import com.miui.video.fragment.SearchHintFragment.OnItemClickListener;
import com.miui.video.fragment.SearchHintFragment.OnSearchHistoryClearListener;
import com.miui.video.fragment.SearchLoadingFragment;
import com.miui.video.fragment.SearchRecommendFragment;
import com.miui.video.fragment.SearchRecommendFragment.OnSearchWordClickListener;
import com.miui.video.fragment.SearchResultFragment;
import com.miui.video.model.AppSettings;
import com.miui.video.statistic.ComUserDataStatisticInfo;
import com.miui.video.statistic.ComUserDataTypeValueDef;
import com.miui.video.statistic.SearchKeySourceDef;
import com.miui.video.statistic.SearchStatisticInfo;
import com.miui.video.type.BaseMediaInfo;
import com.miui.video.type.SearchInfo;
import com.miui.video.util.AlertMessage;
import com.miui.video.util.DKLog;
import com.miui.video.util.Util;
import com.miui.video.widget.ListViewEx.OnLoadMoreListener;
import com.miui.video.widget.bg.OnlineBg;
import com.miui.video.widget.filter.MediaFilterView.OnFilterViewClickListener;
import com.miui.video.widget.searchbox.SearchBox;

/**
 *@author tangfuling
 *
 */

public class SearchActivity extends BaseFragmentActivity {
	
	public static final String TAG = SearchActivity.class.getName();
	
	public static String SEARCH_KEY_WORD_TAG = "search_key_word";
	public static String SEARCH_KEY_SOURCE_TAG = "search_key_source";
	public static String SEARCH_KEY_POSITION_TAG = "search_key_position";
	
	//UI
	private View mTitleTopBack;
	private SearchBox mSearchBox;
	private TextView mBtnSearch;
	
	private SearchRecommendFragment mRecommendFragment;
	private SearchHintFragment mHintFragment;
	private SearchEmptyFragment mEmptyFragment;
	private SearchResultFragment mResultFragment;
	private SearchLoadingFragment mLoadingFragment;
	private FragmentManager mFragmentManager;
	
	//received data
	private String mSearchKey;
	private String mKeySource;
	private int mPosition;
	
	//data from network
	private ArrayList<BaseMediaInfo> mRecommends;
	private ArrayList<String> mKeyWordList = new ArrayList<String>();
    private List<String> mHistoryList = new ArrayList<String>();
	
	private HashMap<String, CategoryDetailInfo> mCategoryDetailInfoMap;
	private ArrayList<CategoryDetailInfo> mCategoryDetailInfos;
	private String mCurrentCategoryName;
	private String mCategoryNameAll;
	
	//data supply
	private SearchMediaInfoSupply mSearchMediaInfoSupply;
	private SearchKeyWordSupply mKeyWordListSupply;
	
	//requst params
	private int PAGE_SIZE = 6;
	private int PAGE_NO = 1;
	
	private boolean mCanGetKeyWord = true;
	
	private int PAGE_RECOMMEND = 0;
	private int PAGE_EMPTY = 1;
	private int PAGE_HINT = 2;
	private int PAGE_RESULT = 3;
	private int PAGE_LOADING = 4;
	
	private int mCurPage = PAGE_RECOMMEND;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);
		init();
	}
	
	@Override
    protected void onStart() {
        super.onStart();
        mHistoryList = DKApp.getSingleton(AppSettings.class).getSearchHistory();
    }

    @Override
    protected void onStop() {
        super.onStop();
        DKApp.getSingleton(AppSettings.class).saveSearchHistory(mHistoryList);
    }
    
    private void addSearchHistory(String key){
        if(TextUtils.isEmpty(key)){
            return;
        }
        if(mHistoryList.size() >= 5){
            mHistoryList.remove(mHistoryList.size() -1);
        }
        if(mHistoryList.contains(key)){
            mHistoryList.remove(key);
        }
        mHistoryList.add(0, key);
    }

    @Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	//init
	private void init() {
		initReceiveData();
		initResources();
		initUI();

		initDataSupply();
		resetData();
	}
	
	private void initReceiveData() {
		Intent intent = getIntent();
		mSearchKey = intent.getStringExtra(SEARCH_KEY_WORD_TAG);
		mKeySource = intent.getStringExtra(SEARCH_KEY_SOURCE_TAG);
		mPosition = intent.getIntExtra(SEARCH_KEY_POSITION_TAG, -1);
	}
	
	private void initResources() {
		mCategoryNameAll = getResources().getString(R.string.all);
	}
	
	private void initUI() {
		initDecorView();
		initTitleTop();
		initSearchBox();
		initFragment();
	}
	
	private void initDecorView() {
		ViewGroup decorView = (ViewGroup) getWindow().getDecorView();
		OnlineBg onlineBg = new OnlineBg(this);
		LayoutParams onlineBgParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		decorView.addView(onlineBg, 0, onlineBgParams);
	}
	
	private void initTitleTop() {
		mTitleTopBack = findViewById(R.id.title_top_back);
		mTitleTopBack.setOnClickListener(mOnClickListener);
		mBtnSearch = (TextView) findViewById(R.id.search_result_search);
		mBtnSearch.setOnClickListener(mOnClickListener);
	}
	
	private void initSearchBox() {
		mSearchBox = (SearchBox) findViewById(R.id.search_result_search_box);
		mSearchBox.addTextChangedListener(mTextWatcher);
		mSearchBox.setOnEditorActionListener(mOnEditorActionListener);
		mSearchBox.setHint(R.string.search_hint);
		mSearchBox.setOnFocusChangeListener(mOnFocusChanged);
	}
	
	private void initFragment() {
//		Bundle bundle = new Bundle();
//		bundle.putString(SEARCH_KEY_WORD_TAG, mSearchKey);
//		bundle.putString(SEARCH_KEY_SOURCE_TAG, mKeySource);
//		bundle.putInt(SEARCH_KEY_POSITION_TAG, mPosition);
		mFragmentManager = getFragmentManager();
		mRecommendFragment = (SearchRecommendFragment) mFragmentManager.findFragmentById(R.id.search_recommend);
		mHintFragment = (SearchHintFragment) mFragmentManager.findFragmentById(R.id.search_hint);
		mEmptyFragment = (SearchEmptyFragment) mFragmentManager.findFragmentById(R.id.search_empty);
		mResultFragment = (SearchResultFragment) mFragmentManager.findFragmentById(R.id.search_result);
		mRecommendFragment.setOnSearchWordClickListener(mOnSearchWordClickListener);
		mHintFragment.addListener(mOnItemClickListener);
		mResultFragment.setOnLoadMoreListener(mOnLoadMoreListener);
		mResultFragment.setOnFilterViewClickListener(mOnFilterViewClickListener);
		mLoadingFragment = (SearchLoadingFragment) mFragmentManager.findFragmentById(R.id.search_load);
		mHintFragment.setOnSearchHistoryClearListener(mHistoryClearListener);
		showRecommendView();
	}
	
	private void initDataSupply() {
		mKeyWordListSupply = new SearchKeyWordSupply();
		mKeyWordListSupply.addListener(mSearchKeyWordListener);
	}
	
	//get data
	private void getKeyWordList(String key) {
		if (mCanGetKeyWord && !Util.isEmpty(key)) {
			SearchInfo searchInfo = new SearchInfo();
			searchInfo.mediaName = key;
			searchInfo.pageNo = PAGE_NO;
			searchInfo.pageSize = PAGE_SIZE;
			searchInfo.searchMask =  DKApi.SEARCH_MASK_ALL;
			searchInfo.mediaNameSearchType = DKApi.SEARCH_MOBILE_BY_KEYWORD;
			mKeyWordListSupply.getSearchKeyWordlist(searchInfo);
		}
	}
	
	private void getSearchMediaInfos(String statisticInfo) {
		if(mSearchMediaInfoSupply != null) {
			mSearchMediaInfoSupply.getSearchMediaInfolist(mSearchKey, mCurrentCategoryName, statisticInfo);
		}
	}
	
	//packaged method
	private void showRecommendView() {
		FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
		fragmentTransaction.hide(mHintFragment);
		fragmentTransaction.hide(mEmptyFragment);
		fragmentTransaction.hide(mResultFragment);
		fragmentTransaction.hide(mLoadingFragment);
		fragmentTransaction.show(mRecommendFragment);
		try {
			fragmentTransaction.commit();
		} catch (Exception e) {
			DKLog.e(TAG, e.getLocalizedMessage());
		}
		
		mRecommendFragment.setData(mKeySource);
		mCurPage = PAGE_RECOMMEND;
	}
	
	private void showEmptyView() {
		FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
		fragmentTransaction.hide(mHintFragment);
		fragmentTransaction.hide(mRecommendFragment);
		fragmentTransaction.hide(mResultFragment);
		fragmentTransaction.hide(mLoadingFragment);
		fragmentTransaction.show(mEmptyFragment);
		try {
			fragmentTransaction.commit();
		} catch (Exception e) {
			DKLog.e(TAG, e.getLocalizedMessage());
		}
		
		mEmptyFragment.setData(mRecommends);
		mCurPage = PAGE_EMPTY;
	}
	
	private void showHintView() {
		FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
		fragmentTransaction.hide(mEmptyFragment);
		fragmentTransaction.hide(mRecommendFragment);
		fragmentTransaction.hide(mResultFragment);
		fragmentTransaction.hide(mLoadingFragment);
		fragmentTransaction.show(mHintFragment);
		try {
			fragmentTransaction.commit();
		} catch (Exception e) {
			DKLog.e(TAG, "showHintView exception.", e);
		}
		mHintFragment.setSearchKey(mSearchKey);
		if(!TextUtils.isEmpty(mSearchKey)){
		      mHintFragment.setSearchKeyWord(mKeyWordList);
		}else{
            mHintFragment.setHistoryList(mHistoryList);
		}
		mCurPage = PAGE_HINT;
	}
	
	private void showResultView(CategoryDetailInfo categoryDetailInfo) {
		FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
		fragmentTransaction.hide(mHintFragment);
		fragmentTransaction.hide(mRecommendFragment);
		fragmentTransaction.hide(mEmptyFragment);
		fragmentTransaction.hide(mLoadingFragment);
		fragmentTransaction.show(mResultFragment);
		try {
			fragmentTransaction.commit();
		} catch (Exception e) {
			DKLog.e(TAG, e.getLocalizedMessage());
		}
		
		mResultFragment.setData(categoryDetailInfo, mCategoryDetailInfos);
		mCurPage = PAGE_RESULT;
	}
	
	private void setShowLoading(boolean showLoading) {
		FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
		fragmentTransaction.hide(mRecommendFragment);
		fragmentTransaction.hide(mHintFragment);
		fragmentTransaction.hide(mEmptyFragment);
		fragmentTransaction.hide(mResultFragment);
		fragmentTransaction.show(mLoadingFragment);
		try {
			fragmentTransaction.commit();
		} catch (Exception e) {
			DKLog.e(TAG, e.getLocalizedMessage());
		}
		
		mCurPage = PAGE_LOADING;
	}
	
	private void resetData() {
		mCurrentCategoryName = mCategoryNameAll;
		if(mCategoryDetailInfoMap != null) {
			mCategoryDetailInfoMap.clear();
		}
		if(mCategoryDetailInfos != null) {
			mCategoryDetailInfos.clear();
		}
		mSearchMediaInfoSupply = new SearchMediaInfoSupply();
		mSearchMediaInfoSupply.addListener(mSearchMediaInfoListener);
	}
	
	private CategoryDetailInfo getCurrentCategoryDetailInfo() {
		if(mCategoryDetailInfoMap == null) {
			return null;
		}
		 return mCategoryDetailInfoMap.get(mCurrentCategoryName);
	}
	
	private String getSearchBoxText() {
		String searchBoxText = mSearchBox.getText().toString().trim();
//		if(Util.isEmpty(searchBoxText)) {
//			CharSequence hint = mSearchBox.getHint();
//			if(hint != null){
//				searchBoxText = hint.toString();
//			}
//		}
		return searchBoxText;
	}
	
	private void setSearchBoxText(String text) {
		if(!Util.isEmpty(text)) {
			mCanGetKeyWord = false;
			mSearchBox.setText(text);
			Editable editable = mSearchBox.getText();
			Selection.setSelection(editable, text.length());
		}
	}
	
	//UI callback
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK && mCurPage != PAGE_RECOMMEND) {
			showRecommendView();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private OnClickListener mOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(v == mTitleTopBack) {
				finish();
			} else if(v == mBtnSearch) {
			    Util.closeInputMethodWindow(SearchActivity.this);
				mSearchKey = getSearchBoxText();
				if(Util.isEmpty(mSearchKey)) {
					AlertMessage.show(R.string.search_result_input_hint);
					return;
				}
				addSearchHistory(mSearchKey);
				mKeySource = SearchKeySourceDef.SEARCHKEY_SOURCE_DIRECT;
				mPosition = -1;
				resetData();
				getSearchMediaInfos(prepareSearchStatisticInfo());
				setShowLoading(true);
			}
		}
	};
	
	//inputmethod input
	private TextWatcher mTextWatcher = new TextWatcher() {
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
		}

		@Override
		public void afterTextChanged(Editable s) {
		    if(!TextUtils.isEmpty(s.toString())){
		        mSearchKey = s.toString();
		        getKeyWordList(s.toString());
		        mCanGetKeyWord = true;
		    }else{
		        if(mHistoryList.size() > 0){
		            mSearchKey = "";
		            showHintView();
		        }else{
		            showRecommendView();
		        }
		    }
		}
	};

	//inutmethod search
	private OnEditorActionListener mOnEditorActionListener = new OnEditorActionListener() {
		
		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		    if(actionId == EditorInfo.IME_ACTION_SEARCH){
		        Util.closeInputMethodWindow(SearchActivity.this);
		        mSearchKey = getSearchBoxText();
		        if(Util.isEmpty(mSearchKey)) {
		            AlertMessage.show(R.string.search_result_input_hint);
		            return true;
		        }
		        addSearchHistory(mSearchKey);
		        mKeySource = SearchKeySourceDef.SEARCHKEY_SOURCE_DIRECT;
		        mPosition = -1;
		        resetData();
		        getSearchMediaInfos(prepareSearchStatisticInfo());
		        setShowLoading(true);
		    }
		    return false;
		}
	};
	
	private OnFocusChangeListener mOnFocusChanged = new OnFocusChangeListener(){
        @Override
        public void onFocusChange(View view, boolean focused) {
            if(focused && view == mSearchBox.getEditText()){
                if(TextUtils.isEmpty(mSearchKey) && mHistoryList.size() > 0){
                    showHintView();
                }
             }
        }
	};
	
	//search recommend click
	private OnSearchWordClickListener mOnSearchWordClickListener = new OnSearchWordClickListener() {
		
		@Override
		public void onSearchWordClick(String keyWord) {
			mSearchKey = keyWord;
			mKeySource = SearchKeySourceDef.SEARCHKEY_SOURCE_RECOMMEND;
			mPosition = -1;
			
			setSearchBoxText(mSearchKey);
			resetData();
			getSearchMediaInfos(prepareSearchStatisticInfo());
			setShowLoading(true);
		}
	};
	
	//search hint click
	private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
		
		@Override
		public void onItemClick(String keyWord, String keySource, int position) {
		    Util.closeInputMethodWindow(SearchActivity.this);
			mSearchKey = keyWord;
			mKeySource = keySource;
			mPosition = position;
			setSearchBoxText(mSearchKey);
			resetData();
			getSearchMediaInfos(prepareSearchStatisticInfo());
			setShowLoading(true);
		}
	};
	
	private OnLoadMoreListener mOnLoadMoreListener = new OnLoadMoreListener() {
		
		@Override
		public void onLoadMore(ListView listView) {
			getSearchMediaInfos("");
		}
	};
	
	private OnFilterViewClickListener mOnFilterViewClickListener = new OnFilterViewClickListener() {
		
		@Override
		public void onFilterViewClick(CategoryDetailInfo categoryDetailInfo) {
			mResultFragment.dismissFilterDialog();
			if(categoryDetailInfo != null) {
				String categoryName = categoryDetailInfo.categoryName;
				if(!categoryName.equals(mCurrentCategoryName)) {
					mCurrentCategoryName = categoryName;
					getSearchMediaInfos("");
				}
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
			
			//refresh data and ui 
			mCategoryDetailInfoMap = categoryDetailInfoMap;
			mCategoryDetailInfos = categoryDetailInfos;
			mRecommends = recommends;
			setShowLoading(false);
			
			CategoryDetailInfo categoryDetailInfo = getCurrentCategoryDetailInfo();
			if(categoryDetailInfo != null) {
				if(categoryDetailInfo.mediaInfoList != null && categoryDetailInfo.mediaInfoList.size() > 0) {
					showResultView(categoryDetailInfo);
					return;
				}
			}
			showEmptyView();
		}
	};
	
	private SearchKeyWordListener mSearchKeyWordListener = new SearchKeyWordListener() {
		@Override
		public void onSearchKeyWordDone(ArrayList<String> searchKeyWordList,
				boolean isError) {
			if(searchKeyWordList != null) {
				mKeyWordList.clear();
				for(int i = 0; i < searchKeyWordList.size(); i++) {
					mKeyWordList.add(searchKeyWordList.get(i));
				}
			}
			showHintView();
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
	
	private OnSearchHistoryClearListener mHistoryClearListener = new OnSearchHistoryClearListener() {
        @Override
        public void onSearchHistoryClear() {
            mHistoryList.clear();
            showRecommendView();
        }
    };
}
