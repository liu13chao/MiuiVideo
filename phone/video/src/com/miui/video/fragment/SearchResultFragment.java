package com.miui.video.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import com.miui.video.MediaDetailActivity;
import com.miui.video.R;
import com.miui.video.adapter.MediaViewListVAdapter;
import com.miui.video.datasupply.SearchMediaInfoSupply.CategoryDetailInfo;
import com.miui.video.statistic.SourceTagValueDef;
import com.miui.video.type.MediaInfo;
import com.miui.video.util.DKLog;
import com.miui.video.widget.ListViewEx;
import com.miui.video.widget.ListViewEx.OnLoadMoreListener;
import com.miui.video.widget.LoadingListView;
import com.miui.video.widget.filter.MediaFilterView;
import com.miui.video.widget.filter.MediaFilterView.OnFilterViewClickListener;
import miui.app.AlertDialog;

import java.util.List;

public class SearchResultFragment extends Fragment {

	private final String TAG = SearchRecommendFragment.class.getName();
	
	private Context mContext;
	private View mContentView;
	
	//UI
	private LoadingListView mLoadingListView;
	private ListViewEx mListView;
	private View mLoadingView;
	private View mEmptyView;
	private View mLoadMoreView;
	private MediaViewListVAdapter mAdapter;
	
	private TextView mTitle;
	private TextView mFilter;
	
	private AlertDialog     mFilterDialog;
	private MediaFilterView mMediaFilterView;
	
	//data
	private CategoryDetailInfo mCurCategoryDetailInfo;
	private List<CategoryDetailInfo> mCategoryDetailInfos;
	
	private OnLoadMoreListener mOnLoadMoreListener;
	private OnFilterViewClickListener mOnFilterViewClickListener;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContentView = inflater.inflate(R.layout.search_result, null);
		return mContentView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mContext = getActivity();
		init();
	}
	
	public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
		this.mOnLoadMoreListener = onLoadMoreListener;
	}
	
	public void setOnFilterViewClickListener(OnFilterViewClickListener onFilterViewClickListener) {
		this.mOnFilterViewClickListener = onFilterViewClickListener;
	}
	
	public void dismissFilterDialog() {
		if(mFilterDialog != null && mFilterDialog.isShowing()) {
			try {
				mFilterDialog.dismiss();
			} catch (Exception e) {
				DKLog.e(TAG, e.getLocalizedMessage());
			}
		}
	}
	
	public void setShowLoading(boolean showLoading) {
		refresh();
		if(showLoading) {
			mLoadingListView.setShowLoading(true);
		} else {
			mLoadingListView.setShowLoading(false);
		}
	}
	
	public void setData(CategoryDetailInfo curCategoryDetailInfo, List<CategoryDetailInfo> categoryDetailInfos) {
		this.mCurCategoryDetailInfo = curCategoryDetailInfo;
		this.mCategoryDetailInfos = categoryDetailInfos;
		refresh();
	}
	
	//init
	private void init() {
		initUI();
	}
	
	private void initUI() {
		initTitle();
		initListView();
	}
	
	private void initTitle() {
		mTitle = (TextView) mContentView.findViewById(R.id.search_result_title);
		mFilter = (TextView) mContentView.findViewById(R.id.search_result_filter);
		mFilter.setOnClickListener(mOnClickListener);
	}
	
	private void initListView() {
		mLoadingListView = (LoadingListView) mContentView.findViewById(R.id.search_result_list);
		mListView = mLoadingListView.getListView();
		
		int height = (int) getResources().getDimension(R.dimen.video_common_list_top_padding);
		mListView.setPadding(0, height, 0, 0);
		mListView.setClipToPadding(false);
		
		mLoadMoreView = View.inflate(mContext, R.layout.load_more_view, null);
		mListView.setLoadMoreView(mLoadMoreView);
		mListView.setCanLoadMore(true);
		mListView.setOnLoadMoreListener(mOnLoadMoreListener);
		mListView.setOnItemClickListener(mOnItemClickListener);
		
		mAdapter = new MediaViewListVAdapter(mContext);
		mListView.setAdapter(mAdapter);
		
		mLoadingView = View.inflate(mContext, R.layout.load_view, null);
		mLoadingListView.setLoadingView(mLoadingView);
		
		mEmptyView = View.inflate(mContext, R.layout.empty_view_media, null);
		TextView emptyTitle = (TextView) mEmptyView.findViewById(R.id.empty_title);
		emptyTitle.setText(getResources().getString(R.string.error_empty_title));
		ImageView emptyIcon = (ImageView) mEmptyView.findViewById(R.id.empty_icon);
		emptyIcon.setBackgroundResource(R.drawable.empty_icon_error);
	}
	
	//packaged method
	private void refresh() {
		refreshTitle();
		refreshResultListView();
	}
	
	private void refreshTitle() {
		int mediaCount = 0;
		if(mCurCategoryDetailInfo != null) {
			mediaCount = mCurCategoryDetailInfo.mediaCount;
		}
		String str = mContext.getResources().getString(R.string.count_ge_result);
		str = String.format(str, mediaCount);
		mTitle.setText(str);
	}
	
	private void refreshResultListView() {
		if(mCurCategoryDetailInfo != null) {
			mListView.setCanLoadMore(mCurCategoryDetailInfo.canLoadMore);
			mAdapter.setGroup(mCurCategoryDetailInfo.mediaInfoList);
		}
	}
	
	private void showFilterDialog() {
		mMediaFilterView = new MediaFilterView(mContext);
		mMediaFilterView.setCategoryDetailInfos(mCategoryDetailInfos, mCurCategoryDetailInfo);
		mMediaFilterView.setOnFilterViewClickListener(mOnFilterViewClickListener);
		String negativeStr = getResources().getString(R.string.cancel);
		
		mFilterDialog = new AlertDialog.Builder(getActivity(), miui.R.style.Theme_Light_Dialog_Alert).create();
		mFilterDialog.setTitle(R.string.filter_program);
		mFilterDialog.setView(mMediaFilterView);
		mFilterDialog.setButton(AlertDialog.BUTTON_NEGATIVE, negativeStr, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		});
		
		try {
			mFilterDialog.show();
		} catch (Exception e) {
			DKLog.e(TAG, e.getLocalizedMessage());
		}
	}
	
	//UI callback
	private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
		
		@Override
		public void onItemClick(android.widget.AdapterView<?> parent, View view, int position, long id) {
			if(parent != null) {
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
	};
	
	private OnClickListener mOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(v == mFilter) {
				showFilterDialog();
			}
		}
	};
}
