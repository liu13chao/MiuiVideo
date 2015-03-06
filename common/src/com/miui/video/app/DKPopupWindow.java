/**
 *   Copyright(c) 2012 DuoKan TV Group
 *    
 *   DKPopupWindow.java
 *
 *   @author tianli(tianli@duokan.com)
 *
 *   2012-8-26 
 */
package com.miui.video.app;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.PopupWindow;

/**
 * @author tianli
 * 
 */
public class DKPopupWindow extends FrameLayout implements DialogInterface {
	protected PopupWindow window;
	protected Context context;
	protected View contentView;
	protected WindowManager windowManager;
	protected int animationStyle;
	protected int offsetX = 0, offsetY = 0;
	private OnWindowListener onWindowListener;

	public void setAnimStyle(int animStyle) {
		this.animationStyle = animStyle;
	}

	public DKPopupWindow(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public DKPopupWindow(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public DKPopupWindow(Context context) {
		super(context);
		init();
	}

	private void init() {
		this.context = getContext();
		window = createPopupWindow(context);
		windowManager = (WindowManager) this.context
				.getSystemService(Context.WINDOW_SERVICE);
	}

	public boolean isShowing() {
		return window.isShowing();
	}

	public void setHeight(int height) {
		if (window != null) {
			window.setHeight(height);
		}
	}

	public void setWidth(int width) {
		if (window != null) {
			window.setWidth(width);
		}
	}

	public void setOutsideDismiss(boolean outsideDismiss) {
		window.setFocusable(outsideDismiss);
		window.setOutsideTouchable(outsideDismiss);
	}

	public void setOnWindowListener(OnWindowListener onWindowListener) {
		this.onWindowListener = onWindowListener;
	}

	public void setBackgroundDrawable(Drawable drawable) {
		if (window != null) {
			window.setBackgroundDrawable(drawable);
		}
	}

	private PopupWindow createPopupWindow(Context context) {
		final PopupWindow popupWindow;

		if (contentView != null) {
			popupWindow = new PopupWindow(contentView);
		} else {
			popupWindow = new PopupWindow(context);
		}
		popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
		popupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
		popupWindow.setBackgroundDrawable(new ColorDrawable());
		popupWindow.setTouchable(true);
		popupWindow.setFocusable(true);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setContentView(this);
		return popupWindow;
	}

	public final void showAtLocation(View anchor, int gravity, int xoff,
			int yoff) {
		window.showAtLocation(anchor, gravity, xoff, yoff);
	}

	public final void showAsDropDown(View anchor, int xoff, int yoff) {
		window.showAsDropDown(anchor, xoff, yoff);
	}

	public final void setContentView(View contentView) {
		this.contentView = contentView;
		addView(contentView);
	}

	@Override
	public void dismiss() {
		try {
			window.dismiss();
		} catch (Exception e) {
		}
	}

	@Override
	public void cancel() {
		dismiss();
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if (onWindowListener != null) {
			onWindowListener.onDismiss(this);
		}
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		if (onWindowListener != null) {
			onWindowListener.onShow(this);
		}
	}

	public static interface OnWindowListener {
		public void onShow(DKPopupWindow window);

		public void onDismiss(DKPopupWindow window);
	}
}