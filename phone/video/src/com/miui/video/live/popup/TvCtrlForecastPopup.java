package com.miui.video.live.popup;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.miui.video.DKApp;
import com.miui.video.R;
import com.miui.video.adapter.TvForecastAdapter;
import com.miui.video.live.TvEpgManager;
import com.miui.video.live.TvEpgManager.TelevisionUpdateInterface;
import com.miui.video.type.TelevisionInfo;
import com.miui.video.type.TelevisionShow;
import com.miui.video.widget.ListViewEx;
import com.miui.video.widget.LoadingListView;
import com.miui.video.widget.RetryView;
import com.miui.video.widget.RetryView.OnRetryLoadListener;

public class TvCtrlForecastPopup extends PopupWindow {
	
	private Context mContext;
	private View mContentView;
	
    private LoadingListView mLoadingListView;
    private ListViewEx mListView;
    private View mLoadingView;
    private View mForecastEmptyView;
    private RetryView mRetryView;
    private TvForecastAdapter mForecastAdapter;
    
    private int mContentViewWidth;
    
    //received data
    private TelevisionInfo mCurTvInfo;
	
	//data supply
	private TvEpgManager mTvEpgManager;
    
    public TvCtrlForecastPopup(Context context) {
    	super(LayoutInflater.from(context).inflate(R.layout.tv_forecast, null));
    	this.mContext = context;
        init();
    }
    
    public void show(View parent, TelevisionInfo curTvInfo) {
    	this.mCurTvInfo = curTvInfo;
    	this.showAtLocation(parent, Gravity.LEFT, 0, 0);

    	mTvEpgManager.addListener(mTvUpdateInterface);
    	getData();
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
    	mContentViewWidth = mContext.getResources().getDimensionPixelSize(R.dimen.tv_forecast_width);
    }
    
    private void initManager() {
    	mTvEpgManager = DKApp.getSingleton(TvEpgManager.class);
    }
    
    private void initUI() {
        initPopupWindow();
    	initListView();
    }
    
    private void initListView() {
    	mContentView = getContentView();
	    TextView topTitleName = (TextView) mContentView.findViewById(R.id.vp_popup_ctrl_top_title_name);
        topTitleName.setText(R.string.forecasts);
        
    	mLoadingListView = (LoadingListView) mContentView.findViewById(R.id.tv_forecast_loading_list_view);
    	mListView = mLoadingListView.getListView();
    	mListView.setSelector(R.drawable.vp_list_item_bg);
        mListView.setVerticalFadingEdgeEnabled(true);
        mListView.setFadingEdgeLength(mContext.getResources().getDimensionPixelSize(R.dimen.video_common_fade_edge_length));
        mListView.setDivider(mContext.getResources().getDrawable(R.drawable.vp_divider_bg_07));
    	mListView.setOnItemClickListener(mOnItemClickListener);
    	
		mForecastAdapter = new TvForecastAdapter(mContext);
		
		mLoadingView = View.inflate(mContext, R.layout.load_view, null);
		mLoadingListView.setLoadingView(mLoadingView);
		
		mForecastEmptyView = View.inflate(mContext, R.layout.empty_view, null);
		TextView emptyHintForecast = (TextView) mForecastEmptyView.findViewById(R.id.empty_title);
		emptyHintForecast.setText(mContext.getResources().getString(R.string.forecast_empty_hint));
		
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
    	getTvEpgInfo();
    }
	
	private void getTvEpgInfo() {
		if(mCurTvInfo == null) {
			refreshListView(false);
			return;
		}
		List<TelevisionShow> televisionShows = mCurTvInfo.getTelevisionShowArray();
		if(televisionShows != null && televisionShows.size() > 0) {
			refreshListView(false);
		} else {
			mLoadingListView.setShowLoading(true);
			mTvEpgManager.addTelevisionInfo(mCurTvInfo);
		}
	}
    
    //packaged method
    private void refreshListView(boolean isError) {
		mListView.setAdapter(mForecastAdapter);
		if(mCurTvInfo != null) {
			List<TelevisionShow> televisionShows = mCurTvInfo.getTelevisionShowArray();
			if(televisionShows != null && televisionShows.size() > 0) {
				mForecastAdapter.setGroup(televisionShows);
				return;
			}
		}
		
		if(isError){
			mLoadingListView.setEmptyView(mRetryView);
		} else{
			mLoadingListView.setEmptyView(mForecastEmptyView);
		}
    }
    
    private void mergeExpiredTvInfo() {
    	if(mCurTvInfo == null) {
    		return;
    	}
		int tvId = mCurTvInfo.getChannelId();
		TelevisionInfo expiredTvInfo = mTvEpgManager.getTelevisionInfo(tvId);;
		if(expiredTvInfo != null) {
			mCurTvInfo = expiredTvInfo;
		}
    }
    
    //UI callback    
	private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			dismiss();
		}
	};
    
    //data callback
	private TelevisionUpdateInterface mTvUpdateInterface = new TelevisionUpdateInterface() {
		
		@Override
		public void updateTelevision() {
			mergeExpiredTvInfo();
			mLoadingListView.setShowLoading(false);
			refreshListView(false);
		}
	};
	
	//self def class
	public interface TvSelectedListener {
		public void onTvSelected(TelevisionInfo tvInfo);
	}
}
