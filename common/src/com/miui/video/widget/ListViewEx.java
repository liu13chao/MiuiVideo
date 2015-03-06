/**
 *   Copyright(c) 2012 DuoKan TV Group
 *    
 *   ListViewEx.java
 *
 *   @author tianli(tianli@duokan.com)
 *
 *   2012-8-20 
 */
package com.miui.video.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.miui.video.helper.OnTouchInterceptor;

/**
 * @author tianli
 * 
 */
public class ListViewEx extends ListView implements OnScrollListener {

	private float mLastMotionX;
	private float mLastMotionY;
	/*
	 * private View emptyView; private View loadView;
	 */
	private View mLoadMoreView;
	private boolean mLoadingFinished = true;
	private boolean mCanLoadMore = false;

	private OnTouchInterceptor mOnTouchInterceptor;
	private OnScrollListener mOnScrollListener;
	private OnLoadMoreListener mOnLoadMoreListener;
	private View mLoadMoreParent = null;

	public ListViewEx(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public ListViewEx(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ListViewEx(Context context) {
		super(context);
		init();
	}

	private void init() {
		//本来可以通过style来定义，但是使用Miui的style会导致编译问题
		this.setVerticalScrollBarEnabled(false);
		this.setWillNotDraw(false);
		// setOnScrollListener to base class.
		super.setOnScrollListener(this);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (getAdapter() == null || getAdapter().isEmpty()) {
			hideView(mLoadMoreView);
		} else {
			if (mCanLoadMore) {
				showView(mLoadMoreView);
			} else {
				hideView(mLoadMoreView);
			}
		}
	}

	private void showView(View view) {
		if (view != null) {
			if (view.getVisibility() != View.VISIBLE) {
				view.setVisibility(View.VISIBLE);
			}
		}
	}

	private void hideView(View view) {
		if (view != null) {
			if (view.getVisibility() != View.GONE) {
				view.setVisibility(View.GONE);
			}
		}
	}

	public void setLoadMoreView(View loadMoreView) {
		if (loadMoreView != null) {
			this.mLoadMoreView = loadMoreView;
			Context context = getContext();
			FrameLayout holderFrame = new FrameLayout(context);
			holderFrame.setLayoutParams(new AbsListView.LayoutParams(
					AbsListView.LayoutParams.MATCH_PARENT,
					AbsListView.LayoutParams.WRAP_CONTENT));
			holderFrame.addView(loadMoreView);
			addFooterView(holderFrame);
			this.mLoadMoreParent = holderFrame;
		}
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (mOnTouchInterceptor != null) {
			boolean isIntercepted = false;
			switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN:
				mLastMotionX = ev.getRawX();
				mLastMotionY = ev.getRawY();
				mOnTouchInterceptor.onPreIntercept(ev);
				break;
			case MotionEvent.ACTION_MOVE:
				float x = ev.getRawX();
				float y = ev.getRawY();
				if (Math.pow(Math.abs(x - mLastMotionX), 2)
						+ Math.pow(Math.abs(y - mLastMotionY), 2) < 2) {
					return false;
				}
				if (mLastMotionX != Integer.MIN_VALUE
						&& mLastMotionY != Integer.MIN_VALUE) {
					double angel = Math.atan(Math.abs(y - mLastMotionY)
							/ Math.abs(x - mLastMotionX));
					if (angel > -Math.PI / 4 && angel < Math.PI / 4) {
						// less than 45 degree, the direction is horizontal
						// scroll.
						isIntercepted = mOnTouchInterceptor
								.onIntercept(
										x < mLastMotionX ? OnTouchInterceptor.SCROLL_LEFT
												: OnTouchInterceptor.SCROLL_RIGHT,
										ev);
					} else {
						isIntercepted = mOnTouchInterceptor.onIntercept(
								y < mLastMotionY ? OnTouchInterceptor.SCROLL_UP
										: OnTouchInterceptor.SCROLL_DOWN, ev);
					}
				}
				mLastMotionX = x;
				mLastMotionY = y;
				if (isIntercepted) {
					return false;
				}
				break;
			}
		}
		return super.onInterceptTouchEvent(ev);
	}

	/*
	 * public boolean isLoading() { return loading; }
	 * 
	 * public void setLoading(boolean loading) { this.loading = loading; }
	 */

	public void setCanLoadMore(boolean canLoadMore) {
		mCanLoadMore = canLoadMore;
		mLoadingFinished = true;
	}

	public boolean isTopmost() {
		if (getChildCount() > 0) {
			View view = getChildAt(0);
			if (Math.abs(view.getTop() - getListPaddingTop()) <= 1
					&& getFirstVisiblePosition() == 0) {
				return true;
			}
			return false;
		}
		return true;
	}

	public boolean isBottommost() {
		boolean canScrollDown;
		int count = getChildCount();
		// The last item is not visible.
		canScrollDown = (getFirstVisiblePosition() + count) < getCount();
		if (!canScrollDown && count > 0) {
			// The last item is visible and the last item's bottom is below
			// list' bottom.
			View child = getChildAt(count - 1);
			canScrollDown = child.getBottom() + getTop() > getBottom()
					- getPaddingBottom();
		}
		return !canScrollDown;
	}

	public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
		this.mOnLoadMoreListener = onLoadMoreListener;
	}

	public void setOnTouchInterceptor(OnTouchInterceptor onTouchInterceptor) {
		this.mOnTouchInterceptor = onTouchInterceptor;
	}

	@Override
	public void setOnScrollListener(OnScrollListener l) {
		this.mOnScrollListener = l;
	}

	public static interface OnLoadMoreListener {
		/**
		 * @return return true means you want to do loading.
		 */
		public void onLoadMore(ListView listView);
	}

	private boolean isLoadMoreViewShown() {
		if (mLoadMoreParent != null && getAdapter() != null
				&& !getAdapter().isEmpty()) {
			for (int i = 0; i < getChildCount(); i++) {
				if (getChildAt(i) == mLoadMoreParent) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if (mCanLoadMore && mLoadingFinished) {
			if (isLoadMoreViewShown()) {
				if(mOnLoadMoreListener != null) {
					mOnLoadMoreListener.onLoadMore(this);
					mLoadingFinished = false;
				}
			}
		}

		if (mOnScrollListener != null) {
			mOnScrollListener.onScroll(view, firstVisibleItem, visibleItemCount,
					totalItemCount);
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (scrollState == SCROLL_STATE_IDLE) {
			// ImageManager.getInstance().resume();
		} else {
			// ImageManager.getInstance().pause();
		}
		if (mOnScrollListener != null) {
			mOnScrollListener.onScrollStateChanged(view, scrollState);
		}
	}

}
