package com.miui.video.dialog;

import java.util.ArrayList;
import com.miui.video.R;
import com.miui.video.adapter.CommentReviewAdapter;
import com.miui.video.api.def.ReviewTypeValueDef;
import com.miui.video.base.BaseDialog;
import com.miui.video.datasupply.ReviewListSupply;
import com.miui.video.datasupply.ReviewListSupply.ReviewListListener;
import com.miui.video.type.MediaInfo;
import com.miui.video.type.MediaReview;
import com.miui.video.util.DKLog;
import com.miui.video.widget.ListViewEx;
import com.miui.video.widget.LoadingListView;
import com.miui.video.widget.RetryView;
import com.miui.video.widget.ScoreStatusView;
import com.miui.video.widget.ListViewEx.OnLoadMoreListener;
import com.miui.video.widget.RetryView.OnRetryLoadListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

/**
 *@author tangfuling
 *
 */

public class CommentReviewDialog extends BaseDialog {
	private static String TAG = CommentReviewDialog.class.getName();
	
	public static final String KEY_MEDIA_INFO = "mediaInfo";
	
	//UI
	private LoadingListView mCommentReviewLoadingListView;
	private ListViewEx mCommentReviewListView;
	private View mLoadMoreView;
	private View mCommentReviewLoadView;
	private View mCommentReviewEmptyView;
	private RetryView mCommentReviewRetryView;
	private Button mBtnBack;
	private Button mBtnEdit;
	private Button mBtnAll;
	private Button mBtnPositive;
	private Button mBtnNegative;
	private ScoreStatusView mScoreStatusView;
	private CommentReviewAdapter mCommentReviewAdapter;
	
	private TextView mCountCommentTv;
	private TextView mCommentAvgScoreTv;
	
	//received data
	private MediaInfo mMediaInfo;
	
	//data from network
	private float mCommentAvgScore;
	private int mCommentTotalCount;
	private ArrayList<MediaReview> mReviewLists;
	private float[] mScorePercent;
	
	//data supply
	private ReviewListSupply mReviewListSupply;
	
	private int mPageSize = 10;
	private int mReviewType = ReviewTypeValueDef.REVIEW_TYPE_ALL;
	private int mPageNoAll = 1;
	private int mPageNoPositive = 1;
	private int mPageNoNegative = 1;
	
	//flags
	private boolean mCanLoadMore = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.comment_review);
		init();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		resetPageNo();
		getReviewListDataUser();
	}
	
	//init
	private void init() {
	    initReceivedData();
		initUI();
		initDataSupply();
		refreshTopStatus();
	}
	
	private void initReceivedData() {
		Intent intent = getIntent();
		Object obj = intent.getSerializableExtra(KEY_MEDIA_INFO);
		if(obj instanceof MediaInfo) {
			mMediaInfo = (MediaInfo) obj;
		}
	}
	
	private void initUI() {
		initTopStatus();
		initBtns();
		initListView();
	}
	
	private void initTopStatus() {
		mCountCommentTv = (TextView) findViewById(R.id.comment_review_count_comment);
		mCommentAvgScoreTv = (TextView) findViewById(R.id.comment_review_count_avg_score);
		mScoreStatusView = (ScoreStatusView) findViewById(R.id.comment_review_score_status);
	}
	
	private void initBtns() {
		mBtnBack = (Button) findViewById(R.id.comment_review_back);
		mBtnEdit = (Button) findViewById(R.id.comment_review_edit);
		mBtnAll = (Button) findViewById(R.id.comment_review_btn_all);
		mBtnPositive = (Button) findViewById(R.id.comment_review_btn_positive);
		mBtnNegative = (Button) findViewById(R.id.comment_review_btn_negative);
		mBtnBack.setOnClickListener(mOnClickListener);
		mBtnEdit.setOnClickListener(mOnClickListener);
		mBtnAll.setOnClickListener(mOnClickListener);
		mBtnPositive.setOnClickListener(mOnClickListener);
		mBtnNegative.setOnClickListener(mOnClickListener);
		mBtnAll.setSelected(true);
	}
	
	private void initListView() {
		mCommentReviewLoadingListView = (LoadingListView) findViewById(R.id.comment_review_list);
		mCommentReviewListView = mCommentReviewLoadingListView.getListView();
		
		mLoadMoreView = View.inflate(this, R.layout.load_more_view, null);
		mCommentReviewListView.setLoadMoreView(mLoadMoreView);
		mCommentReviewListView.setCanLoadMore(true);
		mCommentReviewListView.setOnLoadMoreListener(mOnLoadMoreListener);
		
		mCommentReviewAdapter = new CommentReviewAdapter(this);
		mCommentReviewListView.setAdapter(mCommentReviewAdapter);
		
		mCommentReviewLoadView = View.inflate(this, R.layout.load_view_black, null);
		mCommentReviewLoadingListView.setLoadingView(mCommentReviewLoadView);
		
		mCommentReviewEmptyView = View.inflate(this, R.layout.empty_view_black, null);
		TextView emptyHint = (TextView) mCommentReviewEmptyView.findViewById(R.id.empty_hint);
		emptyHint.setText(getResources().getString(R.string.comment_review_list_empty_hint));
		
		mCommentReviewRetryView = new RetryView(this);
		mCommentReviewRetryView.setOnRetryLoadListener(new OnRetryLoadListener() {
			@Override
			public void OnRetryLoad(View vClicked) {
				getReviewListDataUser();
			}
		});
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
				mCommentReviewLoadingListView.setShowLoading(true);
			}
			mReviewListSupply.getReviewListUser(mMediaInfo.mediaid, getCurPageNo(), 
					mPageSize, mReviewType);
		}
	}
	
	private void resetPageNo() {
		mPageNoAll = 1;
		mPageNoNegative = 1;
		mPageNoPositive = 1;
	}
	
	//packaged method
	private void refreshCommentReviewList(boolean isError) {
		mCommentReviewListView.setCanLoadMore(mCanLoadMore);
		
		mReviewLists = mReviewListSupply.getReviewList(mReviewType);
		mCommentReviewAdapter.setGroup(mReviewLists);
		if(mReviewLists != null && mReviewLists.size() > 0) {
			return;
		} 
		if(isError){
			mCommentReviewLoadingListView.setEmptyView(mCommentReviewRetryView);
		}else{
			mCommentReviewLoadingListView.setEmptyView(mCommentReviewEmptyView);
		}
	}
	
	private void refreshTopStatus() {
		String str = "";
		if(mCommentTotalCount < 10000) {
			str = getResources().getString(R.string.count_comment);
			str = String.format(str, mCommentTotalCount);
		} else {
			str = getResources().getString(R.string.count_comment_wan);
			float count = mCommentTotalCount / 10000f;
			str = String.format(str, count);
		}
		mCountCommentTv.setText(str);
		
		if(mCommentAvgScore == 0) {
			str = "0";
		} else if(mCommentAvgScore == 10) {
			str = "10";
		} else {
			str = getResources().getString(R.string.avg_score);
			str = String.format(str, mCommentAvgScore);
		}
		mCommentAvgScoreTv.setText(str);
		
		mScoreStatusView.setScorePercents(mScorePercent);
	}
	
	private void startCommentEditDialog() {
		Intent intent = new Intent();
		intent.setClass(this, CommentEditDialog.class);
		intent.putExtra(CommentEditDialog.KEY_MEDIA_INFO, mMediaInfo);
		this.startActivity(intent);
	}
	
	private void onCommentReviewBtnClicked() {
		if(mReviewType == ReviewTypeValueDef.REVIEW_TYPE_ALL) {
			mBtnAll.setSelected(true);
			mBtnPositive.setSelected(false);
			mBtnNegative.setSelected(false);
		} else if(mReviewType == ReviewTypeValueDef.REVIEW_TYPE_POSITIVE) {
			mBtnAll.setSelected(false);
			mBtnPositive.setSelected(true);
			mBtnNegative.setSelected(false);
		} else if(mReviewType == ReviewTypeValueDef.REVIEW_TYPE_NEGATIVE) {
			mBtnAll.setSelected(false);
			mBtnPositive.setSelected(false);
			mBtnNegative.setSelected(true);
		}
		mCanLoadMore = true;
		refreshCommentReviewList(false);
		if(getCurPageNo() == 1) {
			getReviewListDataUser();
		}
	}
	
	private int getCurPageNo() {
		if(mReviewType == ReviewTypeValueDef.REVIEW_TYPE_ALL) {
			return mPageNoAll;
		} else if(mReviewType == ReviewTypeValueDef.REVIEW_TYPE_POSITIVE) {
			return mPageNoPositive;
		} else if(mReviewType == ReviewTypeValueDef.REVIEW_TYPE_NEGATIVE) {
			return mPageNoNegative;
		}
		return mPageNoAll;
	}
	
	private void incCurPageNo() {
		if(mReviewType == ReviewTypeValueDef.REVIEW_TYPE_ALL) {
			mPageNoAll++;
		} else if(mReviewType == ReviewTypeValueDef.REVIEW_TYPE_POSITIVE) {
			mPageNoPositive++;
		} else if(mReviewType == ReviewTypeValueDef.REVIEW_TYPE_NEGATIVE) {
			mPageNoNegative++;
		}
	}

	//UI callback
	private OnClickListener mOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			int id = v.getId();
			switch (id) {
			case R.id.comment_review_back:
				try {
					CommentReviewDialog.this.finish();
				} catch (Exception e) {
					DKLog.e(TAG, e.getLocalizedMessage());
				}
				break;
			case R.id.comment_review_edit:
				startCommentEditDialog();
				break;
			case R.id.comment_review_btn_all:
				mReviewType = ReviewTypeValueDef.REVIEW_TYPE_ALL;
				onCommentReviewBtnClicked();
				break;
			case R.id.comment_review_btn_positive:
				mReviewType = ReviewTypeValueDef.REVIEW_TYPE_POSITIVE;
				onCommentReviewBtnClicked();
				break;
			case R.id.comment_review_btn_negative:
				mReviewType = ReviewTypeValueDef.REVIEW_TYPE_NEGATIVE;
				onCommentReviewBtnClicked();
				break;
				
			default:
				break;
			}
		}
	};
	
	private OnLoadMoreListener mOnLoadMoreListener = new OnLoadMoreListener() {
		
		@Override
		public void onLoadMore(ListView listView) {
			if(mCanLoadMore) {
				getReviewListDataUser();
			}
		}
	};
	
	//data callback
	private ReviewListListener mReviewListListener = new ReviewListListener() {
		
		@Override
		public void onReviewListDone(boolean isError, boolean canLoadMore) {
			mCommentAvgScore = mReviewListSupply.getAvgScore();
			mCommentTotalCount = mReviewListSupply.getTotalCount();
			mScorePercent = mReviewListSupply.getScorePercent();
			mCanLoadMore = canLoadMore;
			mCommentReviewLoadingListView.setShowLoading(false);
			refreshCommentReviewList(isError);
			refreshTopStatus();
			
			if(canLoadMore && !isError) {
				incCurPageNo();
			}
		}
	};
}
