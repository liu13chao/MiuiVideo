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
import android.widget.TextView;

import com.miui.video.R;
import com.miui.video.adapter.MediaListVAdapter;
import com.miui.video.addon.AddonHandler;
import com.miui.video.addon.AddonHandler.AddonHandlerInterface;
import com.miui.video.addon.AddonManager;
import com.miui.video.addon.AddonManager.OnAddonChangedListener;
import com.miui.video.type.AddonInfo;
import com.miui.video.type.BaseMediaInfo;
import com.miui.video.widget.ListViewEx;
import com.miui.video.widget.LoadingListView;

public class AddonInstalledFragment extends Fragment {

	private Context mContext;
	private View mContentView;
	
	//UI
	private LoadingListView mLoadingListView;
	private ListViewEx mListView;
	private View mLoadingView;
	private View mEmptyView;
	private MediaListVAdapter mAdapter;
	
	//data from network
	private List<BaseMediaInfo> mAddonInstalledMedias = new ArrayList<BaseMediaInfo>();
	
	//manager
	private AddonManager mAddonManager;
	private AddonHandler mAddonHandler;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContentView = inflater.inflate(R.layout.addon_installed, null);
		return mContentView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		this.mContext = getActivity();
		init();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		mAddonManager.removeListener(mAddonChangeListener);
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
		mLoadingListView = (LoadingListView) mContentView.findViewById(R.id.addon_installed_list);
		mListView = mLoadingListView.getListView();
		
		mAdapter = new MediaListVAdapter(mContext);
		mAdapter.setOnItemClickListener(mOnItemClickListener);
		mListView.setAdapter(mAdapter);
		
		mLoadingView = View.inflate(mContext, R.layout.load_view, null);
		mLoadingListView.setLoadingView(mLoadingView);
		
		mEmptyView = View.inflate(mContext, R.layout.empty_view_media, null);
		TextView emptyTitle = (TextView) mEmptyView.findViewById(R.id.empty_title);
		emptyTitle.setText(getResources().getString(R.string.addon_installed_empty_hint));
		ImageView emptyIcon = (ImageView) mEmptyView.findViewById(R.id.empty_icon);
		emptyIcon.setBackgroundResource(R.drawable.empty_icon_error);
	}
	
	private void initData() {
		initManager();
		getAddonList();
	}
	
	private void initManager() {
		mAddonManager = AddonManager.getInstance();
		mAddonManager.addListener(mAddonChangeListener);
	}
	
	//get data
	private void getAddonList() {
		if(mAddonInstalledMedias.size() == 0) {
			mLoadingListView.setShowLoading(true);
		}
		mAddonManager.load();
	}
	
	//packaged method
	private void refreshListView() {
		mAdapter.setGroup(mAddonInstalledMedias);
		
		if(mAddonInstalledMedias.size() > 0) {
			return;
		}
		mLoadingListView.setEmptyView(mEmptyView);
	}
	
	//UI callback
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
	private OnAddonChangedListener mAddonChangeListener = new OnAddonChangedListener() {
		
		@Override
		public void onAddonChanged() {
			mLoadingListView.setShowLoading(false);
			mAddonInstalledMedias.clear();
			List<AddonInfo> addonList = mAddonManager.getAddonList();
			if(addonList != null) {
				mAddonInstalledMedias.addAll(addonList);
			}
			refreshListView();
		}
	};
}
