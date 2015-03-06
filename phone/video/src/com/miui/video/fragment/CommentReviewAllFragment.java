package com.miui.video.fragment;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.miui.video.CommentReviewActivity;
import com.miui.video.R;
import com.miui.video.adapter.CommentReviewListAllAdapter;
import com.miui.video.api.def.ReviewTypeValueDef;
import com.miui.video.base.BaseFragment;
import com.miui.video.datasupply.ReviewListSupply;
import com.miui.video.datasupply.ReviewListSupply.ReviewListListener;
import com.miui.video.type.MediaInfo;
import com.miui.video.type.MediaReview;
import com.miui.video.widget.ListViewEx;
import com.miui.video.widget.ListViewEx.OnLoadMoreListener;
import com.miui.video.widget.LoadingListView;
import com.miui.video.widget.RetryView;
import com.miui.video.widget.RetryView.OnRetryLoadListener;
import com.miui.video.widget.ScoreStatusView;

public class CommentReviewAllFragment extends BaseFragment {

	private Context mContext;
	private View mContentView;
	
	//UI
	private LoadingListView mLoadingListView;
	private ListViewEx mListView;
	private View mLoadMoreView;
	private View mLoadingView;
	private View mEmptyView;
	private RetryView mRetryView;
	private CommentReviewListAllAdapter mCommentReviewAdapter;
	
	private View mHeaderView;
	private TextView mCountCommentTv;
	private TextView mCommentAvgScoreTv;
	private ScoreStatusView mScoreStatusView;
	
	//data supply
	private ReviewListSupply mReviewListSupply;
	
	//received data
	private MediaInfo mMediaInfo;
	
	//data from network
	private float mCommentAvgScore;
	private int mCommentTotalCount;
	private ArrayList<MediaReview> mReviewLists;
	private float[] mScorePercent;
	
	private int mReviewType = ReviewTypeValueDef.REVIEW_TYPE_ALL;
	private int mPageSize = 10;
	private int mPageNo = 1;
	
	//flags
	private boolean mCanLoadMore = true;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getArguments();
		if(bundle != null) {
			Object obj = bundle.get(CommentReviewActivity.KEY_MEDIA_INFO);
			if(obj instanceof MediaInfo) {
				mMediaInfo = (MediaInfo) obj;
			}
		}
	}
	
	@SuppressLint("InflateParams")
    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContentView = inflater.inflate(R.layout.comment_review_fragment, null);
		return mContentView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mContext = getActivity();
		init();
	}
	
	@Override
	public void onSelected() {
		super.onSelected();
	}
	
	//init
	private void init() {
		initUI();
		initDataSupply();
		getReviewListDataUser();
	}
	
	private void initUI() {
		initHeaderView();
		initListView();
	}
	
	private void initHeaderView() {
		mHeaderView = View.inflate(mContext, R.layout.comment_review_header, null);
		mHeaderView.setBackgroundResource(R.drawable.com_item_bg_up_n);
		mCountCommentTv = (TextView) mHeaderView.findViewById(R.id.comment_review_avg_count);
		mCommentAvgScoreTv = (TextView) mHeaderView.findViewById(R.id.comment_review_avg_score);
		mScoreStatusView = (ScoreStatusView) mHeaderView.findViewById(R.id.comment_review_score_status);
		refreshHeadView();
	}
	
	private void initListView() {
		mLoadingListView = (LoadingListView) mContentView.findViewById(R.id.comment_review_fragment_list);
		mListView = mLoadingListView.getListView();
		mListView.setSelector(R.drawable.transparent);
		
		int height = getResources().getDimensionPixelSize(R.dimen.video_common_list_top_padding);
		mListView.setPadding(0, height, 0, 0);
		mListView.setClipToPadding(false);
		
		mListView.addHeaderView(mHeaderView);
		
		mCommentReviewAdapter = new CommentReviewListAllAdapter(mContext);
		mListView.setAdapter(mCommentReviewAdapter);
		
		mLoadMoreView = View.inflate(mContext, R.layout.load_more_view, null);
		mListView.setLoadMoreView(mLoadMoreView);
		mListView.setCanLoadMore(true);
		mListView.setOnLoadMoreListener(mOnLoadMoreListener);
		
		mLoadingView = View.inflate(mContext, R.layout.load_view, null);
		mLoadingListView.setLoadingView(mLoadingView);
		
		mEmptyView = View.inflate(mContext, R.layout.empty_view_media, null);
		TextView emptyTitle = (TextView) mEmptyView.findViewById(R.id.empty_title);
		emptyTitle.setText(getResources().getString(R.string.detail_comment_empty));
		ImageView emptyIcon = (ImageView) mEmptyView.findViewById(R.id.empty_icon);
		emptyIcon.setBackgroundResource(R.drawable.empty_icon_comment);
		
		mRetryView = new RetryView(mContext);
		mRetryView.setOnRetryLoadListener(new OnRetryLoadListener() {
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
				mLoadingListView.setShowLoading(true);
			}
			mReviewListSupply.getReviewListUser(mMediaInfo.mediaid, mPageNo, 
					mPageSize, mReviewType);
		}
	}
	
	//packaged method
	private void refreshCommentReviewList(boolean isError) {
	    if(!isAdded()){
	        return;
	    }
		mListView.setCanLoadMore(mCanLoadMore);
		
		mReviewLists = mReviewListSupply.getReviewList(mReviewType);
		mCommentReviewAdapter.setGroup(mReviewLists);
		if(mReviewLists != null && mReviewLists.size() > 0) {
			return;
		} 
		if(isError){
			mLoadingListView.setEmptyView(mRetryView);
		}else{
			mLoadingListView.setEmptyView(mEmptyView);
		}
	}
	
	private void refreshHeadView() {
	    if(!isAdded()){
            return;
        }
		if(mReviewLists == null || mReviewLists.size() == 0) {
			mHeaderView.setVisibility(View.INVISIBLE);
		} else {
			mHeaderView.setVisibility(View.VISIBLE);
		}
		
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
	
	//UI callback
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
			mLoadingListView.setShowLoading(false);
			refreshCommentReviewList(isError);
			refreshHeadView();
			
			if(canLoadMore && !isError) {
				mPageNo++;
			}
		}
	};
}
