package com.miui.video.widget.searchbox;

import java.util.ArrayList;
import java.util.Set;

import android.content.Context;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.miui.video.DKApp;
import com.miui.video.R;
import com.miui.video.adapter.SearchHintAdapter;
import com.miui.video.adapter.SearchHintAdapter.OnSearchHintActionListener;
import com.miui.video.api.DKApi;
import com.miui.video.datasupply.SearchKeyWordSupply;
import com.miui.video.datasupply.SearchKeyWordSupply.SearchKeyWordListener;
import com.miui.video.model.AppSettings;
import com.miui.video.statistic.SearchKeySourceDef;
import com.miui.video.type.SearchInfo;
import com.miui.video.util.DKLog;
import com.miui.video.util.Util;
import com.miui.video.widget.ListViewEx;
import com.miui.video.widget.LoadingListView;
import com.miui.video.widget.searchbox.EditTextIME.DispatchKeyEventPreImeListener;

/**
 *@author tangfuling
 *
 */

public class SearchHintPopWindow {
	
	private static String TAG = SearchHintPopWindow.class.getName();
	
	//received data
	private Context context;
	private SearchBox searchBox;
	
	//UI
	private SearchFullscreenPopWindow fullscreenPopWindow;
	private PopupWindow searchHintView;
	private LoadingListView searchHintLoadingListView;
	private ListViewEx searchHintListView;
	private SearchHintAdapter searchHintAdapter;
	
	//data
	private ArrayList<String> keyWordList = new ArrayList<String>();
	
	//data supply
	private SearchKeyWordSupply keyWordListSupply;
	
	//listeners
	private ArrayList<OnPerformSearchListener> listeners = new ArrayList<OnPerformSearchListener>();
	
	//requst params
	private int PAGE_SIZE = 5;
	private int PAGE_NO = 1;
	
	private int searchHintPopWidth;
	
	//flags
	private boolean isShowSearchHintView;
	
	public SearchHintPopWindow(Context context, SearchBox searchBox) {
		this.context = context;
		this.searchBox = searchBox;
		initSearchHintView();
		initDataSupply();
	}
	
	public void addListener(OnPerformSearchListener listener) {
		synchronized (listeners) {
			this.listeners.add(listener);
		}
	}	
	
	public void removeListener(OnPerformSearchListener listener) {
		synchronized (listeners) {
			this.listeners.remove(listener);
		}
	}
	
	public void refreshDefaultSearchHint() {
        Set<String> set = DKApp.getSingleton(AppSettings.class).getSearchRecommend();
		if(set != null) {
			for(String str : set) {
				if(!Util.isEmpty(str)) {
					searchBox.setHint(str);
					return;
				}
			}
		}
		searchBox.setHint(R.string.search_hint);
	}
	
	protected void setSearchWord(String searchWord) {
		if(!Util.isEmpty(searchWord)) {
			searchBox.setText(searchWord);
			Editable editable = searchBox.getText();
			Selection.setSelection(editable, searchWord.length());
		}
	}
	
	protected void setSearchHint(String hintWord) {
		if(!Util.isEmpty(hintWord)) {
			searchBox.setHint(hintWord);
		}
	}
	
	protected String getSearchBoxText() {
		String searchBoxText = searchBox.getText().toString().trim();
		if(Util.isEmpty(searchBoxText)) {
			CharSequence hint = searchBox.getHint();
			if(hint != null){
				searchBoxText = hint.toString();
			}
		}
		return searchBoxText;
	}
	
	protected String getSearchBoxUserText() {
		return searchBox.getText().toString().trim();
	}
	
	//init
	private void initSearchHintView() {
		initSearchBox();
		initSearchHintContentView();
		initPopWindow();
	}
	
	private void initSearchBox() {
		searchHintPopWidth = searchBox.getSearchHintPopWidth();
		searchBox.addTextChangedListener(mTextWatcher);
		searchBox.setOnEditorActionListener(mOnEditorActionListener);
		searchBox.setOnTouchListener(mSearchBoxOnTouchListener);
		searchBox.setDispatchKeyEventPreImeListener(mDispatchKeyEventPreImeListener);
	}
	
	private void initSearchHintContentView() {
		if(searchHintLoadingListView == null) {
			searchHintLoadingListView = new LoadingListView(context);
		}
		if(searchHintAdapter == null) {
			searchHintAdapter = new SearchHintAdapter(context);
		}
		searchHintAdapter.setOnSearchHintActionListener(mOnSearchHintActionListener);
		
		searchHintLoadingListView.setBackgroundResource(R.drawable.search_hint_bg);
		searchHintLoadingListView.setPadding(0, 0, 0, 0);
		searchHintListView = searchHintLoadingListView.getListView();
		searchHintListView.setSelector(R.drawable.vp_list_item_bg);
		searchHintListView.setAdapter(searchHintAdapter);
	}
	
	private void initPopWindow() {
		searchHintView = new PopupWindow(context);
		searchHintView.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.transparent));
		searchHintView.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
		searchHintView.setContentView(searchHintLoadingListView);
		searchHintPopWidth = searchBox.getSearchHintPopWidth();
		searchHintView.setWidth(searchHintPopWidth);
		searchHintView.setHeight(LayoutParams.WRAP_CONTENT);
		searchHintView.setFocusable(false);
		
		fullscreenPopWindow = new SearchFullscreenPopWindow(context);
		fullscreenPopWindow.getContentView().setOnTouchListener(mFullscreenOnTouchListener);
	}
	
	private void initDataSupply() {
		keyWordListSupply = new SearchKeyWordSupply();
		keyWordListSupply.addListener(mSearchKeyWordListener);
	}
	
	//packaged method
	private void getKeyWordList(String key) {
		if (!Util.isEmpty(key)) {
			SearchInfo searchInfo = new SearchInfo();
			searchInfo.mediaName = key;
			searchInfo.pageNo = PAGE_NO;
			searchInfo.pageSize = PAGE_SIZE;
			searchInfo.searchMask =  DKApi.SEARCH_MASK_ALL;
			searchInfo.mediaNameSearchType = DKApi.SEARCH_MOBILE_BY_KEYWORD;
			keyWordListSupply.getSearchKeyWordlist(searchInfo);
		}
	}
	
	private void dismissInputMethod() {
		InputMethodManager ime = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
	    if (ime != null) {  
	        ime.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);  
	    }  
	}
	
	private void performSearch(String keySource, int position) {
		dismissSearchHintView();
		
		String keyWord = getSearchBoxText();
		synchronized (listeners) {
			for(int i = 0; i < listeners.size(); i++) {
				OnPerformSearchListener listener = listeners.get(i);
				if(listener != null) {
					listener.onPerformSearch(keyWord, keySource, position);
				}
			}
		}
	}
	
	private void refreshSearchHintView() {
		String searchBoxUserText = getSearchBoxUserText();
		if(Util.isEmpty(searchBoxUserText)) {
			Set<String> hashSet = DKApp.getSingleton(AppSettings.class).getSearchRecommend();
			if(hashSet != null) {
				keyWordList.clear();
				keyWordList.addAll(hashSet);
			}
		}
		if(keyWordList.size() > 0) {
			searchHintAdapter.setGroup(keyWordList);
			showSearchHintView();
		}
	}
	
	private void showSearchHintView() {
		if(isShowSearchHintView) {
			searchBox.setCursorVisible(true);
			fullscreenPopWindow.show(searchBox);
			try {
				if(searchHintView == null) {
					searchHintView = new PopupWindow(context);
				}
				searchHintView.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.transparent));
				searchHintView.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
				searchHintView.setContentView(searchHintLoadingListView);
				searchHintPopWidth = searchBox.getSearchHintPopWidth();
				searchHintView.setWidth(searchHintPopWidth);
				searchHintView.setHeight(LayoutParams.WRAP_CONTENT);
				searchHintView.setFocusable(false);
				
				int xOffset = 0;
				int searchBoxWidth = searchBox.getWidth();
				if(searchBoxWidth != 0) {
					xOffset = searchBoxWidth - searchHintPopWidth;
				}
				searchHintView.showAsDropDown(searchBox, xOffset, 0);
			} catch (Exception e) {
				
			}	
		}
	}
	
	private void dismissSearchHintView() {
		searchBox.setCursorVisible(false);
		if(isShowSearchHintView) {
			dismissInputMethod();
		}
		isShowSearchHintView = false;
		try {
			searchHintView.dismiss();
			fullscreenPopWindow.dismiss();
		} catch (Exception e) {
			DKLog.e(TAG, e.getLocalizedMessage());
		}
	}

	//UI callback
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
			getKeyWordList(s.toString());
		}
	};

	private OnEditorActionListener mOnEditorActionListener = new OnEditorActionListener() {
		
		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			performSearch(SearchKeySourceDef.SEARCHKEY_SOURCE_DIRECT, -1);
			return true;
		}
	};
	
	private OnTouchListener mSearchBoxOnTouchListener = new OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if(event.getAction() == MotionEvent.ACTION_UP) {
				searchBox.setCursorVisible(true);
				getKeyWordList(getSearchBoxUserText());
				isShowSearchHintView = true;
				refreshSearchHintView();
			}
			return false;
		}
	};
	
	private OnTouchListener mFullscreenOnTouchListener = new OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if(event.getAction() == MotionEvent.ACTION_UP) {
				dismissSearchHintView();
			}
			return false;
		}
	};
	
	private OnSearchHintActionListener mOnSearchHintActionListener = new OnSearchHintActionListener() {
		
		@Override
		public void onSelect(String hint, int position) {
			String keyWord = hint;
			setSearchWord(keyWord);
			if(Util.isEmpty(hint)) {
				performSearch(SearchKeySourceDef.SEARCHKEY_SOURCE_RECOMMEND, position);
			} else {
				performSearch(SearchKeySourceDef.SEARCHKEY_SOURCE_SUGGESTION, position);
			}
		}
	};
	
	private DispatchKeyEventPreImeListener mDispatchKeyEventPreImeListener = new DispatchKeyEventPreImeListener() {
		
		@Override
		public boolean onDispatchKeyEventPreIme(KeyEvent event) {
			if(event.getKeyCode() == KeyEvent.KEYCODE_BACK && isShowSearchHintView) {
				dismissSearchHintView();
				return true;
			}
			return false;
		}
	};
	
	//data callback
	private SearchKeyWordListener mSearchKeyWordListener = new SearchKeyWordListener() {
		
		@Override
		public void onSearchKeyWordDone(ArrayList<String> searchKeyWordList,
				boolean isError) {
			if(searchKeyWordList != null) {
				keyWordList.clear();
				for(int i = 0; i < searchKeyWordList.size(); i++) {
					keyWordList.add(searchKeyWordList.get(i));
				}
			}
			refreshSearchHintView();
		}
	};
	
	//self def class
	public interface OnPerformSearchListener {
		public void onPerformSearch(String keyWord, String keySource, int position);
	}
}
