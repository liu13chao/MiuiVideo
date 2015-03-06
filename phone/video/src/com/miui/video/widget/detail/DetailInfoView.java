package com.miui.video.widget.detail;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.miui.video.R;
import com.miui.video.type.MediaInfo;
import com.miui.video.util.StringUtils;
import com.miui.video.util.Util;

public class DetailInfoView extends LinearLayout {

	private TextView mScoreView;
	private TextView mAreaTimeView;
	private TextView mTypeView;
	private ActorsView mActorsView;
	
	public DetailInfoView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public DetailInfoView(Context context, AttributeSet attrs) {
		super(context, attrs);
	      init();
	}

	public DetailInfoView(Context context) {
		super(context);
	      init();
	}
	
	private void init(){
	    setGravity(Gravity.BOTTOM);
	}
	
	protected void setActors(String actors) {
		if(Util.isEmpty(actors)) {
			mActorsView.setVisibility(View.GONE);
		} else {
			mActorsView.setVisibility(View.VISIBLE);
			mActorsView.setActors(actors);
		}
	}
	
	protected void setMediaInfo(MediaInfo mediaInfo) {
		if(mediaInfo == null) {
			return;
		}
		String score = "%.1f";
		score = StringUtils.formatString(score, mediaInfo.score);
		String actors = mediaInfo.actors;
		StringBuffer type = new StringBuffer();
		type.append(mediaInfo.category);
		StringBuffer areaAndTimeSb = new StringBuffer();
		if(!Util.isEmpty(mediaInfo.area)) {
			areaAndTimeSb.append(mediaInfo.area);
			areaAndTimeSb.append(" | ");
		}
		areaAndTimeSb.append(mediaInfo.issuedate);
		
		mScoreView.setText(score);
		mAreaTimeView.setText(areaAndTimeSb);
		mTypeView.setText(type);
		mActorsView.setActors(actors);
	}

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mScoreView = (TextView) findViewById(R.id.detail_info_score);
        mAreaTimeView = (TextView) findViewById(R.id.detail_info_area_time);
        mTypeView = (TextView) findViewById(R.id.detail_info_type);
        mActorsView = (ActorsView) findViewById(R.id.detail_info_actors);
    }
	
}
