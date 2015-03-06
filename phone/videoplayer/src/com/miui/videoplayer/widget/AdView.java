package com.miui.videoplayer.widget;

import android.content.Context;
import android.text.Html;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.miui.video.R;
import com.miui.videoplayer.media.AdsPlayListener;

public class AdView extends FrameLayout implements AdsPlayListener{

	private static final String TAG = AdView.class.getName();
	private Context mContext;
//	private AdButtonView mAdsButtonView;
	private Button mBtnViewDetail;
	private TextView mCountDownView;
	private boolean mDisableView = false;
	private int mAdDuration = 0;
	private int mAdPlayDuration = 0;
	private NotifyAdsPlayListener mNotifyAdsPlayListener;
	
	public AdView(Context context) {
		super(context);
		mContext = context;
		init();
	}

	public AdView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}

	public AdView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		init();
	}
	
	public void init(){
		setBackgroundResource(R.color.full_translucent);
		View view = View.inflate(mContext, R.layout.vp_adview, null);
		mCountDownView = (TextView)view.findViewById(R.id.countdown);
		mBtnViewDetail = (Button)view.findViewById(R.id.adbtn);
		addView(view);
	}
	
	public void setDisableView(boolean disable){
		mDisableView = disable;
	}
	
	public int getAdDuration(){
		return mAdDuration;
	}
	
	public int getAdPlayDuration(){
		return mAdPlayDuration;
	}
	
	@Override
	public void onAdsPlayStart() {
		if(mNotifyAdsPlayListener != null){
			mNotifyAdsPlayListener.onNotifyAdsStart();
		}
	}

	@Override
	public void onAdsPlayEnd() {
		setVisibility(View.GONE);
		mCountDownView.setText("");
		if(mNotifyAdsPlayListener != null){
			mNotifyAdsPlayListener.onNotifyAdsEnd();
		}
	}

	@Override
	public void onAdsDuration(int duration) {
		Log.d(TAG, "duration:" + duration);
		mAdDuration = duration;
		if(mNotifyAdsPlayListener != null){
			mNotifyAdsPlayListener.onAdsDuration(duration);
		}
	}

	@Override
	public void onAdsTimeUpdate(int leftSeconds) {
	    if(mDisableView){
	        if(getVisibility() == View.VISIBLE){
	            setVisibility(View.INVISIBLE);
	        }
        }else{
            if(getVisibility() != View.VISIBLE){
                setVisibility(View.VISIBLE);
            }
        }
		fillCountDown(leftSeconds);
		mAdPlayDuration = mAdDuration - leftSeconds;
	}
	
	private void fillCountDown(int leftSeconds){
		String format = mContext.getString(R.string.ad_countdown);
		String text = String.format(format, "<font color='#ff4444'>" + leftSeconds + " </font>" );
		mCountDownView.setText(Html.fromHtml(text));
	}
	
	public Button getAdButtonView(){
		return mBtnViewDetail;
	}
	
	public void setNotifyAdsPlayListener(NotifyAdsPlayListener listener){
		this.mNotifyAdsPlayListener = listener;
	}
	
	public interface NotifyAdsPlayListener{
		public void onNotifyAdsStart();
		public void onNotifyAdsEnd();
		public void onAdsDuration(int duration);
	}
}
