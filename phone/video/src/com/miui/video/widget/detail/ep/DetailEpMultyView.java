package com.miui.video.widget.detail.ep;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.miui.video.R;
import com.miui.video.SelectEpActivity;
import com.miui.video.type.MediaDetailInfo2;
import com.miui.video.type.MediaSetInfo;
import com.miui.video.util.StringUtils;
import com.miui.video.widget.detail.ep.DetailEpView.OnEpClickListener;

public class DetailEpMultyView extends LinearLayout {

	private Context mContext;
//	private MediaDetailInfo mMediaInfo;
	private MediaSetInfo[] mSetList;
	private MediaDetailInfo2 mMediaDetailInfo2;
	@SuppressLint("UseSparseArrays")
	private HashMap<Integer, MediaSetInfo> mAvailableCiMap = new HashMap<Integer, MediaSetInfo>();
	private int mCurEp = 1;
	
	//UI
	private TextView mMultyAll;
	private TextView mMultyTitle;
	private GridView mMultyGridView;
	private DetailEpMultyAdapter mAdapter;
	
	//data
	private List<SetInfoStatusEp> mSetInfoStatusEps = new ArrayList<SetInfoStatusEp>();
	
	private List<OnEpClickListener> mListeners = new ArrayList<OnEpClickListener>();
	
	private int NUM_COLUMNS = 4;
	
	public DetailEpMultyView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
		init();
	}

	public DetailEpMultyView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		init();
	}

	public DetailEpMultyView(Context context) {
		super(context);
		this.mContext = context;
		init();
	}
	
	protected void addOnEpChangeListener(OnEpClickListener listener) {
		if(listener != null && !mListeners.contains(listener)) {
			mListeners.add(listener);
		}
	}
	
	protected void removeOnEpChangeListener(OnEpClickListener listener) {
		if(listener != null) {
			mListeners.remove(listener);
		}
	}
	
	protected void setCurEp(int curEp) {
		this.mCurEp = curEp;
		refresh();
	}
	
	protected void setData(MediaDetailInfo2 mediaDetailInfo2) {
		mAvailableCiMap.clear();
		mMediaDetailInfo2 = mediaDetailInfo2;
		if(mMediaDetailInfo2 != null) {
			if(mediaDetailInfo2.mediaciinfo != null) {
	             mSetList = mMediaDetailInfo2.mediaciinfo.videos;
				if(mSetList != null) {
					for(int i = 0; i < mSetList.length; i++) {
						MediaSetInfo mediaSetInfo = mSetList[i];
						if(mediaSetInfo != null) {
							mAvailableCiMap.put(mediaSetInfo.ci, mediaSetInfo);
						}
					}
				}
			}
		}
		refresh();
	}

	private void init() {
	}
	
	@Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mMultyAll = (TextView) findViewById(R.id.detail_ep_multy_more);
        mMultyTitle = (TextView) findViewById(R.id.detail_ep_multy_title);
        mMultyGridView = (GridView) findViewById(R.id.detail_ep_multy_gridview);
        mMultyAll.setOnClickListener(mOnClickListener);
        
        mAdapter = new DetailEpMultyAdapter(mContext);
        mMultyGridView.setSelector(R.drawable.transparent);
        mMultyGridView.setVerticalScrollBarEnabled(false);
        mMultyGridView.setNumColumns(NUM_COLUMNS);
        mMultyGridView.setVerticalSpacing(mContext.getResources().getDimensionPixelSize(
                R.dimen.size_30));
        mMultyGridView.setHorizontalSpacing(mContext.getResources().getDimensionPixelSize(
                R.dimen.size_30));
        mMultyGridView.setAdapter(mAdapter);
        mMultyGridView.setOnItemClickListener(mOnItemClickListener);
    }
	
	//packaged method
	private void buildSetInfoStatusEps() {
		mSetInfoStatusEps.clear();
        if(mSetList == null || mSetList.length == 0 ||mAvailableCiMap.size() == 0) {
            return;
        }
        int start = MediaSetInfo.indexOfCi(mSetList, mCurEp);
        start = Math.min(start, mSetList.length - NUM_COLUMNS * 2);
        start = Math.max(0, start);
		for(int i = start; i < start + NUM_COLUMNS * 2 && i < mSetList.length; i++) {
			SetInfoStatusEp setInfoStatusEp = new SetInfoStatusEp();
			MediaSetInfo mediaSetInfo = mSetList[i];
			if(mediaSetInfo == null) {
				setInfoStatusEp.isEnable = false;
			} else {
		         setInfoStatusEp.episode = mediaSetInfo.ci;
		         setInfoStatusEp.setInfo = mediaSetInfo;
				setInfoStatusEp.isEnable = true;
				if(setInfoStatusEp.episode == mCurEp) {
					setInfoStatusEp.isSelected = true;
				} else {
					setInfoStatusEp.isSelected = false;
				}
			}
			mSetInfoStatusEps.add(setInfoStatusEp);
		}
	}
	
	private void refreshTitle() {
		if(mMediaDetailInfo2 == null || mMediaDetailInfo2.mediainfo == null) {
			return;
		}
		int setNow = mMediaDetailInfo2.mediainfo.setnow;
		mMultyAll.setText(R.string.all_ep);
		String totalStr = mContext.getResources().getString(R.string.gong_count_ji);
		totalStr = StringUtils.formatString(totalStr, setNow);
		mMultyTitle.setText(totalStr);
		if(setNow <= NUM_COLUMNS * 2) {
			mMultyAll.setVisibility(View.GONE);
		} else {
			mMultyAll.setVisibility(View.VISIBLE);
		}
	}
	
	private void setCurEpLoading() {
		for(int i = 0; i < mSetInfoStatusEps.size(); i++) {
			SetInfoStatusEp setInfoStatusEp = mSetInfoStatusEps.get(i);
			if(setInfoStatusEp != null) {
				if(mCurEp == setInfoStatusEp.episode) {
//					setInfoStatusEp.isLoading = true;
					setInfoStatusEp.isSelected = true;
				} else {
//					setInfoStatusEp.isLoading = false;
					setInfoStatusEp.isSelected = false;
				}
			}
		}
		mAdapter.setGroup(mSetInfoStatusEps);
	}
	
	private void refresh() {
		refreshTitle();
		buildSetInfoStatusEps();
		mAdapter.setGroup(mSetInfoStatusEps);
	}
	
	private void notifyEpChanged(int curEp) {
		for(int i = 0; i < mListeners.size(); i++) {
			OnEpClickListener listener = mListeners.get(i);
			if(listener != null) {
				listener.onEpClick(curEp);
			}
		}
	}
	
	//UI callback
	private OnClickListener mOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(v == mMultyAll) {
				Intent intent = new Intent();
				intent.setClass(mContext, SelectEpActivity.class);
				intent.putExtra(SelectEpActivity.KEY_MEDIA_DETAIL_INFO2, mMediaDetailInfo2);
				mContext.startActivity(intent);
			}
		}
	};
	
	private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Object obj = parent.getItemAtPosition(position);
			if(obj instanceof SetInfoStatusEp) {
				SetInfoStatusEp setInfoStatusEp = (SetInfoStatusEp) obj;
				mCurEp = setInfoStatusEp.episode;
				setCurEpLoading();
				notifyEpChanged(mCurEp);
			}
		}
	};
}
