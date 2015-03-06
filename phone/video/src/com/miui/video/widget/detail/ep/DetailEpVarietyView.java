package com.miui.video.widget.detail.ep;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.miui.video.R;
import com.miui.video.SelectVarietyActivity;
import com.miui.video.type.MediaDetailInfo2;
import com.miui.video.type.MediaSetInfo;
import com.miui.video.widget.detail.ep.DetailEpView.OnEpClickListener;

public class DetailEpVarietyView extends LinearLayout {

	private Context mContext;
	
	private DetailEpVarietyWrapper mVarietyWrapper;
	private TextView mVarietyAll;
	
	private MediaDetailInfo2 mMediaDetailInfo2;
	
	@SuppressLint("UseSparseArrays")
	private HashMap<Integer, MediaSetInfo> mAvailableCiMap = new HashMap<Integer, MediaSetInfo>();
	
	private List<SetInfoStatusVariety> mSetInfoStatusVarietys = new ArrayList<SetInfoStatusVariety>();
	
	private List<OnEpClickListener> mListeners = new ArrayList<OnEpClickListener>();
	
	private int mCurEp = -1;
	
	private int mMaxItemCount = 4;
	
	MediaSetInfo[] mAvailableList;

	public DetailEpVarietyView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
	}

	public DetailEpVarietyView(Context context) {
		super(context);
		this.mContext = context;
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
	
	protected void setData(MediaDetailInfo2 mediaDetailInfo2) {
		this.mMediaDetailInfo2 = mediaDetailInfo2;
		if(mediaDetailInfo2 != null) {
			if(mediaDetailInfo2.mediaciinfo != null) {
				mAvailableList= mediaDetailInfo2.mediaciinfo.videos;
				if(mAvailableList != null && mAvailableList.length > 0) {
					for(int i = 0; i < mAvailableList.length; i++) {
						MediaSetInfo mediaSetInfo = mAvailableList[i];
						if(mediaSetInfo != null) {
							mAvailableCiMap.put(mediaSetInfo.ci, mediaSetInfo);
							if(mCurEp < 0 && i == mAvailableList.length - 1){
							    mCurEp = mediaSetInfo.ci;
							}
						}
					}
				}
			}
		}
		refresh();
	}
	
	protected void setCurEp(int curEp) {
	    if(curEp != mCurEp){
	        this.mCurEp = curEp;
	        refresh();
	    }
	}

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mVarietyWrapper = (DetailEpVarietyWrapper) findViewById(R.id.detail_ep_variety_wrapper);
        mVarietyWrapper.setOnClickListener(mOnClickListener);
        mVarietyAll = (TextView) findViewById(R.id.detail_ep_variety_more);
        mVarietyAll.setOnClickListener(mOnClickListener);
    }
	
    //packaged method
	private void buildSetInfoStatusVarieys() {
		mSetInfoStatusVarietys.clear();
		if(mAvailableList == null || mAvailableList.length == 0 ||mAvailableCiMap.size() == 0) {
			return;
		}
		int start = MediaSetInfo.indexOfCi(mAvailableList, mCurEp);
		if(start >= 0){
		    start = Math.min(start + mMaxItemCount, mAvailableList.length - 1);
		}else{
		    start = mAvailableList.length - 1;
		}
		for(int i = start; i >= 0 && i > start - mMaxItemCount; i--){
		    if(mAvailableList[i] == null){
		        continue;
		    }
		    int ci = mAvailableList[i].ci;
            MediaSetInfo mediaSetInfo = mAvailableCiMap.get(ci);
            if(mediaSetInfo != null) {
                SetInfoStatusVariety setInfoStatusVariety = new SetInfoStatusVariety();
                setInfoStatusVariety.setInfo = mediaSetInfo;
                setInfoStatusVariety.episode = ci;
                setInfoStatusVariety.isEnable = true;
                setInfoStatusVariety.date = mediaSetInfo.date;
                setInfoStatusVariety.videoName = mediaSetInfo.videoname;
                if(setInfoStatusVariety.episode == mCurEp) {
                    setInfoStatusVariety.isSelected = true;
                } else {
                    setInfoStatusVariety.isSelected = false;
                }
                mSetInfoStatusVarietys.add(setInfoStatusVariety);
            }
		}
	}
	
	private void refresh() {
		buildSetInfoStatusVarieys();
		mVarietyWrapper.setData(mSetInfoStatusVarietys);
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
			if(v == mVarietyAll) {
				Intent intent = new Intent();
				intent.setClass(mContext, SelectVarietyActivity.class);
				intent.putExtra(SelectVarietyActivity.KEY_MEDIA_DETAIL_INFO_2, mMediaDetailInfo2);
				mContext.startActivity(intent);
			} else if(v instanceof DetailEpItemVariety) {
				DetailEpItemVariety detailEpItemVariety = (DetailEpItemVariety) v;
				SetInfoStatusVariety setInfoStatusVariety = detailEpItemVariety.getData();
				if(setInfoStatusVariety != null) {
					notifyEpChanged(setInfoStatusVariety.episode);
				}
			}
		}
	};
}
