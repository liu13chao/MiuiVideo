package com.miui.video.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.miui.video.R;
import com.miui.video.adapter.MediaListVAdapter;
import com.miui.video.addon.AddonHandler;
import com.miui.video.addon.AddonHandler.AddonHandlerInterface;
import com.miui.video.datasupply.AddonListSupply;
import com.miui.video.datasupply.AddonListSupply.AddonListListener;
import com.miui.video.type.AddonInfo;
import com.miui.video.type.BaseMediaInfo;
import com.miui.video.widget.ListViewEx;
import com.miui.video.widget.ListViewEx.OnLoadMoreListener;
import com.miui.video.widget.LoadingListView;
import com.miui.video.widget.RetryView;
import com.miui.video.widget.RetryView.OnRetryLoadListener;

public class AddonAllFragment extends Fragment {
	
	private Context mContext;
	private View mContentView;
	
	//UI
	private LoadingListView mLoadingListView;
	private ListViewEx mListView;
	private View mLoadMoreView;
	private View mLoadingView;
	private View mEmptyView;
	private RetryView mRetryView;
	private MediaListVAdapter mAdapter;
	
	//data from network
	private List<BaseMediaInfo> mAddonAllMedias = new ArrayList<BaseMediaInfo>();
	
	//data supply
	private AddonListSupply mAddonListSupply;
	
	private int mPageNo = 1;
	private boolean mCanLoadMore = true;
	
	private AddonHandler mAddonHandler;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContentView = inflater.inflate(R.layout.addon_all, null);
		return mContentView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		this.mContext = getActivity();
		init();
	}

	//init
	private void init() {
		initAddonHandler();
		initUI();
		initData();
	}
	
	private void initAddonHandler() {
		mAddonHandler = new AddonHandler(getActivity(), mAddonHandlerInterface);
	}
	
	private void initUI() {
		initListView();
	}
	
	private void initListView() {
		mLoadingListView = (LoadingListView) mContentView.findViewById(R.id.addon_all_list);
		mListView = mLoadingListView.getListView();
		
		mLoadMoreView = View.inflate(mContext, R.layout.load_more_view, null);
		mListView.setLoadMoreView(mLoadMoreView);
		mListView.setCanLoadMore(true);
		mListView.setOnLoadMoreListener(mOnLoadMoreListener);
		
		mAdapter = new MediaListVAdapter(mContext);
		mAdapter.setOnItemClickListener(mOnItemClickListener);
		mListView.setAdapter(mAdapter);
		
		mLoadingView = View.inflate(mContext, R.layout.load_view, null);
		mLoadingListView.setLoadingView(mLoadingView);
		
		mEmptyView = View.inflate(mContext, R.layout.empty_view_media, null);
		TextView emptyTitle = (TextView) mEmptyView.findViewById(R.id.empty_title);
		emptyTitle.setText(getResources().getString(R.string.error_empty_title));
		ImageView emptyIcon = (ImageView) mEmptyView.findViewById(R.id.empty_icon);
		emptyIcon.setBackgroundResource(R.drawable.empty_icon_error);
		
		mRetryView = new RetryView(mContext);
		mRetryView.setOnRetryLoadListener(new OnRetryLoadListener() {
			@Override
			public void OnRetryLoad(View vClicked) {
				getAddonList();
			}
		});
	}
	
	private void initData() {
		initDataSupply();
		getAddonList();
	}
	
	private void initDataSupply() {
		mAddonListSupply = new AddonListSupply();
		mAddonListSupply.addListener(mAddonListListener);
	}
	
	//get data
	private void getAddonList() {
		if(mAddonAllMedias.size() == 0) {
			mLoadingListView.setShowLoading(true);
		}
		mAddonListSupply.getAddonList(mPageNo, "");
	}
	
	//packaged method
	private void refreshListView(boolean isError) {
		mListView.setCanLoadMore(mCanLoadMore);
		mAdapter.setGroup(mAddonAllMedias);
		
		if(mAddonAllMedias.size() > 0) {
			return;
		}
		if(isError) {
			mLoadingListView.setEmptyView(mRetryView);
		} else {
			mLoadingListView.setEmptyView(mEmptyView);
		}
	}
	
	//UI callback
	private OnLoadMoreListener mOnLoadMoreListener = new OnLoadMoreListener() {
		
		@Override
		public void onLoadMore(ListView listView) {
			if(mCanLoadMore) {
				getAddonList();
			}
		}
	};
	
	private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Object obj = parent.getItemAtPosition(position);
			if(obj instanceof AddonInfo) {
				AddonInfo addonInfo = (AddonInfo) obj;
				mAddonHandler.onAddonClick(addonInfo);
			}
		}
	};
	
	AddonHandlerInterface mAddonHandlerInterface = new AddonHandlerInterface() {
		
		@Override
		public void onInstallComplete() {
			
		}
		
		@Override
		public void onInstall() {
			
		}
	};
	
	//data callback
	private AddonListListener mAddonListListener = new AddonListListener() {
		
		@Override
		public void onAddonListDone(List<BaseMediaInfo> addonList, int totalCount,
				boolean isError, boolean canLoadMore) {
			mCanLoadMore = canLoadMore;
			mLoadingListView.setShowLoading(false);
			mAddonAllMedias.clear();
			if(addonList != null) {
				mAddonAllMedias.addAll(addonList);
			}
			refreshListView(isError);
			
			if(canLoadMore && !isError) {
				mPageNo++;
			}
		}
	};
}
