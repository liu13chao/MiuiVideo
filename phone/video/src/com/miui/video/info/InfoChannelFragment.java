package com.miui.video.info;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.miui.video.DKApp;
import com.miui.video.R;
import com.miui.video.adapter.InfoListAdapter;
import com.miui.video.base.BaseFragment;
import com.miui.video.datasupply.InformationListSupply.InformationDataListDetail;
import com.miui.video.info.InfoChannelDataManager.InfoDataChangeListener;
import com.miui.video.model.ChannelInfoStore;
import com.miui.video.type.Channel;
import com.miui.video.type.InformationData;
import com.miui.video.widget.ListViewEx;
import com.miui.video.widget.ListViewEx.OnLoadMoreListener;
import com.miui.video.widget.LoadingListView;
import com.miui.video.widget.RetryView;
import com.miui.video.widget.RetryView.OnRetryLoadListener;

public class InfoChannelFragment extends BaseFragment implements InfoDataChangeListener{

    public static final String TAG = "InfoChannelFragment";
	private Context mContext;
	private View mContentView;
	
	//UI
	private LoadingListView mLoadingListView;
	private ListViewEx mListView;
	private View mLoadMoreView;
	private View mLoadingView;
	private View mEmptyView;
	private RetryView mRetryView;
	private InfoListAdapter mAdapter;
	
//	//received data
	private Channel mChannel;
	private Channel mRootChannel;

	//	//data from network
	private InfoChannelDataManager mDataManager = null;
//	
//	//data supply
//	private InformationListSupply mInformationListSupply;
	
	//flags
	private boolean mForceInitData = false;
	private boolean mIsDataInited = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    Bundle bundle = getArguments();
	    if(bundle != null) {
	        Object obj = bundle.getSerializable(InfoConstants.KEY_CHANNEL);
	        if(obj instanceof Channel) {
	            mChannel = DKApp.getSingleton(ChannelInfoStore.class).getChannel((Channel) obj);
	        }
	        obj = bundle.getSerializable(InfoConstants.KEY_ROOT_CHANNEL);
            if(obj instanceof Channel) {
                mRootChannel = DKApp.getSingleton(ChannelInfoStore.class).getChannel((Channel) obj);
            }
	    }
	}
	
    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContentView = inflater.inflate(R.layout.info_channel_fragment, container, false);
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
		if(mDataManager != null){
			mDataManager.removeDataChangeListener(this);
		}
	}
	
	public void setForceInitData(boolean forceInitData) {
		this.mForceInitData = forceInitData;
	}
	
	@Override
	public void onSelected() {
		super.onSelected();
		Log.d(TAG, "onSelected " + mIsDataInited);
		if(!mIsDataInited){
		    mForceInitData = true;
		    initData();
		}
	}
	
	//init
	private void init() {
		initUI();
		initData();
	}
	
	private void initUI() {
		initListView();
	}
	
	private void initData(){
	    Log.d(TAG, "initData. ");
	    if(mChannel != null && mDataManager == null){
	          mDataManager = InfoChannelDataFactory.getInstance().getManager(mChannel.id);
	    }
	    if(mDataManager == null){
	        Log.d(TAG, "mDataManager is null. ");
	        return;
	    }
	    Log.d(TAG, "initData to load.");
	    mDataManager.addInfoDataChangeListener(this);
	    if(mForceInitData) {
	        loadData();
	    }
	}
	
	private void initListView() {
		mLoadingListView = (LoadingListView) mContentView.findViewById(R.id.info_channel_fragment_list);
		mListView = mLoadingListView.getListView();
		mListView.setOnItemClickListener(mOnItemClickListener);
		
		int top = (int) getResources().getDimension(R.dimen.info_fragment_margin);
		int bottom = (int) getResources().getDimension(R.dimen.size_20);
		mListView.setPadding(0,  top, 0, bottom);
		mListView.setClipToPadding(false);
		
		mLoadMoreView = View.inflate(mContext, R.layout.load_more_view, null);
		mListView.setLoadMoreView(mLoadMoreView);
		mListView.setCanLoadMore(true);
		mListView.setOnLoadMoreListener(mOnLoadMoreListener);
		
		mAdapter = new InfoListAdapter(mContext);
		mListView.setAdapter(mAdapter);
		
		mLoadingView = View.inflate(mContext, R.layout.load_view, null);
		mLoadingListView.setLoadingView(mLoadingView);
		
		mEmptyView = View.inflate(mContext, R.layout.empty_view_media, null);
		TextView emptyTitle = (TextView) mEmptyView.findViewById(R.id.empty_title);
		emptyTitle.setText(getResources().getString(R.string.error_empty_title));
		ImageView emptyIcon = (ImageView) mEmptyView.findViewById(R.id.empty_icon);
		emptyIcon.setBackgroundResource(R.drawable.empty_icon_error);
		
		mRetryView = new RetryView(mContext);
		mRetryView.setOnRetryLoadListener(new OnRetryLoadListener() {
			@Override
			public void OnRetryLoad(View vClicked) {
			    mIsDataInited = false;
			    loadData();
			}
		});
	}
	
//	private void initDataSupply() {
//		mInformationListSupply = new InformationListSupply();
//		mInformationListSupply.addListener(mInformationListListener);
//	}
	
	private void loadData() {
		if(!mIsDataInited && mDataManager != null) {
		    Log.d(TAG, "loadData.");
			mDataManager.getInformationList();
			mIsDataInited = true;
		}
	}
	
	//packaged method
	public void refreshListView(boolean isError) {
	    if(mDataManager == null){
	        return;
	    }
		mLoadingListView.setShowLoading(false);
		InformationData[] informations = null;
		InformationDataListDetail curInformationDataList = mDataManager.getCurInformationDataList();
		if(curInformationDataList != null) {
			mListView.setCanLoadMore(curInformationDataList.canLoadMore);
			mAdapter.setGroup(curInformationDataList.medialist);
			if(curInformationDataList.medialist.size() > 0) {
				return;
			}
		} else {
			mAdapter.setGroup(informations);
		}
		int emptyViewTopMargin = getResources().getDimensionPixelSize(R.dimen.video_common_empty_top_margin);
		if(isError){
			mLoadingListView.setEmptyView(mRetryView, emptyViewTopMargin);
		} else{
			mLoadingListView.setEmptyView(mEmptyView, emptyViewTopMargin);
		}
	}
	
	public void showLoading(){
		if(mLoadingListView != null){
			mLoadingListView.setShowLoading(true);
		}
	}
	
//	private InformationDataListDetail getCurInformationDataList() {
//		if(mInformationDataListMap != null) {
//			return mInformationDataListMap.get(mCurChannelId);
//		}
//		return null;
//	}
	
//	//data callback
//	private InformationDataListListener mInformationListListener = new InformationDataListListener() {
//		
//		@Override
//		public void onInformationDataListDone(
//				HashMap<Integer, InformationDataListDetail> informationDataListMap,
//				boolean isError) {
//			mInformationDataListMap = informationDataListMap;
//			refreshListView(isError);
//		}
//	};
	
	//UI callback
	private OnLoadMoreListener mOnLoadMoreListener = new OnLoadMoreListener() {
		@Override
		public void onLoadMore(ListView listView) {
		    if(mDataManager != null){
	            mDataManager.getInformationList();  
		    }
		}
	};
	
	private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
		
		public void onItemClick(android.widget.AdapterView<?> parent, View view, int position, long id) {
		    if(mChannel == null || mRootChannel == null){
		        return;
		    }
			if(position >= 0) {
			    InformationData obj = mAdapter.getItem(position);
	             InfoPlayUtil.playInformation(getActivity(), obj, null);
			}            
//				if(mRootChannel.getChannelType() == Channel.CHANNEL_TYPE_YY){
//					Object obj = mAdapter.getItem(position);
//					if(obj instanceof InformationData) {
//						InformationData informationData = (InformationData) obj;
//						InfoPlayUtil.playInformation(mContext, informationData, null);
//					}
//				}else{
//					Intent intent = new Intent();
//					intent.putExtra(InfoChannelPlayActivity.KEY_CHANNEL_ID, mChannel.id);
//					intent.putExtra(InfoChannelPlayActivity.KEY_ITEM_POSITION, position);
//					Object obj = mAdapter.getItem(position);
//					if(obj instanceof InformationData) {
//					    InformationData informationData = (InformationData) obj;
//					    intent.putExtra(InfoChannelPlayActivity.KEY_INFODATA, informationData);
//					    intent.setClass(getActivity(), InfoChannelPlayActivity.class);
//					    startActivity(intent);
//					}
//				}
//			}
		};
	};

	@Override
	public void setSelection(int position) {
	}
	
//	//statistic
//	private String getSourcePath() {
//		StringBuilder sb = new StringBuilder();
//		sb.append(SourceTagValueDef.PHONE_V6_CHANNEL);
//		if(mChannel != null) {
//			sb.append("_");
//			sb.append(mChannel.name);
//		}
//		return sb.toString();
//	}
}
