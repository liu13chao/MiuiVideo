package com.miui.video;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.miui.video.api.def.ReviewTypeValueDef;
import com.miui.video.base.BaseActivity;
import com.miui.video.base.BaseFragment;
import com.miui.video.fragment.CommentReviewAllFragment;
import com.miui.video.fragment.CommentReviewComFragment;
import com.miui.video.type.MediaInfo;
import com.miui.video.widget.pager.PagerView;
import com.miui.video.widget.pager.ViewFragmentPagerAdapter;

/**
 *@author tangfuling
 *
 */

public class CommentReviewActivity extends BaseActivity {
	
	public static final String KEY_MEDIA_INFO = "mediaInfo";
	public static final String KEY_REVIEW_TYPE = "reviewType";
	
	//UI
	private View mTitleTop;
	private TextView mTitleTopName;
	private TextView mWriteComment;
	
	private CommentReviewAllFragment mCommentReviewAllFragment;
	private CommentReviewComFragment mCommentReviewPositiveFragment;
	private CommentReviewComFragment mCommentReviewNegativeFragment;
	
	private PagerView mPagerView;
	private ViewFragmentPagerAdapter mViewPagerAdapter;
	private BaseFragment[] mPages;
	
	//received data
	private MediaInfo mMediaInfo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.comment_review);
		init();
	}
	
	//init
	private void init() {
	    initReceivedData();
	    initUI();
	}
	
	private void initReceivedData() {
		Intent intent = getIntent();
		Object obj = intent.getSerializableExtra(KEY_MEDIA_INFO);
		if(obj instanceof MediaInfo) {
			mMediaInfo = (MediaInfo) obj;
		}
	}
	
	private void initUI() {
		initTitleTop();
		initFragment();
		initPagerView();
	}
	
	private void initTitleTop() {
		mTitleTop = findViewById(R.id.title_top);
		mTitleTop.setOnClickListener(mOnClickListener);
		mTitleTopName = (TextView) mTitleTop.findViewById(R.id.title_top_name);
		mTitleTopName.setText(R.string.comment);
		mWriteComment = (TextView) findViewById(R.id.comment_review_write_comment);
		mWriteComment.setOnClickListener(mOnClickListener);
	}
	
	private void initFragment() {
		Bundle bundleAll = new Bundle();
		bundleAll.putSerializable(KEY_MEDIA_INFO, mMediaInfo);
		Bundle bundlePositive = new Bundle();
		bundlePositive.putSerializable(KEY_MEDIA_INFO, mMediaInfo);
		bundlePositive.putInt(KEY_REVIEW_TYPE, ReviewTypeValueDef.REVIEW_TYPE_POSITIVE);
		Bundle bundleNegative = new Bundle();
		bundleNegative.putSerializable(KEY_MEDIA_INFO, mMediaInfo);
		bundleNegative.putInt(KEY_REVIEW_TYPE, ReviewTypeValueDef.REVIEW_TYPE_NEGATIVE);
		
		mCommentReviewAllFragment = new CommentReviewAllFragment();
		mCommentReviewPositiveFragment = new CommentReviewComFragment();
		mCommentReviewNegativeFragment = new CommentReviewComFragment();
		mCommentReviewAllFragment.setArguments(bundleAll);
		mCommentReviewPositiveFragment.setArguments(bundlePositive);
		mCommentReviewNegativeFragment.setArguments(bundleNegative);
	}
	
	private void initPagerView() {
		mPagerView = (PagerView) findViewById(R.id.comment_review_pager_view);
		String[] titles = new String[3];
		titles[0] = getResources().getString(R.string.all);
		titles[1] = getResources().getString(R.string.positive_comment);
		titles[2] = getResources().getString(R.string.negative_comment);
		mPagerView.setTitle(titles);
		
		mViewPagerAdapter = new ViewFragmentPagerAdapter(getFragmentManager());
		mPages = new BaseFragment[3];
		mPages[0] = mCommentReviewAllFragment;
		mPages[1] = mCommentReviewPositiveFragment;
		mPages[2] = mCommentReviewNegativeFragment;
		
		mViewPagerAdapter.setPages(mPages);
		mPagerView.setViewPagerAdapter(mViewPagerAdapter);
	}
	
	private void startCommentEditActivity() {
		Intent intent = new Intent();
		intent.setClass(this, CommentEditActivity.class);
		intent.putExtra(CommentEditActivity.KEY_MEDIA_INFO, mMediaInfo);
		this.startActivity(intent);
	}
	
	//UI callback
	private OnClickListener mOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(v == mTitleTop) {
				CommentReviewActivity.this.finish();
			} else if(v == mWriteComment) {
				startCommentEditActivity();
			}
		}
	};
}
