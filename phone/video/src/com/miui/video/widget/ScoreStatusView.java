package com.miui.video.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.miui.video.R;

public class ScoreStatusView extends FrameLayout {

	private Context mContext;
	
	//UI
	private ProgressBar mProgressBar1;
	private ProgressBar mProgressBar2;
	private ProgressBar mProgressBar3;
	private ProgressBar mProgressBar4;
	private ProgressBar mProgressBar5;
	private TextView mPercentTv1;
	private TextView mPercentTv2;
	private TextView mPercentTv3;
	private TextView mPercentTv4;
	private TextView mPercentTv5;
	
	//data
	private float[] scorePercents;
	
	public ScoreStatusView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
		init();
	}

	public ScoreStatusView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		init();
	}

	public ScoreStatusView(Context context) {
		super(context);
		this.mContext = context;
		init();
	}
	
	public void setScorePercents(float[] scorePercents) {
		this.scorePercents = scorePercents;
		refresh();
	}
	
	private void init() {
		View view = View.inflate(mContext, R.layout.score_status, null);
		addView(view);
		
		mProgressBar1 = (ProgressBar) view.findViewById(R.id.score_status_progress1);
		mProgressBar2 = (ProgressBar) view.findViewById(R.id.score_status_progress2);
		mProgressBar3 = (ProgressBar) view.findViewById(R.id.score_status_progress3);
		mProgressBar4 = (ProgressBar) view.findViewById(R.id.score_status_progress4);
		mProgressBar5 = (ProgressBar) view.findViewById(R.id.score_status_progress5);
		mPercentTv1 = (TextView) view.findViewById(R.id.score_status_tv1);
		mPercentTv2 = (TextView) view.findViewById(R.id.score_status_tv2);
		mPercentTv3 = (TextView) view.findViewById(R.id.score_status_tv3);
		mPercentTv4 = (TextView) view.findViewById(R.id.score_status_tv4);
		mPercentTv5 = (TextView) view.findViewById(R.id.score_status_tv5);
		
		refresh();
	}
	
	private void refresh() {
		String percentStatus = mContext.getResources().getString(R.string.percent_status);
		if(scorePercents != null && scorePercents.length >= 5) {
			mProgressBar1.setProgress((int) scorePercents[0]);
			mProgressBar2.setProgress((int) scorePercents[1]);
			mProgressBar3.setProgress((int) scorePercents[2]);
			mProgressBar4.setProgress((int) scorePercents[3]);
			mProgressBar5.setProgress((int) scorePercents[4]);
			String percentStatus1 = String.format(percentStatus, scorePercents[0]);
			mPercentTv1.setText(percentStatus1);
			String percentStatus2 = String.format(percentStatus, scorePercents[1]);
			mPercentTv2.setText(percentStatus2);
			String percentStatus3 = String.format(percentStatus, scorePercents[2]);
			mPercentTv3.setText(percentStatus3);
			String percentStatus4 = String.format(percentStatus, scorePercents[3]);
			mPercentTv4.setText(percentStatus4);
			String percentStatus5 = String.format(percentStatus, scorePercents[4]);
			mPercentTv5.setText(percentStatus5);
		} else {
			mProgressBar1.setProgress(0);
			mProgressBar2.setProgress(0);
			mProgressBar3.setProgress(0);
			mProgressBar4.setProgress(0);
			mProgressBar5.setProgress(0);
			String percentStatus0 = String.format(percentStatus, 0f);
			mPercentTv1.setText(percentStatus0);
			mPercentTv2.setText(percentStatus0);
			mPercentTv3.setText(percentStatus0);
			mPercentTv4.setText(percentStatus0);
			mPercentTv5.setText(percentStatus0);
		}
	}

}
