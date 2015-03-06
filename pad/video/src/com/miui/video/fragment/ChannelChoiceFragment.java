package com.miui.video.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;
import com.miui.video.ChannelActivity;
import com.miui.video.R;
import com.miui.video.adapter.MediaViewListAdapter;
import com.miui.video.datasupply.ChannelChoiceSupply;
import com.miui.video.datasupply.ChannelChoiceSupply.ChannelRecommendListener;
import com.miui.video.dialog.MediaDetailDialogFragment;
import com.miui.video.statistic.ChannelMediaInfoListTypeDef;
import com.miui.video.statistic.GetChannelMediaListStatisticInfo;
import com.miui.video.statistic.SourceTagValueDef;
import com.miui.video.type.Channel;
import com.miui.video.type.MediaInfo;
import com.miui.video.widget.ListViewEx;
import com.miui.video.widget.LoadingListView;
import com.miui.video.widget.RetryView;
import com.miui.video.widget.RetryView.OnRetryLoadListener;
import com.miui.video.widget.media.MediaView;
import com.miui.video.widget.media.MediaView.OnMediaClickListener;

/**
 *@author tangfuling
 *
 */

public class ChannelChoiceFragment extends Fragment {
	
	private Context mContext;
	private View mContentView;
	
	//UI
	private LoadingListView mLoadingListView;
	private ListViewEx mListView;
	private View mLoadingView;
	private View mEmptyView;
	private RetryView mRetryView;
	private MediaViewListAdapter mAdapter;
	
	//received data
	private Channel mChannel;
	
	//data from network
	private Object[] mChoiceMedias;
	
	//data supply
	private ChannelChoiceSupply mChannelRecommendSupply;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getArguments();
		if(bundle != null) {
			Object obj = bundle.get(ChannelActivity.KEY_CHANNEL);
			if(obj instanceof Channel) {
				mChannel = (Channel) obj;
			}
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContentView = inflater.inflate(R.layout.channel_choice, null);
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
		mChannelRecommendSupply.removeListener(mChannelRecommendListener);
	}
	
	//init
	private void init() {
		initUI();
		initDataSupply();
		initData();
	}
	
	private void initUI() {
		initListView();
	}
	
	private void initListView() {
		mLoadingListView = (LoadingListView) mContentView.findViewById(R.id.channel_choice_list);
		mListView = mLoadingListView.getListView();
		mListView.setVerticalFadingEdgeEnabled(true);
		mListView.setFadingEdgeLength(getResources().getDimensionPixelSize(R.dimen.video_fade_edge_length));
		
		View headView = new View(mContext);
		int height = (int) getResources().getDimension(R.dimen.video_common_list_top_padding);
		AbsListView.LayoutParams headViewParams = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, height);
		headView.setLayoutParams(headViewParams);
		mListView.addHeaderView(headView);
		
		mAdapter = new MediaViewListAdapter(mContext);
		mAdapter.setOnMediaClickListener(mOnMediaClickListener);
		mListView.setAdapter(mAdapter);
		
		mLoadingView = View.inflate(mContext, R.layout.load_view, null);
		mLoadingListView.setLoadingView(mLoadingView);
		
		mEmptyView = View.inflate(mContext, R.layout.empty_view_media, null);
		TextView emptyHint = (TextView) mEmptyView.findViewById(R.id.empty_hint);
		emptyHint.setText(getResources().getString(R.string.channel_choice_empty_hint));
		ImageView emptyIcon = (ImageView) mEmptyView.findViewById(R.id.empty_icon);
		emptyIcon.setBackgroundResource(R.drawable.empty_icon_media);
		
		mRetryView = new RetryView(mContext);
		mRetryView.setOnRetryLoadListener(new OnRetryLoadListener() {
			@Override
			public void OnRetryLoad(View vClicked) {
				getChoiceMedia();
			}
		});
	}
	
	private void initDataSupply() {
		if(mChannelRecommendSupply == null) {
			mChannelRecommendSupply = new ChannelChoiceSupply();
		}
		mChannelRecommendSupply.addListener(mChannelRecommendListener);
	}
	
	private void initData() {
		getChoiceMedia();
	}
	
	//get data
	private void getChoiceMedia() {
		if(mChannel != null && (mChoiceMedias == null || mChoiceMedias.length == 0)) {
			mLoadingListView.setShowLoading(true);
			mChannelRecommendSupply.getChoiceMedias(mChannel.id, prepareChoiceStatisticInfo());
		} else {
			refreshListView(false);
		}
	}
	
	//packaged method
	private void refreshListView(boolean isError) {
		mAdapter.setGroup(mChoiceMedias);
		
		if(mChoiceMedias != null && mChoiceMedias.length > 0) {
			return;
		}
		if(isError){
			mLoadingListView.setEmptyView(mRetryView);
		}else{
			mLoadingListView.setEmptyView(mEmptyView);
		}
	}
	
	//data callback
	private ChannelRecommendListener mChannelRecommendListener = new ChannelRecommendListener() {
		
		@Override
		public void onChoiceMediasDone(Object[] recommendations, boolean isError) {
			mLoadingListView.setShowLoading(false);
			mChoiceMedias = recommendations;
			refreshListView(isError);
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
				intent.putExtra(MediaDetailDialogFragment.KEY_SOURCE_PATH, SourceTagValueDef.PAD_CHANNEL_CHOICE_VALUE);
				mContext.startActivity(intent);
			}
		}
	};
	
	//statistic
	private String prepareChoiceStatisticInfo() {
		 GetChannelMediaListStatisticInfo  getChannelMediaListStatisticInfo = new GetChannelMediaListStatisticInfo();
         getChannelMediaListStatisticInfo.categoryId = getCategoryId();
         getChannelMediaListStatisticInfo.listType = ChannelMediaInfoListTypeDef.LIST_HOT_TYPE_CODE;
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
}
