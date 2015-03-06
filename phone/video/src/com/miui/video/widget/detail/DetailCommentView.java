package com.miui.video.widget.detail;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.miui.video.CommentEditActivity;
import com.miui.video.CommentReviewActivity;
import com.miui.video.R;
import com.miui.video.api.def.ReviewTypeValueDef;
import com.miui.video.datasupply.ReviewListSupply;
import com.miui.video.datasupply.ReviewListSupply.ReviewListListener;
import com.miui.video.type.MediaInfo;
import com.miui.video.type.MediaReview;

public class DetailCommentView extends FrameLayout {

	private Context mContext;
	private View mContentView;
	private TextView mCommentReviewBtn;
	private TextView mCommentWriteBtn;
	private DetailCommentReviewView mCommentReviewView;
	
	private View mEmptyView;
	private TextView mEmptyWriteBtn;
	
	//data supply
	private ReviewListSupply mReviewListSupply;
	
	//received data
	private MediaInfo mMediaInfo;
	private int mReviewType = ReviewTypeValueDef.REVIEW_TYPE_ALL;
	private int mPageNo = 1;
	private int mPageSize = 3;
	
	//data from net
	private int mTotalCommentCount;
	
	public DetailCommentView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
		init();
	}

	public DetailCommentView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		init();
	}

	public DetailCommentView(Context context) {
		super(context);
		this.mContext = context;
		init();
	}
	
	public void setMediaInfo(MediaInfo mediaInfo) {
	    if(mediaInfo != null){
	        if(mMediaInfo == null || mMediaInfo.mediaid != mediaInfo.mediaid){
	            mReviewListSupply.getReviewListUser(mediaInfo.mediaid, mPageNo, 
	                    mPageSize, mReviewType);
	        }
	        mMediaInfo = mediaInfo;
	    }
	}
	
	//init
	private void init() {
		initDataSupply();
		initUI();
	}
	
	private void initUI() {
		mContentView = View.inflate(mContext, R.layout.detail_comment, null);
		addView(mContentView);
		mCommentWriteBtn = (TextView) mContentView.findViewById(R.id.detail_comment_write);
		mCommentWriteBtn.setOnClickListener(mOnClickListener);
		mCommentReviewBtn = (TextView) mContentView.findViewById(R.id.detail_comment_btn);
		mCommentReviewBtn.setOnClickListener(mOnClickListener);
		mCommentReviewView = (DetailCommentReviewView) findViewById(R.id.detail_comment_review);
		
		mEmptyView = View.inflate(mContext, R.layout.detail_comment_empty, null);
		mEmptyWriteBtn = (TextView) mEmptyView.findViewById(R.id.detail_comment_empty_write);
		mEmptyWriteBtn.setOnClickListener(mOnClickListener);
		addView(mEmptyView);
		
		refresh();
	}
	
	private void initDataSupply() {
		mReviewListSupply = new ReviewListSupply();
		mReviewListSupply.addListener(mReviewListListener);
	}
	
	//packaged method
	private void refresh() {
		List<MediaReview> reviews = mReviewListSupply.getReviewList(mReviewType);
		mCommentReviewView.setMediaReviews(reviews);
		String str = mContext.getResources().getString(R.string.see_all_count_comment);
		str = String.format(str, mTotalCommentCount);
		mCommentReviewBtn.setText(str);
		if(mTotalCommentCount == 0) {
			mContentView.setVisibility(View.GONE);
			mEmptyView.setVisibility(View.VISIBLE);
		} else {
			mContentView.setVisibility(View.VISIBLE);
			mEmptyView.setVisibility(View.GONE);
		}
	}
	
	private void startCommentEditActivity() {
		Intent intent = new Intent();
		intent.setClass(mContext, CommentEditActivity.class);
		intent.putExtra(CommentEditActivity.KEY_MEDIA_INFO, mMediaInfo);
		mContext.startActivity(intent);
	}
	
	private void startCommentReviewActivity() {
		Intent intent = new Intent();
		intent.setClass(mContext, CommentReviewActivity.class);
		intent.putExtra(CommentReviewActivity.KEY_MEDIA_INFO, mMediaInfo);
		mContext.startActivity(intent);
	}
	
	//UI callback
	private OnClickListener mOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(v == mCommentWriteBtn || v == mEmptyWriteBtn) {
				startCommentEditActivity();
			} else if(v == mCommentReviewBtn){
				startCommentReviewActivity();
			}
		}
	};
	
	//data callback
	private ReviewListListener mReviewListListener = new ReviewListListener() {
		
		@Override
		public void onReviewListDone(boolean isError, boolean canLoadMore) {
			mTotalCommentCount = mReviewListSupply.getTotalCount();
			refresh();
		}
	};
}
