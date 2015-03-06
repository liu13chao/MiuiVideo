package com.miui.video.live.popup;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.miui.video.DKApp;
import com.miui.video.R;
import com.miui.video.adapter.TvSelectTvAdapter;
import com.miui.video.live.TvChannelManager;
import com.miui.video.live.TvChannelManager.TelevisionInfoListener;
import com.miui.video.live.TvEpgManager;
import com.miui.video.live.TvEpgManager.TelevisionUpdateInterface;
import com.miui.video.type.TelevisionInfo;
import com.miui.video.widget.ListViewEx;
import com.miui.video.widget.ListViewEx.OnLoadMoreListener;
import com.miui.video.widget.LoadingListView;
import com.miui.video.widget.RetryView;
import com.miui.video.widget.RetryView.OnRetryLoadListener;

public class TvCtrlSelectTvPopup extends PopupWindow {
	
	private Context mContext;
	private View mContentView;
	
    private LoadingListView mLoadingListView;
    private ListViewEx mListView;
    private View mLoadMoreView;
    private View mLoadingView;
    private View mSelectTvEmptyView;
    private RetryView mRetryView;
    private TvSelectTvAdapter mSelectTvAdapter;
    
    private int mContentViewWidth;
    
    //received data
    private TelevisionInfo mCurTvInfo;
    
	//data from net
	private ArrayList<TelevisionInfo> mTelevisionInfos = new ArrayList<TelevisionInfo>();
	
	//data supply
	private TvChannelManager mTvChannelManager;
	private TvEpgManager mTvEpgManager;
	
	//request params
	private int mTvChannelPageNo = 1;
	
	//flags
	private boolean mTvChannelCanLoadMore = true;
	
	//listener
	private TvSelectedListener mTvSelectedListener;
    
    public TvCtrlSelectTvPopup(Context context) {
    	super(LayoutInflater.from(context).inflate(R.layout.tv_select_tv, null));
    	this.mContext = context;
        init();
    }
    
    public void show(View parent, TelevisionInfo curTvInfo) {
    	this.mCurTvInfo = curTvInfo;
    	this.showAtLocation(parent, Gravity.LEFT, 0, 0);

    	mTvEpgManager.addListener(mTvUpdateInterface);
    	getData();
    }
    
    public void setTvSelectedListener(TvSelectedListener listener) {
    	this.mTvSelectedListener = listener;
    }
    
    @Override
    public void dismiss() {
    	super.dismiss();
    	mTvEpgManager.removeListeners(mTvUpdateInterface);
    }
    
    //init
    private void init() {
    	this.setAnimationStyle(R.style.leftmenu_popup_anim_style);
    	initDimen();
    	initUI();
    	
    	initManager();
    }
    
    private void initDimen() {
    	mContentViewWidth = mContext.getResources().getDimensionPixelSize(R.dimen.tv_selecttv_width);
    }
    
    private void initManager() {
		mTvChannelManager = new TvChannelManager();
    	mTvEpgManager = DKApp.getSingleton(TvEpgManager.class);
		mTvChannelManager.addListener(mTelevisionInfoListener);
    }
    
    private void initUI() {
        initPopupWindow();
    	initListView();
    }
    
    private void initListView() {
    	mContentView = getContentView();
	    TextView topTitleName = (TextView) mContentView.findViewById(R.id.vp_popup_ctrl_top_title_name);
        topTitleName.setText(R.string.select_tvs);
    	
    	mLoadingListView = (LoadingListView) mContentView.findViewById(R.id.tv_select_tv_loading_list_view);
    	mListView = mLoadingListView.getListView();
		mLoadMoreView = View.inflate(mContext, R.layout.load_more_view, null);
    	mListView.setLoadMoreView(mLoadMoreView);
    	mListView.setCanLoadMore(true);
    	mListView.setOnLoadMoreListener(mOnLoadMoreListener);
    	
    	mListView.setSelector(R.drawable.vp_list_item_bg);
        mListView.setVerticalFadingEdgeEnabled(true);
        mListView.setFadingEdgeLength(mContext.getResources().getDimensionPixelSize(R.dimen.video_common_fade_edge_length));
        mListView.setDivider(mContext.getResources().getDrawable(R.drawable.vp_divider_bg_07));
    	mListView.setOnItemClickListener(mOnItemClickListener);
    	
		mSelectTvAdapter = new TvSelectTvAdapter(mContext);
		
		mLoadingView = View.inflate(mContext, R.layout.load_view, null);
		mLoadingListView.setLoadingView(mLoadingView);
		
		mSelectTvEmptyView = View.inflate(mContext, R.layout.empty_view, null);
		TextView emptyHintSelectTv = (TextView) mSelectTvEmptyView.findViewById(R.id.empty_title);
		emptyHintSelectTv.setText(mContext.getResources().getString(R.string.select_tv_empty_hint));
		
		mRetryView = new RetryView(mContext, RetryView.STYLE_LIGHT);
		mRetryView.setOnRetryLoadListener(new OnRetryLoadListener() {
			@Override
			public void OnRetryLoad(View vClicked) {
				getData();
			}
		});
    }
    
    private void initPopupWindow() {
    	this.setWidth(mContentViewWidth);
    	this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
    	this.setOutsideTouchable(true);
    	this.setFocusable(true);
    	this.setTouchable(true);
        this.setBackgroundDrawable(new ColorDrawable());
    }
    
	//get data
    private void getData() {
    	getTelevisionInfo();
    }
    
	private void getTelevisionInfo() {
		if(mTelevisionInfos.size() > 0 && !mTvChannelCanLoadMore) {
			refreshListView(false);
		} else {
			if(mTelevisionInfos.size() == 0) {
				mLoadingListView.setShowLoading(true);
			}
			mTvChannelManager.getTelevisionInfo(mTvChannelPageNo);
		}
	}
    
    //packaged method
    private void refreshListView(boolean isError) {
    	mListView.setCanLoadMore(mTvChannelCanLoadMore);
		mListView.setAdapter(mSelectTvAdapter);
		mSelectTvAdapter.setCurTvInfo(mCurTvInfo);
		mSelectTvAdapter.setGroup(mTelevisionInfos);
		if(mTelevisionInfos.size() > 0) {
			return;
		}
		
		if(isError){
			mLoadingListView.setEmptyView(mRetryView);
		} else{
			mLoadingListView.setEmptyView(mSelectTvEmptyView);
		}
    }
    
    private void mergeExpiredTvInfo() {
    	for(int i = 0; i < mTelevisionInfos.size(); i++) {
    		TelevisionInfo tvInfo = mTelevisionInfos.get(i);
    		int tvId = tvInfo.getChannelId();
    		TelevisionInfo expiredTvInfo = mTvEpgManager.getTelevisionInfo(tvId);;
    		if(expiredTvInfo != null) {
    			mTelevisionInfos.set(i, expiredTvInfo);
    		}
    	}
    }
    
    //UI callback    
	private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			dismiss();
			if(mTvSelectedListener != null) {
				TelevisionInfo curTvInfo = mTelevisionInfos.get(position);
				mCurTvInfo = curTvInfo;
				mTvSelectedListener.onTvSelected(mCurTvInfo);
			}
		}
	};
	
	private OnLoadMoreListener mOnLoadMoreListener = new OnLoadMoreListener() {
		
		@Override
		public void onLoadMore(ListView listView) {
			if(mTvChannelCanLoadMore) {
				getTelevisionInfo();
			}
		}
	};
    
    //data callback
	private TelevisionInfoListener mTelevisionInfoListener = new TelevisionInfoListener() {
		
		@Override
		public void onTelevisionInfosDone(
				ArrayList<TelevisionInfo> televisionInfos, boolean isError,
				boolean canLoadMore) {
			mLoadingListView.setShowLoading(false);
			mTelevisionInfos.clear();
			if(televisionInfos != null) {
				mTelevisionInfos.addAll(televisionInfos);
				mTvEpgManager.addTelevisionInfo(mTelevisionInfos);
			}
			mTvChannelCanLoadMore = canLoadMore;
			refreshListView(isError);
			
			if(canLoadMore && !isError) {
				mTvChannelPageNo++;
			}
		}
	};
	
	private TelevisionUpdateInterface mTvUpdateInterface = new TelevisionUpdateInterface() {
		
		@Override
		public void updateTelevision() {
			mergeExpiredTvInfo();
			refreshListView(false);
		}
	};
	
	//self def class
	public interface TvSelectedListener {
		public void onTvSelected(TelevisionInfo tvInfo);
	}
}
