package com.miui.video.widget.searchbox;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

/**
 *@author tangfuling
 *
 */

public class EditTextIME extends EditText {

	private DispatchKeyEventPreImeListener listener;
	
	public EditTextIME(Context context) {
		super(context);

	}

	public EditTextIME(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

	}

	public EditTextIME(Context context, AttributeSet attrs) {
		super(context, attrs);

	}
	
	public void setDispatchKeyEventPreImeListener(DispatchKeyEventPreImeListener listener) {
		this.listener = listener;
	}

	@Override
	public boolean dispatchKeyEventPreIme(KeyEvent event) {
		if(listener != null) {
			return listener.onDispatchKeyEventPreIme(event);
		}
		return super.dispatchKeyEventPreIme(event);
	}
	
	public interface DispatchKeyEventPreImeListener {
		public boolean onDispatchKeyEventPreIme(KeyEvent event);
	}
}
