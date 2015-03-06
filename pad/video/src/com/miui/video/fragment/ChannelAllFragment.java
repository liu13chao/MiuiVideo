package com.miui.video.fragment;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLayoutChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.miui.video.ChannelActivity;
import com.miui.video.R;
import com.miui.video.adapter.MediaViewListAdapter;
import com.miui.video.api.DKApi;
import com.miui.video.datasupply.ChannelFilterSupply;
import com.miui.video.datasupply.ChannelFilterSupply.ChannelFilterListener;
import com.miui.video.dialog.MediaDetailDialogFragment;
import com.miui.video.statistic.ChannelMediaInfoListTypeDef;
import com.miui.video.statistic.GetChannelMediaListStatisticInfo;
import com.miui.video.statistic.SourceTagValueDef;
import com.miui.video.type.Channel;
import com.miui.video.type.MediaInfo;
import com.miui.video.type.MediaInfoQuery;
import com.miui.video.util.DKLog;
import com.miui.video.widget.ListViewEx;
import com.miui.video.widget.ListViewEx.OnLoadMoreListener;
import com.miui.video.widget.LoadingListView;
import com.miui.video.widget.RetryView;
import com.miui.video.widget.RetryView.OnRetryLoadListener;
import com.miui.video.widget.filter.FilterViewFilter;
import com.miui.video.widget.filter.FilterViewFilter.ButtonTag;
import com.miui.video.widget.filter.FilterViewFilter.OnFilterViewSelectedListener;
import com.miui.video.widget.filter.FilterViewMenu;
import com.miui.video.widget.media.MediaView;
import com.miui.video.widget.media.MediaView.OnMediaClickListener;
/**
 *@author tangfuling
 *
 */

public class ChannelAllFragment extends Fragment {

	private static final String TAG = ChannelAllFragment.class.getName();
	
	private Context mContext;
	private View mContentView;
	
	//UI
	private LinearLayout mFilterViewWraper;
	private Button mFilterBtn;
	private FilterViewFilter mFilterViewFilter;
	private FilterViewMenu mFilterViewMenu;
	
	private LoadingListView mLoadingListView;
	private ListViewEx mListView;
	private View mLoadMoreView;
	private View mLoadingView;
	private View mEmptyView;
	private RetryView mRetryView;
	private MediaViewListAdapter mAdapter;
	
	//received data
	private Channel mChannel;
	
	//data from network
	private Object[] mFilterMedias;
	
	//data supply
	private ChannelFilterSupply mChannelFilterSupply;
	
	//request params
	private int mPageSize = 24;
	private int mPageNo = 1;
	private int mOrderBy = DKApi.ORDER_BY_ISSUEDATE;
	private int[] mChannelIds;
	
	//flags
	private boolean mIsFold = false;
	private boolean mCanLoadMore = true;
	private boolean mIsAnimationPlaying = false;
	private boolean mIsListViewEmpty = true;
	private boolean mIsRepositioned = false;
	
	private int mAnimationCount;
	private int ANIMATE_DURATION = 300;
	
	private int mFilterViewFilterHeight = 0;
	private int mListItemTopMargin = 0;
	
	private int mLastTouchY;
	private int mCurrentTouchY;
	
	//flags
	private boolean mIsDataInited = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getArguments();
		if(bundle != null) {
			Object obj = bundle.getSerializable(ChannelActivity.KEY_CHANNEL);
			if(obj instanceof Channel) {
				mChannel = (Channel) obj;
			}
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContentView = inflater.inflate(R.layout.channel_all, null);
		return mContentView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mContext = getActivity();
		init();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		mChannelFilterSupply.removeListener(mChannelFilterListener);
	}
	
	public void onSelected() {
		if(mChannelFilterSupply != null) {
			initData();
		}
	}
	
	//init
	private void init() {
		initUI();
		initRequestParams();
		initDataSupply();
	}
	
	private void initUI() {
		initFilterView();
		initListView();
	}
	
	private void initFilterView() {		
		mIsFold = true;
		mFilterBtn = (Button) mContentView.findViewById(R.id.channel_all_filter_btn);
		mFilterBtn.setOnClickListener(mOnClickListener);
		mFilterBtn.setSelected(false);
		
		mFilterViewFilter = new FilterViewFilter(mContext);
		mFilterViewFilter.setChannel(mChannel);
		mFilterViewFilter.setOnFilterViewSelectedListener(mOnFilterViewSelectedListener);
		mFilterViewFilter.addOnLayoutChangeListener(mOnLayoutChangeListener);
		mFilterViewFilter.setVisibility(View.INVISIBLE);
		
		mFilterViewMenu = (FilterViewMenu) mContentView.findViewById(R.id.channel_all_filter_menu);
		mFilterViewMenu.setAlpha(0);
		
		mFilterViewWraper = new LinearLayout(mContext);
		mFilterViewWraper.setOrientation(LinearLayout.VERTICAL);
		View paddingView = new View(mContext);
		int height = (int) getResources().getDimension(R.dimen.video_common_list_top_padding);
		LinearLayout.LayoutParams paddingViewParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height);
		mFilterViewWraper.addView(mFilterViewFilter);
		mFilterViewWraper.addView(paddingView, paddingViewParams);
	}
	
	private void initListView() {
		mLoadingListView = (LoadingListView) mContentView.findViewById(R.id.channel_all_list);
		mListView = mLoadingListView.getListView();
		mListView.setVerticalFadingEdgeEnabled(true);
		mListView.setFadingEdgeLength(getResources().getDimensionPixelSize(R.dimen.video_fade_edge_length));
		
		mListView.addHeaderView(mFilterViewWraper);
		mListView.setOnScrollListener(mOnScrollListener);
		mListView.setOnTouchListener(mOnTouchListener);
		mLoadMoreView = View.inflate(mContext, R.layout.load_more_view, null);
		mListView.setLoadMoreView(mLoadMoreView);
		mListView.setCanLoadMore(true);
		mListView.setOnLoadMoreListener(mOnLoadMoreListener);
		
		mAdapter = new MediaViewListAdapter(mContext);
		mAdapter.setOnMediaClickListener(mOnMediaClickListener);
		mListView.setAdapter(mAdapter);
		
		mLoadingView = View.inflate(mContext, R.layout.load_view, null);
		mLoadingListView.setLoadingView(mLoadingView);
		
		mEmptyView = View.inflate(mContext, R.layout.empty_view_media, null);
		TextView emptyHint = (TextView) mEmptyView.findViewById(R.id.empty_hint);
		emptyHint.setText(getResources().getString(R.string.channel_all_empty_hint));
		ImageView emptyIcon = (ImageView) mEmptyView.findViewById(R.id.empty_icon);
		emptyIcon.setBackgroundResource(R.drawable.empty_icon_media);
		
		mRetryView = new RetryView(mContext);
		mRetryView.setOnRetryLoadListener(new OnRetryLoadListener() {
			@Override
			public void OnRetryLoad(View vClicked) {
				getFilterMedia(prepareFilterMediaStatisticInfo());
			}
		});
	}
	
	private void initRequestParams() {
		mChannelIds = new int[1];
		if(mChannel != null) {
			mChannelIds[0] = mChannel.id;
		}
	}
	
	private void initDataSupply() {
		if(mChannelFilterSupply == null) {
			mChannelFilterSupply = new ChannelFilterSupply();
		}
		mChannelFilterSupply.addListener(mChannelFilterListener);
	}
	
	private void initData() {
		if(!mIsDataInited) {
			if(mFilterMedias == null || mFilterMedias.length == 0) {
				getFilterMedia(prepareFilterMediaStatisticInfo());
			} else {
				refreshListView(false);
			}
			mIsDataInited = true;
		}
	}
	
	//get data
	private void getFilterMedia(String statisticInfo) {
		if(mFilterMedias != null && mFilterMedias.length > 0 && !mCanLoadMore) {
			refreshListView(false);
		} else {
			if(mFilterMedias == null || mFilterMedias.length == 0) {
				mLoadingListView.setShowLoading(true);
			}
			MediaInfoQuery query = new MediaInfoQuery();
			query.orderBy = mOrderBy;
			query.pageNo = mPageNo;
			query.pageSize = mPageSize;
			query.ids = mChannelIds;
			query.statisticInfo = statisticInfo;
			mChannelFilterSupply.getFilterMedias(query);
		}
	}
	
	//packaged method
	private void onFilterBtnClick() {
		if(mIsAnimationPlaying) {
			return;
		}
		boolean isSelected = mFilterBtn.isSelected();
		isSelected = !isSelected;
		mFilterBtn.setSelected(isSelected);
		if(isSelected) {
			foldListView();
		} else {
			if(mIsFold && !mIsAnimationPlaying) {
				mListView.setSelection(0);
			}
			unfoldListView();
		}
	}
	
	private void refreshListView(boolean isError) {
		mListView.setCanLoadMore(mCanLoadMore);
		mAdapter.setGroup(mFilterMedias);
		if(mFilterMedias != null && mFilterMedias.length > 0) {
			mIsListViewEmpty = false;
			return;
		}
		mIsListViewEmpty = true;
		if(isError){
			mLoadingListView.setEmptyView(mRetryView);
		}else{
			mLoadingListView.setEmptyView(mEmptyView);
		}
	} 
	
	private void resetListView() {
		mFilterMedias = null;
		mPageNo = 1;
		mCanLoadMore = true;
		refreshListView(false);
	}
	
	private void rePositionListView() {
		if(!mIsRepositioned) {
			mLoadingListView.setTranslationY(-(mFilterViewFilterHeight + mListItemTopMargin));
			mFilterViewFilter.setVisibility(View.VISIBLE);
			mIsFold = true;
			mFilterBtn.setSelected(true);
			mIsRepositioned = true;
		}
	}
	
	private void resizeListView() {
		LayoutParams params = (LayoutParams) mLoadingListView.getLayoutParams();
		params.height = getListViewOriginHeight() + mFilterViewFilterHeight;
		mLoadingListView.setLayoutParams(params);
	}
	
	private int getListViewOriginHeight() {
		int listViewTopMargin = getResources().getDimensionPixelSize(R.dimen.channel_all_list_top_margin);
		int screenHeight = getResources().getDisplayMetrics().heightPixels;
		return screenHeight - listViewTopMargin;
	}
	
	private void foldListView() {
		DKLog.d(TAG, "fold list view");
		if(!mIsFold && !mIsAnimationPlaying) {
			animationFold();
		}
	}
	
	private void unfoldListView() {
		DKLog.d(TAG, "unfold list view");
		if(mIsFold && !mIsAnimationPlaying) {
			animationUnfold();
		}
	}
	
	private void animationFold() {
		DKLog.d(TAG, "animation fold");
		mIsFold = true;
		mFilterBtn.setSelected(true);
		
		Animator filterViewFilterAnim = ObjectAnimator.ofFloat(mLoadingListView, "translationY", 
        		0, -(mFilterViewFilterHeight + mListItemTopMargin));
		filterViewFilterAnim.setInterpolator(new DecelerateInterpolator());
		filterViewFilterAnim.setDuration(ANIMATE_DURATION);
		filterViewFilterAnim.addListener(mAnimatorListener);
		filterViewFilterAnim.start();
		
		Animator filterViewMenuAnim = ObjectAnimator.ofFloat(mFilterViewMenu, "alpha", 0f, 1f);
		filterViewMenuAnim.setDuration(ANIMATE_DURATION);
		filterViewMenuAnim.addListener(mAnimatorListener);
		filterViewMenuAnim.start();
	}
		
	private void animationUnfold() {
		DKLog.d(TAG, "animation unfold");
		mIsFold = false;
		mFilterBtn.setSelected(false);
		
		Animator filterViewFilterAnim = ObjectAnimator.ofFloat(mLoadingListView, "translationY", 
				-(mFilterViewFilterHeight + mListItemTopMargin), 0);
		filterViewFilterAnim.setInterpolator(new DecelerateInterpolator());
		filterViewFilterAnim.setDuration(ANIMATE_DURATION);
		filterViewFilterAnim.addListener(mAnimatorListener);
		filterViewFilterAnim.start();
		
		Animator filterViewMenuAnim = ObjectAnimator.ofFloat(mFilterViewMenu, "alpha", 1f, 0f);
		filterViewMenuAnim.setDuration(ANIMATE_DURATION);
		filterViewMenuAnim.addListener(mAnimatorListener);
		filterViewMenuAnim.start();
	}
	
	//UI callback
	private OnFilterViewSelectedListener mOnFilterViewSelectedListener = new OnFilterViewSelectedListener() {
		
		@Override
		public void onFilterViewSelected(int[] selectedChannelIds, ButtonTag tag) {
			if(selectedChannelIds != null) {
				resetListView();
				mChannelIds = selectedChannelIds;
				getFilterMedia(prepareFilterMediaStatisticInfo());
			}
			
			if(tag != null) {
				if(tag.parentChannelId != tag.channelId) {
					mFilterViewMenu.addMenuItem(tag);
				} else {
					mFilterViewMenu.removeMenuItem(tag);
				}
			}
		}
	};
	
	private OnLayoutChangeListener mOnLayoutChangeListener = new OnLayoutChangeListener() {
		
		@Override
		public void onLayoutChange(View v, int left, int top, int right,
				int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
			if(mFilterViewFilterHeight == 0) {
				mListItemTopMargin = mContext.getResources().getDimensionPixelSize(R.dimen.media_view_list_default_intervalV);
				mFilterViewFilterHeight = mFilterViewFilter.getHeight();
				resizeListView();
			}
		}
	};
	
	private OnClickListener mOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(v == mFilterBtn) {
				onFilterBtnClick();
			}
		}
	};
	
	private OnLoadMoreListener mOnLoadMoreListener = new OnLoadMoreListener() {
		
		@Override
		public void onLoadMore(ListView listView) {
			if(mCanLoadMore) {
				getFilterMedia(null);
			}
		}
	};
	
	private OnScrollListener mOnScrollListener = new OnScrollListener() {
		
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			if(scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
				View child0 = view.getChildAt(0);
				if(child0 != null && child0.getTop() == 0
						&& mCurrentTouchY > mLastTouchY) {
					unfoldListView();
				}
			}
		}
		
		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			View child0 = view.getChildAt(0);
			if(child0 != null) {
				if(firstVisibleItem == 0 && child0.getTop() == 0 
						&& mCurrentTouchY > mLastTouchY) {
					unfoldListView();
				}
			}
		}
	};
	
	private OnTouchListener mOnTouchListener = new OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if(event.getAction() == MotionEvent.ACTION_DOWN) {
				mLastTouchY = (int) event.getY();
			} else if(event.getAction() == MotionEvent.ACTION_MOVE) {
				mCurrentTouchY = (int) event.getY();
				if(mCurrentTouchY < mLastTouchY) {
					foldListView();
					mLastTouchY = mCurrentTouchY;
				} else if(mCurrentTouchY > mLastTouchY && mIsListViewEmpty) {
					unfoldListView();
					mLastTouchY = mCurrentTouchY;
				}
			}
			return false;
		}
	};
	
	private AnimatorListener mAnimatorListener = new AnimatorListener() {
		
		@Override
		public void onAnimationStart(Animator animation) {
			DKLog.d(TAG, "animation start");
			mAnimationCount++;
			mIsAnimationPlaying = true;
		}
		
		@Override
		public void onAnimationRepeat(Animator animation) {
			
		}
		
		@Override
		public void onAnimationEnd(Animator animation) {
			mAnimationCount--;
			if(mAnimationCount == 0) {
				mIsAnimationPlaying = false;
			}
		}
		
		@Override
		public void onAnimationCancel(Animator animation) {
			mAnimationCount--;
			if(mAnimationCount == 0) {
				mIsAnimationPlaying = false;
			}
		}
	};

	//data callback
	private ChannelFilterListener mChannelFilterListener = new ChannelFilterListener() {
		
		@Override
		public void onFilterMediasDone(Object[] filterMedias, boolean isError,
				boolean canLoadMore) {
			mCanLoadMore = canLoadMore;
			mLoadingListView.setShowLoading(false);
			mFilterMedias = filterMedias;
			refreshListView(isError);
			rePositionListView();
			if(canLoadMore) {
				mPageNo++;
			}
		}
	};

	//UI callback
	private OnMediaClickListener mOnMediaClickListener = new OnMediaClickListener() {
		
		@Override
		public void onMediaClick(MediaView mediaView, Object media) {
			if(media instanceof MediaInfo) {
				Intent intent = new Intent();
				intent.setClass(mContext, MediaDetailDialogFragment.class);
				intent.putExtra(MediaDetailDialogFragment.KEY_MEDIA_INFO, (MediaInfo)media);
				intent.putExtra(MediaDetailDialogFragment.KEY_SOURCE_PATH, SourceTagValueDef.PAD_CHANNEL_ALL_VALUE);
				mContext.startActivity(intent);
			}
		}
	};
	
	//statistic
	private String prepareFilterMediaStatisticInfo() {
		GetChannelMediaListStatisticInfo  getChannelMediaListStatisticInfo = new GetChannelMediaListStatisticInfo();
        getChannelMediaListStatisticInfo.categoryId = getCategoryId();
        getChannelMediaListStatisticInfo.listType = ChannelMediaInfoListTypeDef.LIST_FEATURE_TYPE_CODE;
        getChannelMediaListStatisticInfo.setFilter(getFilterTypes(), getFilterValues());
		return getChannelMediaListStatisticInfo.formatToJson();
	}
	
	private String getCategoryId() {
    	if(mChannel != null) {
    		StringBuilder categoryId = new StringBuilder();
            categoryId.append(mChannel.name);
            categoryId.append("(");
            categoryId.append(mChannel.id);
            categoryId.append(")");
            return categoryId.toString();
    	}
        return "";
    }
	
	private String[] getFilterTypes() {
		String[] filterTypes = null;
		if(mChannelIds == null || mChannel == null || mChannel.sub == null) {
			return filterTypes;
		}
		
		if(mChannelIds.length == 1) {
			filterTypes = new String[1];
			filterTypes[0] = mChannelIds[0] +"";
		} else {
			Channel[] subChannel = mChannel.sub;
			int filterSize = subChannel.length;
			filterTypes = new String[filterSize];
			for(int i = 0; i < filterSize; i++) {
				if(subChannel[i] == null) {
					break;
				}
	            StringBuilder filterType = new StringBuilder();
	            filterType.append(subChannel[i].name);
	            filterType.append("(");
	            filterType.append(subChannel[i].id);
	            filterType.append(")");
	            filterTypes[i] = filterType.toString();
	        }
		}
		return filterTypes;
	}
	
	private String[] getFilterValues() {
		String[] filterValues = null;
		if(mChannelIds == null) {
			return filterValues;
		}
		
		if(mChannelIds.length == 1) {
			filterValues = new String[1];
			filterValues[0] = mChannelIds[0] +"";
		} else {
			int filterSize = mChannelIds.length;
			filterValues = new String[filterSize];
			for(int i = 0; i < filterSize; i++) {
				filterValues[i] = mChannelIds[i] +"";
			}
		}
		return filterValues;
	}
}
