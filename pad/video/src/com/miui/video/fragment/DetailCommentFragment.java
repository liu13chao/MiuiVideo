package com.miui.video.fragment;

import java.util.ArrayList;
import com.miui.video.R;
import com.miui.video.adapter.ReviewListAdapter;
import com.miui.video.api.def.ReviewTypeValueDef;
import com.miui.video.datasupply.ReviewListSupply;
import com.miui.video.datasupply.ReviewListSupply.ReviewListListener;
import com.miui.video.dialog.CommentEditDialog;
import com.miui.video.dialog.CommentReviewDialog;
import com.miui.video.dialog.MediaDetailDialogFragment;
import com.miui.video.type.MediaInfo;
import com.miui.video.type.MediaReview;
import com.miui.video.widget.ListViewEx;
import com.miui.video.widget.LoadingListView;
import com.miui.video.widget.RetryView;
import com.miui.video.widget.RetryView.OnRetryLoadListener;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 *@author tangfuling
 *
 */

public class DetailCommentFragment extends Fragment {
	
	private Context mContext;
	
	//UI
	private LoadingListView mReviewListLoadingListView;
	private ListViewEx mReviewListListView;
	private View mReviewListLoadView;
	private View mReviewListEmptyView;
	private RetryView mReviewListRetryView;
	private ReviewListAdapter mReviewListAdapter;
	private View mReviewListFootView;
	private Button mBtnComment;
	private Button mMoreComment;
	private Button mBtnCommentEmpty;
	
	//received data
	private MediaInfo mMediaInfo;
		
	//data from network
	private int mCommentTotalCount;
	private ArrayList<MediaReview> mReviewLists;
	
	//data supply
	private ReviewListSupply mReviewListSupply;
	
	private int mReviewListSize = 3;
	private int mPageNo = 1;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getActivity();
		
		Bundle bundle = getArguments();
		if(bundle != null) {
			Object mediaInfo = bundle.getSerializable(MediaDetailDialogFragment.KEY_MEDIA_INFO);
			if(mediaInfo instanceof MediaInfo) {
				this.mMediaInfo = (MediaInfo) mediaInfo;
			}
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		getReviewListDataUser();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mReviewListLoadingListView = new LoadingListView(mContext);
		return mReviewListLoadingListView;
		
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		init();
	}
	
	//public method
	public void onSelected() {
		getReviewListDataUser();
	}
	
	//init
	private void init() {
		initUI();
		initDataSupply();
	}
	
	private void initUI() {
		mReviewListListView = mReviewListLoadingListView.getListView();
		
		initFootView();
		
		mReviewListAdapter = new ReviewListAdapter(mContext);
		mReviewListListView.setAdapter(mReviewListAdapter);
		
		mReviewListLoadView = View.inflate(mContext, R.layout.load_view_black, null);
		mReviewListLoadingListView.setLoadingView(mReviewListLoadView);
		
		mReviewListEmptyView = View.inflate(mContext, R.layout.empty_view_comment, null);
		mBtnCommentEmpty = (Button) mReviewListEmptyView.findViewById(R.id.emtpy_comment_btn);
		mBtnCommentEmpty.setOnClickListener(mOnClickListener);
		TextView emptyHint = (TextView) mReviewListEmptyView.findViewById(R.id.empty_hint);
		emptyHint.setText(mContext.getResources().getString(R.string.detail_review_list_empty_hint));
		
		mReviewListRetryView = new RetryView(mContext, RetryView.STYLE_BLACK);
		mReviewListRetryView.setOnRetryLoadListener(new OnRetryLoadListener() {
			@Override
			public void OnRetryLoad(View vClicked) {
				getReviewListDataUser();
			}
		});
	}
	
	private void initFootView() {
		mReviewListFootView = View.inflate(mContext, R.layout.detail_review_list_foot, null);
		mReviewListListView.addFooterView(mReviewListFootView);
		hideReviewListFootView();
		
		mBtnComment = (Button) mReviewListFootView.findViewById(R.id.detail_review_list_comment);
		mMoreComment = (Button) mReviewListFootView.findViewById(R.id.detail_review_list_more_comment);
		mBtnComment.setOnClickListener(mOnClickListener);
		mMoreComment.setOnClickListener(mOnClickListener);
	}
	
	private void initDataSupply() {
		if(mReviewListSupply == null) {
			mReviewListSupply = new ReviewListSupply();
			mReviewListSupply.addListener(mReviewListListener);
		}
	}
	
	//get data
	private void getReviewListDataUser() {
		if(mMediaInfo != null) {
			if(mReviewLists == null || mReviewLists.size() == 0) {
				mReviewListLoadingListView.setShowLoading(true);
			}
			mReviewListSupply.getReviewListUser(mMediaInfo.mediaid, mPageNo, mReviewListSize,
					ReviewTypeValueDef.REVIEW_TYPE_ALL);
		}
	}
	
	//packaged method
	private void showReviewListFootView() {
		mReviewListFootView.setVisibility(View.VISIBLE);
		String str = mContext.getResources().getString(R.string.see_all_count_comment);
		str = String.format(str, mCommentTotalCount);
		mMoreComment.setText(str);
		
		refreshFootView();
	}
	
	private void hideReviewListFootView() {
		mReviewListFootView.setVisibility(View.INVISIBLE);
	}
	
	private void refreshFootView() {
		if(mCommentTotalCount > mReviewListSize) {
			mMoreComment.setVisibility(View.VISIBLE);
			mBtnComment.setBackgroundResource(R.drawable.btn_detail_comment_left);
		} else {
			mMoreComment.setVisibility(View.GONE);
			mBtnComment.setBackgroundResource(R.drawable.btn_dialog_bg);
		}
	}
	
	private void refreshReviewListListView(boolean isError) {
		if(mReviewLists != null && mReviewLists.size() > 0) {
			mReviewListAdapter.setGroup(mReviewLists);
			showReviewListFootView();
			return;
		} 
		if(isError){
			mReviewListLoadingListView.setEmptyView(mReviewListRetryView);
		}else{
			mReviewListLoadingListView.setEmptyView(mReviewListEmptyView);
		}
	}
	
	private void startCommentEditDialog() {
		Intent intent = new Intent();
		intent.setClass(mContext, CommentEditDialog.class);
		intent.putExtra(CommentEditDialog.KEY_MEDIA_INFO, mMediaInfo);
		startActivity(intent);
	}
	
	private void startCommentReviewDialog() {
		Intent intent = new Intent();
		intent.setClass(mContext, CommentReviewDialog.class);
		intent.putExtra(CommentReviewDialog.KEY_MEDIA_INFO, mMediaInfo);
		startActivity(intent);
	}

	//UI callback
	private OnClickListener mOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			int id = v.getId();
			if(id == R.id.detail_review_list_comment) {
				startCommentEditDialog();
			} else if(id == R.id.detail_review_list_more_comment) {
				startCommentReviewDialog();
			} else if(id == R.id.emtpy_comment_btn) {
				startCommentEditDialog();
			}
		}
	};

	//data callback
	private ReviewListListener mReviewListListener = new ReviewListListener() {
		
		@Override
		public void onReviewListDone(boolean isError, boolean canLoadMore) {
			mReviewListLoadingListView.setShowLoading(false);
			mReviewListListView.setCanLoadMore(canLoadMore);
			
			mCommentTotalCount = mReviewListSupply.getTotalCount();
			mReviewLists = mReviewListSupply.getReviewList(ReviewTypeValueDef.REVIEW_TYPE_ALL);
			refreshReviewListListView(isError);
		}
	};
}
