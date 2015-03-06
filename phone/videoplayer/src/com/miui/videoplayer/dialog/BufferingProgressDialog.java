package com.miui.videoplayer.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.KeyEvent;
import android.view.View;

import com.miui.video.R;

public class BufferingProgressDialog extends Dialog {
//	private Context mContext;
	private View mOnKeyDownListener;
	
	public BufferingProgressDialog(Context context) {
		super(context, R.style.buffer_dialog_style);
//		this.mContext = context;
		setupViews();
	}

	private void setupViews() {
		this.setContentView(R.layout.vp_dialog_buffering);
		this.setCancelable(false);
		this.setCanceledOnTouchOutside(false);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void setOnKeyDownListener(View onKeyDownListener) {
		this.mOnKeyDownListener = onKeyDownListener;
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		if (mOnKeyDownListener != null) {
			mOnKeyDownListener.onKeyDown(KeyEvent.KEYCODE_BACK, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
		}
	}
}
