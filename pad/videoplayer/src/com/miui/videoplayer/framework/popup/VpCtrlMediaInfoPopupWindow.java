package com.miui.videoplayer.framework.popup;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout.LayoutParams;
import android.widget.TextView;

import com.duokan.MediaPlayer.MediaInfo;
import com.miui.video.R;
import com.miui.videoplayer.framework.utils.DKTimeFormatter;

public class VpCtrlMediaInfoPopupWindow extends ManagedPopupWindow {
	
	private TextView lengthTextView;
	private TextView resolutionTextView;
	private TextView videoCodingTextView;
	private TextView audioCodingTextView;
	private TextView audioBitrateTextView;
	private TextView frameRateTextView;
	
	private Context mContext;
//	private View mAnchor;
	
	private static VpCtrlMediaInfoPopupWindow instance;
	
	public static VpCtrlMediaInfoPopupWindow getInstance(Context context) {
		if (instance == null) {
			instance = new VpCtrlMediaInfoPopupWindow(context);
		}
		return instance;
	}

	private VpCtrlMediaInfoPopupWindow(Context context) {
		super(LayoutInflater.from(context).inflate(R.layout.vp_popup_ctrl_video_info, null), LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		this.mContext = context;
		
		setupWindows();
	}

	private void setupWindows() {
		this.setFocusable(false);
		this.setTouchable(false);
		
		View contentView = getContentView();
		lengthTextView = (TextView) contentView.findViewById(R.id.length);
		resolutionTextView = (TextView) contentView.findViewById(R.id.resolution);
		videoCodingTextView = (TextView) contentView.findViewById(R.id.video_coding);
		audioCodingTextView = (TextView) contentView.findViewById(R.id.audio_coding);
		audioBitrateTextView = (TextView) contentView.findViewById(R.id.audio_bitrate);
		frameRateTextView = (TextView) contentView.findViewById(R.id.frame_rate);
		
//		AndroidUtils.setBoldFontForChinese(lengthTextView);
//		AndroidUtils.setBoldFontForChinese(resolutionTextView);
//		AndroidUtils.setBoldFontForChinese(videoCodingTextView);
//		AndroidUtils.setBoldFontForChinese(audioCodingTextView);
//		AndroidUtils.setBoldFontForChinese(audioBitrateTextView);
//		AndroidUtils.setBoldFontForChinese(frameRateTextView);
		
		TextView lengthLabelTextView = (TextView) contentView.findViewById(R.id.length_label);
		TextView resolutionLabelTextView = (TextView) contentView.findViewById(R.id.resolution_label);
		TextView videoCodingLabelTextView = (TextView) contentView.findViewById(R.id.video_coding_label);
		TextView audioCodingLabelTextView = (TextView) contentView.findViewById(R.id.audio_coding_label);
		TextView audioBitrateLabelTextView = (TextView) contentView.findViewById(R.id.audio_bitrate_label);
		TextView frameRateLabelTextView = (TextView) contentView.findViewById(R.id.frame_rate_label);
		
//		AndroidUtils.setBoldFontForChinese(lengthLabelTextView);
//		AndroidUtils.setBoldFontForChinese(resolutionLabelTextView);
//		AndroidUtils.setBoldFontForChinese(videoCodingLabelTextView);
//		AndroidUtils.setBoldFontForChinese(audioCodingLabelTextView);
//		AndroidUtils.setBoldFontForChinese(audioBitrateLabelTextView);
//		AndroidUtils.setBoldFontForChinese(frameRateLabelTextView);
	}

	@Override
	public void show(View anchor) {
//		mAnchor = anchor;
		int x = (int) mContext.getResources().getDimension(R.dimen.vp_ctrl_media_info_location_x);
		int y = (int) mContext.getResources().getDimension(R.dimen.vp_ctrl_media_info_location_y);
		this.showAtLocation(anchor, Gravity.LEFT| Gravity.TOP, x, y);
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

	public static boolean isNull() {
		return instance == null;
	}
	
	public static void setNull() {
		instance = null;
	}
}
