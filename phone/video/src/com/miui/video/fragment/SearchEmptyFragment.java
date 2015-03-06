package com.miui.video.fragment;

import java.util.ArrayList;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.miui.video.MediaDetailActivity;
import com.miui.video.R;
import com.miui.video.statistic.SourceTagValueDef;
import com.miui.video.type.BaseMediaInfo;
import com.miui.video.type.MediaInfo;
import com.miui.video.widget.media.MediaViewGrid;

public class SearchEmptyFragment extends Fragment {

	private Context mContext;
	private View mContentView;
	
	//UI
	private View mEmptyMediaView;
	private MediaViewGrid mMediaViewGrid;
	
	//data
	private ArrayList<BaseMediaInfo> mRecommends;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContentView = inflater.inflate(R.layout.search_empty, null);
		return mContentView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mContext = getActivity();
		init();
	}
	
	public void setData(ArrayList<BaseMediaInfo> recommends) {
		this.mRecommends = recommends;
		refreshListView();
	}
	
	//init
	private void init() {
		initUI();
	}
	
	private void initUI() {
		initListView();
	}
	
	private void initListView() {
		TextView emptyTitle = (TextView) mContentView.findViewById(R.id.empty_title);
		emptyTitle.setText(getResources().getString(R.string.search_result_empty_text));
		ImageView emptyIcon = (ImageView) mContentView.findViewById(R.id.empty_icon);
		emptyIcon.setBackgroundResource(R.drawable.empty_icon_search);
		mEmptyMediaView = mContentView.findViewById(R.id.search_empty_media);
		mMediaViewGrid = (MediaViewGrid) mContentView.findViewById(R.id.search_empty_media_view_row);
		mMediaViewGrid.setOnItemClickListener(mOnItemClickListener);
	}
	
	//packaged method
	private void refreshListView() {
		if(mRecommends != null && mRecommends.size() > 0) {
			mMediaViewGrid.setGroup(mRecommends);
			mEmptyMediaView.setVisibility(View.VISIBLE);
			return;
		}
		mEmptyMediaView.setVisibility(View.INVISIBLE);
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
}
