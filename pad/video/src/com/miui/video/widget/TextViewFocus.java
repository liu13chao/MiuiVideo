package com.miui.video.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewDebug.ExportedProperty;
import android.widget.TextView;

public class TextViewFocus extends TextView {
	
	public TextViewFocus(Context context) {
		super(context);
	}

	public TextViewFocus(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public TextViewFocus(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	@Override
	@ExportedProperty(category = "focus")
	public boolean isFocused() {
		return true;
	}
}
