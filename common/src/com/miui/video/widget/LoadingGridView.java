package com.miui.video.widget;

import com.miui.video.R;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ListAdapter;

public class LoadingGridView extends FrameLayout {

//	private Context mContext;
	private GridView mGridView;
	private View mLoadingView;
	private View mEmptyView;
	private boolean mIsLoading;
	private View loadingResultView;
	
//	private LayoutParams mParams;
	
	public LoadingGridView(Context context) {
		super(context);
		init(context);
	}

	public LoadingGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public LoadingGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context) {
//		this.mContext = context;
		mGridView = (GridView) View.inflate(context, R.layout.loading_gridview, null);
//		mGridView = new GridView(this.mContext);
//		mParams = new FrameLayout.LayoutParams(
//				FrameLayout.LayoutParams.MATCH_PARENT,
//				FrameLayout.LayoutParams.MATCH_PARENT);
//		mGridView.setLayoutParams(mParams);
		addView(mGridView);
		setWillNotDraw(false);
	}
	
	public GridView getGridView(){
		return mGridView;
	}
	
	public void setLoadingView(View loadingView, int topMargin) {
		if (loadingView == null || this.mLoadingView == loadingView){
			return;
		}
		if (mLoadingView != null){
			removeView(mLoadingView);
		}
		mLoadingView = loadingView;
		FrameLayout.LayoutParams ltParams = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.WRAP_CONTENT,
				FrameLayout.LayoutParams.WRAP_CONTENT);
		ltParams.gravity = Gravity.CENTER_HORIZONTAL;
		ltParams.topMargin = topMargin;
		loadingView.setLayoutParams(ltParams);
		loadingView.setVisibility(View.INVISIBLE);
		addView(loadingView);
	}

	public void setLoadingView(View loadingView) {
		if (loadingView == null || this.mLoadingView == loadingView){
			return;
		}
		if (mLoadingView != null){
			removeView(mLoadingView);
		}
		mLoadingView = loadingView;
		FrameLayout.LayoutParams ltParams = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.WRAP_CONTENT,
				FrameLayout.LayoutParams.WRAP_CONTENT);
		ltParams.gravity = Gravity.CENTER;
		loadingView.setLayoutParams(ltParams);
		loadingView.setVisibility(View.INVISIBLE);
		addView(loadingView);
	}

	public View getLoadingView() {
		return mLoadingView;
	}

	public void setShowLoading(boolean bLoading) {
		this.mIsLoading = bLoading;
		if (mLoadingView == null){
			return;
		}
		mLoadingView.setVisibility(bLoading ? View.VISIBLE : View.INVISIBLE);
	}

	public boolean isLoading() {
		return mIsLoading;
	}
	
	public void setEmptyView(View emptyView, int topMargin) {
		if (emptyView == null || this.mEmptyView == emptyView){
			return;
		}
		if (mEmptyView != null){
			removeView(mEmptyView);
		}
		this.mEmptyView = emptyView;
		FrameLayout.LayoutParams ltParams = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.WRAP_CONTENT,
				FrameLayout.LayoutParams.WRAP_CONTENT);
		ltParams.gravity = Gravity.CENTER_HORIZONTAL;
		ltParams.topMargin = topMargin;
		emptyView.setLayoutParams(ltParams);
		emptyView.setVisibility(View.INVISIBLE);
		addView(emptyView);
	}

	public void setEmptyView(View emptyView) {
		if (emptyView == null || this.mEmptyView == emptyView){
			return;
		}
		if (mEmptyView != null){
			removeView(mEmptyView);
		}
		this.mEmptyView = emptyView;
		FrameLayout.LayoutParams ltParams = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.WRAP_CONTENT,
				FrameLayout.LayoutParams.WRAP_CONTENT);
		ltParams.gravity = Gravity.CENTER;
		emptyView.setLayoutParams(ltParams);
		emptyView.setVisibility(View.INVISIBLE);
		addView(emptyView);
	}

	public View getEmptyView() {
		return mEmptyView;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		if(mIsLoading){
			hideView(mEmptyView);
			showView(mLoadingView);
		}else{
			hideView(mLoadingView);
			ListAdapter adapter = mGridView.getAdapter();
			if(adapter == null || adapter.getCount() == 0){
				showView(mEmptyView);
			}else{
				hideView(mEmptyView);
			}
		}
	}
	
	private void showView(View view){
		if(view != null){
			if(view.getVisibility() != View.VISIBLE){
				view.setVisibility(View.VISIBLE);
			}
		}
	}
	
	private void hideView(View view){
		if(view != null){
			if(view.getVisibility() == View.VISIBLE){
				view.setVisibility(View.INVISIBLE);
			}
		}
	}
	
	public void setLoadingResultView(View loadingResultView) {
		if( loadingResultView == null || 
				    this.loadingResultView == loadingResultView)
			return;

		if( this.loadingResultView != null)
			removeView(this.loadingResultView);
		this.loadingResultView = loadingResultView;
		FrameLayout.LayoutParams ltParams = new 
				FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
						         	FrameLayout.LayoutParams.WRAP_CONTENT);
		ltParams.gravity = Gravity.CENTER;
		loadingResultView.setLayoutParams(ltParams);
		loadingResultView.setVisibility(View.INVISIBLE);
		addView(loadingResultView);
	}
	public View getLoadingResultView() {
		return loadingResultView;
	}
	public void setShowLoadingResult(boolean bShowResult) {
		if( loadingResultView == null)
			return;
		loadingResultView.setVisibility(bShowResult ? View.VISIBLE : View.INVISIBLE);
	}
	
}
