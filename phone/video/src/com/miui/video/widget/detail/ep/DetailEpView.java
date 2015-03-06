package com.miui.video.widget.detail.ep;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.miui.video.R;
import com.miui.video.api.def.MediaConstantsDef;
import com.miui.video.local.OnlinePlayHistory;
import com.miui.video.local.PlayHistory;
import com.miui.video.statistic.SourceTagValueDef;
import com.miui.video.type.MediaDetailInfo2;
import com.miui.video.type.MediaInfo;
import com.miui.video.type.MediaSetInfo;
import com.miui.video.type.MediaSetInfoList;

public class DetailEpView extends FrameLayout {

    private final int UI_STYLE_SINGLE = 0;
    private final int UI_STYLE_MULTY = 1;
    private final int UI_STYLE_VARIETY = 2;

    //received data
    private MediaInfo mMediaInfo;
    private MediaDetailInfo2 mMediaDetailInfo2;
    private MediaSetInfoList mMediaSetInfoList;
    private PlayHistory mPlayHistory;

    private Context mContext;
    private View mContentView;

    private int mCurCi = -1;

    //manager
    private DetailEpPlayManager mDetailEpPlayManager;

    public DetailEpView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        init();
    }

    public DetailEpView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    public DetailEpView(Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public void setData(MediaInfo mediaInfo, MediaDetailInfo2 mediaDetailInfo2) {
        this.mMediaInfo = mediaInfo;
        this.mMediaDetailInfo2 = mediaDetailInfo2;
        if(mMediaDetailInfo2 != null) {
            mMediaSetInfoList = mMediaDetailInfo2.mediaciinfo;
        }
        refresh();
    }

    public void setPlayHistory(PlayHistory playHistory) {
        this.mPlayHistory = playHistory;
        refresh();
    }

    public int getCurCi() {
        return mCurCi;
    }

    public void playCurCi() {
        playMedia(mCurCi);
    }

    //init
    private void init() {
        mDetailEpPlayManager = new DetailEpPlayManager(mContext);
    }

    //packaged method
    private void playMedia(int ci) {
        if(mMediaInfo == null || mMediaSetInfoList == null) {
            return;
        }
        mCurCi = ci;
        int pos = MediaSetInfo.indexOfCi(mMediaSetInfoList.videos, ci);
        if(pos >= 0){
            MediaSetInfo set = mMediaSetInfoList.videos[pos];
                    mDetailEpPlayManager.playMedia(mMediaInfo, mMediaSetInfoList.style, 
                            set, SourceTagValueDef.PHONE_V6_DETAIL_VALUE);
        }
    }

    private void refresh() {
        refreshCi();

        int uiStyle = getUiStyle();
        switch (uiStyle) {
        case UI_STYLE_SINGLE:
			DetailEpSingleView detailEpSingleView = new DetailEpSingleView(mContext);
			if(mMediaDetailInfo2 != null) {
				detailEpSingleView.setData(mMediaDetailInfo2.mediainfo);
			}
			mContentView = detailEpSingleView;
            break;
        case UI_STYLE_MULTY:
            DetailEpMultyView detailEpMultyView = (DetailEpMultyView)LayoutInflater.from(mContext).
            inflate(R.layout.detail_ep_multy, this, false);
            detailEpMultyView.addOnEpChangeListener(mOnEpClickListener);
            if(mMediaDetailInfo2 != null) {
                detailEpMultyView.setCurEp(mCurCi);
                detailEpMultyView.setData(mMediaDetailInfo2);
            }
            mContentView = detailEpMultyView;
            break;
        case UI_STYLE_VARIETY:
            DetailEpVarietyView detailEpVarietyView = (DetailEpVarietyView)LayoutInflater.from(mContext).
            inflate(R.layout.detail_ep_variety, this, false);
            detailEpVarietyView.addOnEpChangeListener(mOnEpClickListener);
            if(mMediaDetailInfo2 != null) {
                detailEpVarietyView.setCurEp(mCurCi);
                detailEpVarietyView.setData(mMediaDetailInfo2);
            }
            mContentView = detailEpVarietyView;
            break;

        default:
            mContentView = null;
            break;
        }

        removeAllViews();
        if(mContentView != null) {
            LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            addView(mContentView, params);
            this.setVisibility(View.VISIBLE);
        } else {
            this.setVisibility(View.GONE);
        }
    }

    private void refreshCi() {
        if(mPlayHistory instanceof OnlinePlayHistory) {
            OnlinePlayHistory onlinePlayHistory = (OnlinePlayHistory) mPlayHistory;
            mCurCi = onlinePlayHistory.mediaCi;
        } else {
            if(mMediaSetInfoList != null && mMediaSetInfoList.videos != null
                    && mMediaSetInfoList.videos.length > 0){
                if(mMediaSetInfoList != null && mMediaSetInfoList.isVariety() 
                        && mMediaSetInfoList.videos != null) {
                    int index = mMediaSetInfoList.videos.length - 1;
                    mCurCi = mMediaSetInfoList.videos[index].ci;
                } else {
                    mCurCi = mMediaSetInfoList.videos[0].ci;
                }
            }
        }
    }

    private int getUiStyle() {
        if(mMediaInfo == null) {
            return -1;
        }
        if(!mMediaInfo.isMultiSetType()) {
            return UI_STYLE_SINGLE;
        } else {
            if(mMediaSetInfoList != null 
                    && mMediaSetInfoList.style == MediaConstantsDef.MEDIA_TYPE_VARIETY) {
                return UI_STYLE_VARIETY;
            } else {
                return UI_STYLE_MULTY;
            }
        }
    }

	public void setPreferenceSource(int source){
		mDetailEpPlayManager.setPreferenceSource(source);
	}
    
    //UI callback
    private OnEpClickListener mOnEpClickListener = new OnEpClickListener() {
        @Override
        public void onEpClick(int curEp) {
            playMedia(curEp);
        }
    };

    //self def class
    public interface OnEpClickListener {
        public void onEpClick(int curEp);
    }
}
