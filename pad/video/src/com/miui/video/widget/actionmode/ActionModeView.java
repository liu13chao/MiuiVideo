package com.miui.video.widget.actionmode;

import com.miui.video.R;
import com.miui.video.widget.actionmode.ActionModeBottomMenuItem.ActionModeItemClickListener;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

/**
 *@author tangfuling
 *
 */

public class ActionModeView extends FrameLayout {
	
	private Context mContext;
	
	private ViewGroup mContainer;
	protected ActionModeTopView mTopView;
	private ActionModeBottomView mBottomView;
	
//	private View mTopBg;
//	private View mTopMask;
	private View mBottomBg;
	private int mTopViewHeight;
	private int mBottomViewHeight;
//	private int mTopBgHeight;
//	private int mTopMaskHeight;
	private int mBottomBgHeight;
	
	private Callback mCallback;
	private ActionModeBottomMenu mMenu;
	
	//flags
//	private boolean mIsVisible;
	private boolean mIsEdit;
	private boolean mSelectAll;
	private boolean mIsAnimationPlaying;
	private boolean mIsLight = true;
	private int mAnimationCount;
	
	private static final int ANIMATE_DURATION = 300;
	
	public ActionModeView(Context context) {
		super(context);
		this.mContext = context;
		
		init();
	}
	
	public ActionModeView(Context context, Callback callback, ViewGroup container, boolean isLight) {
		super(context);
		this.mContext = context;
		this.mCallback = callback;
		this.mContainer = container;
		this.mIsLight = isLight;
		init();
	}
	
	public void setTitle(String title) {
		mTopView.setTitle(title);
	}
	
//	public boolean isVisible() {
//		return mIsVisible;
//	}
	
	public void setEnable(boolean isEnable) {
		mTopView.setEnable(isEnable);
		if (!isEnable) {
			exitActionMode();
		}
	}
	
	public boolean isEdit() {
		return mIsEdit;
	}
	
	public void startActionMode() {
		if(mIsAnimationPlaying) {
			return;
		}
		if (!mIsEdit) {
			mIsEdit = true;
			animationIn();
			mTopView.setEdit(true);
			mContainer.bringChildToFront(this);
		}
//		if(!mIsVisible) {
//			mIsVisible = true;
//			animationIn();
//		}
	}
	
	public void exitActionMode() {
		if(mIsAnimationPlaying) {
			return;
		}
		
		if(mIsEdit) {
			mIsEdit = false;
			mSelectAll = false;
			mTopView.setEdit(false);
			animationOut();
		}
	}

	public void setUISelectAll() {
		mSelectAll = false;
		mTopView.setUISelectAll();
		setBottomViewEnabled(false);
	}
	
	public void setUISelectNone() {
		mSelectAll = true;
		mTopView.setUISelectNone();
		setBottomViewEnabled(true);
	}
	
	public void setUiSelectPart() {
		mSelectAll = false;
		mTopView.setUiSelectPart();
		setBottomViewEnabled(true);
	}
	
	public int getBottomHeight() {
		return mBottomBgHeight;
	}
	
	//init
	private void init() {
		initDimen();
		initView();
	}
	
	private void initDimen() {
		mBottomViewHeight = getResources().getDimensionPixelSize(R.dimen.action_mode_bottom_view_height);
//		mTopBgHeight = getResources().getDimensionPixelSize(R.dimen.action_mode_top_bg_height);
//		mTopMaskHeight = getResources().getDimensionPixelSize(R.dimen.action_mode_top_bg_mask_height);
		if (mIsLight) {
			mTopViewHeight = getResources().getDimensionPixelSize(R.dimen.local_media_list_top_margin);
			mBottomBgHeight = getResources().getDimensionPixelSize(R.dimen.action_mode_bottom_bg_height);
		} else {
			mTopViewHeight = getResources().getDimensionPixelSize(R.dimen.video_common_title_height);
			mBottomBgHeight = getResources().getDimensionPixelSize(R.dimen.action_mode_bottom_light_bg_height);
//			mBottomBgHeight = getResources().getDimensionPixelSize(R.dimen.local_detail_more_height);
		}
	}
	
	private void initView() {
		initBg();
		initTopView();
		initBottomView();
		initTranslation();
		if (mContainer != null) {
			LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			mContainer.addView(this, params);
			mContainer.bringChildToFront(this);
		}
	}
	
	private void initBg() {
//		mTopBg = new View(mContext);
//		mTopBg.setBackgroundResource(R.drawable.action_mode_top_bg);
//		LayoutParams topBgParams = new LayoutParams(LayoutParams.MATCH_PARENT, mTopBgHeight);
//		topBgParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
//		addView(mTopBg, topBgParams);
//		
//		mTopMask = new View(mContext);
//		mTopMask.setBackgroundResource(R.drawable.action_mode_top_bg_mask);
//		LayoutParams topBgMaskParams = new LayoutParams(LayoutParams.MATCH_PARENT, mTopMaskHeight);
//		topBgMaskParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
//		addView(mTopMask, topBgMaskParams);
		
		mBottomBg = new View(mContext);
		if (mIsLight) {
			mBottomBg.setBackgroundResource(R.drawable.action_mode_bottom_bg);
		} else {
			int color = getResources().getColor(R.color.dlg_light);
			mBottomBg.setBackgroundColor(color);
		}
		LayoutParams bottomBgParams = new LayoutParams(LayoutParams.MATCH_PARENT, mBottomBgHeight);
		bottomBgParams.gravity = Gravity.BOTTOM;
		addView(mBottomBg, bottomBgParams);
	}
	
	private void initTopView() {
		mTopView = new ActionModeTopView(mContext, mIsLight);
		LayoutParams topViewParams = new LayoutParams(LayoutParams.MATCH_PARENT, mTopViewHeight);
		addView(mTopView, topViewParams);
		mTopView.getCancelBtn().setOnClickListener(mTopBtnOnClickListener);
		mTopView.getSelecteAllBtn().setOnClickListener(mTopBtnOnClickListener);
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
			bottomViewParams.gravity = Gravity.BOTTOM;
			addView(mBottomView, bottomViewParams);
		}
	}
	
	private void initTranslation() {
		mIsEdit = false;
		
//		mTopView.setTranslationY(-mTopViewHeight);
//		mTopBg.setTranslationY(-mTopBgHeight);
//		mTopMask.setTranslationY(-mTopMaskHeight);
		
		mBottomView.setTranslationY(mBottomViewHeight);
		if (mBottomBg != null) {
			mBottomBg.setTranslationY(mBottomBgHeight);
		}
	}
	
	//packaged method
	private void animationIn() {
//		animationInTopView(mTopView);
//		animationInTopView(mTopBg);
//		animationInTopView(mTopMask);
				
		animationInBottomView(mBottomView);
		if (mBottomBg != null) {
			animationInBottomView(mBottomBg);
		}
		animationInBottomViewInner();
	}
	
	private void animationOut() {
//		animationOutTopView(mTopView);
//		animationOutTopView(mTopBg);
//		animationOutTopView(mTopMask);
		
		animationOutBottomView(mBottomView);
		if (mBottomBg != null) {
			animationOutBottomView(mBottomBg);
		}
	}
	
//	private void animationInTopView(View view) {
//		if(view == null) {
//			return;
//		}
//		Animator topViewAnim = ObjectAnimator.ofFloat(view, "translationY", 
//        		-view.getHeight(), 0);
//		topViewAnim.setInterpolator(new DecelerateInterpolator());
//		topViewAnim.setDuration(ANIMATE_DURATION);
//		topViewAnim.addListener(mAnimatorListener);
//		topViewAnim.start();
//	}
	
//	private void animationOutTopView(View view) {
//		if(view == null) {
//			return;
//		}
//		Animator topViewAnim = ObjectAnimator.ofFloat(view, "translationY", 
//        		0, -view.getHeight());
//		topViewAnim.setInterpolator(new DecelerateInterpolator());
//		topViewAnim.setDuration(ANIMATE_DURATION);
//		topViewAnim.addListener(mAnimatorListener);
//		topViewAnim.start();
//	}
	
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
	private OnClickListener mTopBtnOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if (v == mTopView.getCancelBtn()) {
				exitActionMode();
				if(mCallback != null) {
					mCallback.onActionCancelClick();
				}
			} else if (v == mTopView.getSelecteAllBtn()) {
				if (mIsEdit) {
					mSelectAll = !mSelectAll;
					if(mSelectAll) {
						setUISelectNone();
					} else {
						setUISelectAll();
					}
					if(mCallback != null) {
						mCallback.onActionSelectAllClick(mSelectAll);
					}
				} else {
					mCallback.onActionEditClick();
				}
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
		public void onActionEditClick();
		public void onActionCancelClick();
		public void onActionSelectAllClick(boolean selectAll);
	}
}
