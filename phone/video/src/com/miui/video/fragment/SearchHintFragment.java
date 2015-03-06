package com.miui.video.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.miui.video.R;
import com.miui.video.adapter.SearchHintAdapter;
import com.miui.video.adapter.SearchHintAdapter.OnSearchHintActionListener;
import com.miui.video.statistic.SearchKeySourceDef;
import com.miui.video.widget.ListViewEx;
import com.miui.video.widget.LoadingListView;

public class SearchHintFragment extends Fragment {

	//received data
	private Context context;
	
	//UI
	private View contentView;
	private LoadingListView searchHintLoadingListView;
	private ListViewEx searchHintListView;
	private SearchHintAdapter searchHintAdapter;
	private View mClearSearchFooter;
	
	//data
	private List<String> mKeyWordList = new ArrayList<String>();
    private List<String> mHistoryList = new ArrayList<String>();
    private String mSearchKey = "";
	
	//listeners
	private List<OnItemClickListener> listeners = new ArrayList<OnItemClickListener>();
	private OnSearchHistoryClearListener mOnSearchHistoryClearListener;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		contentView = LayoutInflater.from(getActivity()).inflate(R.layout.search_hint, container, false);
		return contentView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		context = getActivity();
		init();
	}
	
	public void addListener(OnItemClickListener listener) {
		if(listener != null && !listeners.contains(listener)) {
			listeners.add(listener);
		}
	}	
	
	public void removeListener(OnItemClickListener listener) {
		if(listener != null) {
			listeners.remove(listener);
		}
	}
	
	public void setSearchKeyWord(List<String> keyWordList) {
		mKeyWordList = keyWordList;
		refreshSearchHintView(mKeyWordList);
		removeClearView();
	}
	
	public void setHistoryList(List<String> historyList){
	    mHistoryList = historyList;
	    refreshSearchHintView(mHistoryList);
	    addClearView();
	}
	
	private void addClearView(){
	    if(mClearSearchFooter.getParent() == null){
	           searchHintListView.addFooterView(mClearSearchFooter);
	    }
	}
	
	private void removeClearView(){
	    if(mClearSearchFooter.getParent() != null){
	        searchHintListView.removeFooterView(mClearSearchFooter);
	    }
	}
	
	public void setSearchKey(String searchKey){
	    mSearchKey = searchKey;
	}
	
	//init
	private void init() {
		initUI();
	}
	
	private void initUI() {
		searchHintLoadingListView = (LoadingListView) contentView.findViewById(R.id.search_hint_list);
		searchHintAdapter = new SearchHintAdapter(context);
		searchHintAdapter.setOnSearchHintActionListener(mOnSearchHintActionListener);
		searchHintListView = searchHintLoadingListView.getListView();
		
		int height = (int) getResources().getDimension(R.dimen.size_30);
		searchHintListView.setPadding(0, height, 0, 0);
		searchHintListView.setClipToPadding(false);
		searchHintListView.setSelector(R.drawable.vp_list_item_bg);
		
		searchHintListView.setAdapter(searchHintAdapter);
		
		mClearSearchFooter = View.inflate(getActivity(), R.layout.search_history_clear, null);
		mClearSearchFooter.findViewById(R.id.clear_history).setOnClickListener(mOnSearchClearHistory);
	}
	
	//packaged method
	private void refreshSearchHintView(List<String> list) {
		searchHintAdapter.setGroup(list);
		searchHintAdapter.setSearchKey(mSearchKey);
	}
	
	private void notifyItemClick(String keyWord, int position) {
		for(int i = 0; i < listeners.size(); i++) {
			OnItemClickListener listener = listeners.get(i);
			if(listener != null) {
				listener.onItemClick(keyWord, SearchKeySourceDef.SEARCHKEY_SOURCE_SUGGESTION, position);
			}
		}
	}
	
    public void setOnSearchHistoryClearListener(
            OnSearchHistoryClearListener onSearchHistoryClearListener) {
        this.mOnSearchHistoryClearListener = onSearchHistoryClearListener;
    }

    //UI callback
	private OnSearchHintActionListener mOnSearchHintActionListener = new OnSearchHintActionListener() {
		@Override
		public void onSelect(String hint, int position) {
			notifyItemClick(hint, position);
		}
	};
	
	private OnClickListener mOnSearchClearHistory = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if(mOnSearchHistoryClearListener != null){
                mOnSearchHistoryClearListener.onSearchHistoryClear();
            }
        }
    };
	
	//self def class
	public interface OnItemClickListener {
		public void onItemClick(String keyWord, String keySource, int position);
	}
	
	public interface OnSearchHistoryClearListener{
	    public void onSearchHistoryClear();
	}
}
