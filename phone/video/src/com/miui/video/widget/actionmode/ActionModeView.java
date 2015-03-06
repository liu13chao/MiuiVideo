package com.miui.video.widget.actionmode;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;

import com.miui.video.R;
import com.miui.video.widget.actionmode.ActionModeBottomMenuItem.ActionModeItemClickListener;

/**
 *@author tangfuling
 *
 */

public class ActionModeView extends RelativeLayout {
	
	private Context mContext;
	private ActionModeTopView mTopView;
	private ActionModeBottomView mBottomView;
	private View mTopBg;
	private View mTopMask;
	private View mBottomBg;
	private int mTopViewHeight;
	private int mBottomViewHeight;
	private int mTopBgHeight;
	private int mTopMaskHeight;
	private int mBottomBgHeight;
	
	private Callback mCallback;
	private ActionModeBottomMenu mMenu;
	
	//flags
	private boolean mIsVisible;
	private boolean mSelectAll;
	private boolean mIsAnimationPlaying;
	private int mAnimationCount;
	
	private static final int ANIMATE_DURATION = 300;
	
	public ActionModeView(Context context) {
		super(context);
		this.mContext = context;
		
		init();
	}
	
	public ActionModeView(Context context, Callback callback) {
		super(context);
		this.mContext = context;
		this.mCallback = callback;
		
		init();
	}
	
	public void setTitle(String title) {
		mTopView.setTitle(title);
	}
	
	public boolean isVisible() {
		return mIsVisible;
	}
	
	public void startActionMode() {
		if(mIsAnimationPlaying) {
			return;
		}
		
		if(!mIsVisible) {
			mIsVisible = true;
			animationIn();
		}
	}
	
	public void exitActionMode() {
		if(mIsAnimationPlaying) {
			return;
		}
		
		if(mIsVisible) {
			mIsVisible = false;
			mSelectAll = false;
			animationOut();
		}
	}
	
	//UI状态与mSelectAll状态相反
	public void setUISelectAll() {
		mSelectAll = false;
		mTopView.getSelecteAllBtn().setText(R.string.select_all);
		setBottomViewEnabled(false);
	}
	
	public void setUISelectNone() {
		mSelectAll = true;
		mTopView.getSelecteAllBtn().setText(R.string.select_none);
		setBottomViewEnabled(true);
	}
	
	public void setUiSelectPart() {
		mSelectAll = false;
		mTopView.getSelecteAllBtn().setText(R.string.select_all);
		setBottomViewEnabled(true);
	}
	
	//init
	private void init() {
		initDimen();
		initView();
	}
	
	private void initDimen() {
		mTopViewHeight = getResources().getDimensionPixelSize(R.dimen.action_mode_top_view_height);
		mBottomViewHeight = getResources().getDimensionPixelSize(R.dimen.action_mode_bottom_view_height);
		mTopBgHeight = getResources().getDimensionPixelSize(R.dimen.action_mode_top_bg_height);
		mTopMaskHeight = getResources().getDimensionPixelSize(R.dimen.action_mode_top_bg_mask_height);
		mBottomBgHeight = getResources().getDimensionPixelSize(R.dimen.action_mode_bottom_bg_height);
	}
	
	private void initView() {
		initBg();
		initTopView();
		initBottomView();
		initTranslation();
	}
	
	private void initTopView() {
		mTopView = new ActionModeTopView(mContext);
		LayoutParams topViewParams = new LayoutParams(LayoutParams.MATCH_PARENT, mTopViewHeight);
		topViewParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		addView(mTopView, topViewParams);
		mTopView.getCancelBtn().setOnClickListener(mCancelBtnOnClickListener);
		mTopView.getSelecteAllBtn().setOnClickListener(mSelecteAllBtnOnClickListener);
	}
	
	private void initBottomView() {
		mMenu = new ActionModeBottomMenu(mContext);
		if(mCallback != null) {
			mCallback.onCreateBottomMenu(mMenu);
		}
		int menuChildCount = mMenu.getChildCount();
		if(menuChildCount != 0) {
			mBottomView = new ActionModeBottomView(mContext, mMenu);
			mBottomView.setActionModeItemClickListener(mActionModeItemClickListener);
			LayoutParams bottomViewParams = new LayoutParams(LayoutParams.MATCH_PARENT, mBottomViewHeight);
			bottomViewParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			addView(mBottomView, bottomViewParams);
		}
	}
	
	private void initBg() {
		mTopBg = new View(mContext);
		mTopBg.setBackgroundResource(R.drawable.action_mode_top_bg);
		LayoutParams topBgParams = new LayoutParams(LayoutParams.MATCH_PARENT, mTopBgHeight);
		topBgParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		addView(mTopBg, topBgParams);
		
		mTopMask = new View(mContext);
		mTopMask.setBackgroundResource(R.drawable.action_mode_top_bg_mask);
		LayoutParams topBgMaskParams = new LayoutParams(LayoutParams.MATCH_PARENT, mTopMaskHeight);
		topBgMaskParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		addView(mTopMask, topBgMaskParams);
		
		mBottomBg = new View(mContext);
		mBottomBg.setBackgroundResource(R.drawable.action_mode_bottom_bg);
		LayoutParams bottomBgParams = new LayoutParams(LayoutParams.MATCH_PARENT, mBottomBgHeight);
		bottomBgParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		addView(mBottomBg, bottomBgParams);
	}
	
	private void initTranslation() {
		mIsVisible = false;
		
		mTopView.setTranslationY(-mTopViewHeight);
		mTopBg.setTranslationY(-mTopBgHeight);
		mTopMask.setTranslationY(-mTopMaskHeight);
		
		mBottomView.setTranslationY(mBottomViewHeight);
		mBottomBg.setTranslationY(mBottomBgHeight);
	}
	
	//packaged method
	private void animationIn() {
		 animationInTopView(mTopView);
		 animationInTopView(mTopBg);
		 animationInTopView(mTopMask);
		 
		 animationInBottomView(mBottomView);
		 animationInBottomView(mBottomBg);
		 animationInBottomViewInner();
	}
	
	private void animationOut() {
		animationOutTopView(mTopView);
		animationOutTopView(mTopBg);
		animationOutTopView(mTopMask);
		
		animationOutBottomView(mBottomView);
		animationOutBottomView(mBottomBg);
	}
	
	private void animationInTopView(View view) {
		if(view == null) {
			return;
		}
		Animator topViewAnim = ObjectAnimator.ofFloat(view, "translationY", 
        		-view.getHeight(), 0);
		topViewAnim.setInterpolator(new DecelerateInterpolator());
		topViewAnim.setDuration(ANIMATE_DURATION);
		topViewAnim.addListener(mAnimatorListener);
		topViewAnim.start();
	}
	
	private void animationOutTopView(View view) {
		if(view == null) {
			return;
		}
		Animator topViewAnim = ObjectAnimator.ofFloat(view, "translationY", 
        		0, -view.getHeight());
		topViewAnim.setInterpolator(new DecelerateInterpolator());
		topViewAnim.setDuration(ANIMATE_DURATION);
		topViewAnim.addListener(mAnimatorListener);
		topViewAnim.start();
	}
	
	private void animationInBottomView(View view) {
		if(view == null) {
			return;
		}
		Animator bottomViewAnim = ObjectAnimator.ofFloat(view, "translationY", 
				view.getHeight(), 0);
		bottomViewAnim.setInterpolator(new DecelerateInterpolator());
		bottomViewAnim.setDuration(ANIMATE_DURATION);
		bottomViewAnim.addListener(mAnimatorListener);
		bottomViewAnim.start();
	}
	
	private void animationOutBottomView(View view) {
		if(view == null) {
			return;
		}
		Animator bottomViewAnim = ObjectAnimator.ofFloat(view, "translationY", 
				0, view.getHeight());
		bottomViewAnim.setInterpolator(new DecelerateInterpolator());
		bottomViewAnim.setDuration(ANIMATE_DURATION);
		bottomViewAnim.addListener(mAnimatorListener);
		bottomViewAnim.start();
	}
	
	private void animationInBottomViewInner() {
		mBottomView.animationInInner();
	}
	
	private void setBottomViewEnabled(boolean enabled) {
		mBottomView.setIsEnabled(enabled);
	}
	
	//UI callback
	private OnClickListener mCancelBtnOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			exitActionMode();
			if(mCallback != null) {
				mCallback.onActionCancelClick();
			}
		}
	};
	
	private OnClickListener mSelecteAllBtnOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			mSelectAll = !mSelectAll;
			if(mSelectAll) {
				setUISelectNone();
			} else {
				setUISelectAll();
			}
			
			if(mCallback != null) {
				mCallback.onActionSelectAllClick(mSelectAll);
			}
		}
	};
	
	private ActionModeItemClickListener mActionModeItemClickListener = new ActionModeItemClickListener() {
		
		@Override
		public void onActionModeItemClick(ActionModeBottomMenuItem menuItem) {
			if(mCallback != null) {
				mCallback.onActionItemClick(menuItem);
			}
		}
	};
	
	private AnimatorListener mAnimatorListener = new AnimatorListener() {
		
		@Override
		public void onAnimationStart(Animator animation) {
			mAnimationCount++;
			mIsAnimationPlaying = true;
		}
		
		@Override
		public void onAnimationRepeat(Animator animation) {
			
		}
		
		@Override
		public void onAnimationEnd(Animator animation) {
			mAnimationCount--;
			if(mAnimationCount == 0) {
				mIsAnimationPlaying = false;
			}
		}
		
		@Override
		public void onAnimationCancel(Animator animation) {
			mAnimationCount--;
			if(mAnimationCount == 0) {
				mIsAnimationPlaying = false;
			}
		}
	};
	
	//self def class
	public static interface Callback {
		public void onCreateBottomMenu(ActionModeBottomMenu menu);
		public void onActionItemClick(ActionModeBottomMenuItem menuItem);
		public void onActionCancelClick();
		public void onActionSelectAllClick(boolean selectAll);
	}
}
