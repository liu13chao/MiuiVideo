package com.miui.videoplayer.widget;

import com.duokan.MediaPlayer.MediaInfo;
import com.miui.video.R;
import com.miui.videoplayer.common.DKTimeFormatter;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

public class VideoInfoView extends FrameLayout {

	private Context mContext;
	
	private TextView lengthTextView;
	private TextView resolutionTextView;
	private TextView videoCodingTextView;
	private TextView audioCodingTextView;
	private TextView audioBitrateTextView;
	private TextView frameRateTextView;
	
	public VideoInfoView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		init();
	}

	public VideoInfoView(Context context) {
		super(context);
		this.mContext = context;
		init();
	}
	

	public void updateValues(MediaInfo mediaInfo) {
		lengthTextView.setText(DKTimeFormatter.getInstance().stringForTime(mediaInfo.duration));
		resolutionTextView.setText(mediaInfo.videoWidth + "*" + mediaInfo.videoHeight);
		String noVideoString = mContext.getResources().getString(R.string.video_info_audio_null);
		if (mediaInfo.videoCodecName != null) {
			videoCodingTextView.setText(mediaInfo.videoCodecName.toUpperCase());
		} else {
			videoCodingTextView.setText(noVideoString);
		}
		if (mediaInfo.audioCodecName != null) {
			audioCodingTextView.setText(mediaInfo.audioCodecName.toUpperCase());
		} else {
			audioCodingTextView.setText(noVideoString);
		}
		audioBitrateTextView.setText(mediaInfo.audioSampleRate + "");
		frameRateTextView.setText(mediaInfo.videoFrameRate + "");		
	}

	//init
	private void init() {
		View view = View.inflate(mContext, R.layout.vp_video_info, null);
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		addView(view, params);
		
		lengthTextView = (TextView) view.findViewById(R.id.length);
		resolutionTextView = (TextView) view.findViewById(R.id.resolution);
		videoCodingTextView = (TextView) view.findViewById(R.id.video_coding);
		audioCodingTextView = (TextView) view.findViewById(R.id.audio_coding);
		audioBitrateTextView = (TextView) view.findViewById(R.id.audio_bitrate);
		frameRateTextView = (TextView) view.findViewById(R.id.frame_rate);
	}
}
