package com.miui.videoplayer.framework.popup;

import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

import com.miui.video.R;
import com.miui.videoplayer.framework.DuoKanConstants;
import com.miui.videoplayer.framework.utils.DKTimeFormatter;
import com.miui.videoplayer.framework.utils.DisplayInformationFetcher;

public class ProgressTimePopupWindow extends ManagedPopupWindow {
	public static final int PREV_MEDIA_NOTICE_POSITION = -1001;
	public static final int NEXT_MEDIA_NOTICE_POSITION = -1002;
	
	private TopStatusBarPopupWindow mTopStatusBarPopupWindow;
	private VpCtrlMenuPopupWindow mCtrlMenuPopupWindow;
	
	public TopStatusBarPopupWindow getTopStatusBarPopupWindow() {
		return mTopStatusBarPopupWindow;
	}

	public void setTopStatusBarPopupWindow(TopStatusBarPopupWindow topStatusBarPopupWindow) {
		this.mTopStatusBarPopupWindow = topStatusBarPopupWindow;
	}
	
	public void setCtrlMenuPopupWindow(VpCtrlMenuPopupWindow ctrlMenuPopupWindow) {
		this.mCtrlMenuPopupWindow = ctrlMenuPopupWindow;
	}

	private TextView mTimeTextView;
	private ImageView mOrientationImageView;
	private ImageView mLeftOrientationImageView;
	private boolean mSmallFont;
	private Context mContext;
	
	public ProgressTimePopupWindow(Context context, boolean smallFont) {
		super();
		this.setHeight(LayoutParams.WRAP_CONTENT);
		this.setWidth(LayoutParams.WRAP_CONTENT);
		if (DuoKanConstants.ENABLE_V5_UI) {
			this.setContentView(LayoutInflater.from(context).inflate(R.layout.vp_popup_center_time_progress_v5, null));
		} else {
			this.setContentView(LayoutInflater.from(context).inflate(R.layout.vp_popup_center_time_progress, null));
		}
		this.mContext = context;
		this.mSmallFont = smallFont;
		setupWindow();
	}
	
	private void setupWindow() {
		View rootView = getContentView();
		mOrientationImageView = (ImageView) rootView.findViewById(R.id.progress_time_imageview);
		mTimeTextView = (TextView) rootView.findViewById(R.id.progress_time_textview);
		if (!DuoKanConstants.ENABLE_V5_UI) {
			if (mSmallFont) {
				float smallSize = mContext.getResources().getDimension(R.dimen.popup_center_time_progress_textview_font_size_small);
				mTimeTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallSize);
			} else {
				float bigSize = mContext.getResources().getDimension(R.dimen.popup_center_time_progress_textview_font_size_big);
				mTimeTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, bigSize);
			}
		} else {
			mLeftOrientationImageView = (ImageView) rootView.findViewById(R.id.progress_time_left_imageview);
		}
	}
	
	public void updatePosition(int position) {
//		Log.e("UPDATE: ", position + "");
		if (position == PREV_MEDIA_NOTICE_POSITION) {
			mTimeTextView.setText(R.string.adjust_position_prev_media_notice);
		} else if (position == NEXT_MEDIA_NOTICE_POSITION) {
			mTimeTextView.setText(R.string.adjust_position_next_media_notice);
		} else {
			mTimeTextView.setText(DKTimeFormatter.getInstance().stringForTime(position));
		}
	}
	
	public void setOrientation(boolean forward) {
		if (DuoKanConstants.ENABLE_V5_UI) {
			if (forward) {
				mOrientationImageView.setImageResource(R.drawable.vp_arrow_right_v5);
				mOrientationImageView.setVisibility(View.VISIBLE);
				mLeftOrientationImageView.setVisibility(View.INVISIBLE);
			} else {
				mOrientationImageView.setImageResource(R.drawable.vp_arrow_right_v5);
				mOrientationImageView.setVisibility(View.INVISIBLE);
				mLeftOrientationImageView.setVisibility(View.VISIBLE);
			}
		} else {
			if (forward) {
				mOrientationImageView.setImageResource(R.drawable.vp_arrow_right);
			} else {
				mOrientationImageView.setImageResource(R.drawable.vp_arrow_left);
			}
		}
	}

	@Override
	public void show(View anchor) {
	}

	@Override
	public void showAtLocation(View parent, int gravity, int x, int y) {
//		super.showAtLocation(parent, gravity, x, y);
		if (DuoKanConstants.ENABLE_V5_UI) {
			if (mTopStatusBarPopupWindow != null) {
				mTopStatusBarPopupWindow.dismiss();
			}
			if (mCtrlMenuPopupWindow != null && mCtrlMenuPopupWindow.isShowing()) {
				mCtrlMenuPopupWindow.dismiss();
			}
			super.showAtLocation(parent, Gravity.TOP| Gravity.CENTER, 0, 0);
		} else {
			super.showAtLocation(parent, gravity, x, y);
		}
	}
	
}
