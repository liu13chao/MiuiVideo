/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   LandscapeVideoController.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-11-26
 */
package com.miui.videoplayer.controller;

import java.util.ArrayList;
import java.util.List;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.miui.video.R;
import com.miui.video.controller.AnimatorFactory;
import com.miui.video.controller.UIConfig;
import com.miui.videoplayer.controller.ControllerView.OnControlEventListener;
import com.miui.videoplayer.fragment.CoreFragment;
import com.miui.videoplayer.fragment.MilinkFragment;
import com.miui.videoplayer.fragment.VideoProxy;
import com.miui.videoplayer.media.MediaPlayerControl;
import com.miui.videoplayer.menu.Menu;
import com.miui.videoplayer.model.OnlineLoader;
import com.miui.videoplayer.videoview.IVideoView;
import com.miui.videoplayer.widget.GestureBrightness;
import com.miui.videoplayer.widget.GestureSeek;
import com.miui.videoplayer.widget.GestureVolumn;
import com.miui.videoplayer.widget.MediaController;
import com.miui.videoplayer.widget.SelectSourceView;
import com.miui.videoplayer.widget.StatusBar;

/**
 * @author tianli
 *
 */
public class FullScreenVideoController extends RelativeLayout implements
IVideoLifeCycle, OnControlEventListener{

    public static final String TAG = "FullScreenVideoController";
    
    private Activity mActivity;
    private FrameLayout mAnchor;

    private Handler mUiHandler = new Handler(Looper.getMainLooper());

    private MediaPlayerControl mPlayer;
    private CoreFragment mCoreFragment;
    private VideoProxy mVideoProxy;

    private boolean mIsShowing = false;
    private boolean mIsVideoLoading = false;

    // Views
    private ImageView mLeftMask;
    private View mLeftLayout;
    private MediaController mMediaController;
    private StatusBar mStatusBar;
    private GestureBrightness mBrightness;
    private GestureVolumn mVolumn;
    private GestureSeek mSeek;
    private Menu mMenu;
    private SelectSourceView mSelectSourceView;
    
    // Screen Locker
    private ImageView mScreenLocker;
    private boolean mIsScreenLocked = false;
    private OrientationUpdater mOrientationUpdater;
    
    // State 
    public static final int STATE_IDLE = 0;
    public static final int STATE_LOCK_SCREEN = 1;
    public static final int STATE_LOADING = 2;
    public static final int STATE_MILINK = 3;
    public static final int STATE_FULL = 4;

    private List<Animator> mAnimators = new ArrayList<Animator>();

    public FullScreenVideoController(Context context, AttributeSet attrs,
            int defStyle) {
        super(context, attrs, defStyle);
    }

    public FullScreenVideoController(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FullScreenVideoController(Context context) {
        super(context);
    }

    public void attachActivity(Activity activity, FrameLayout anchor, 
            OrientationUpdater orientationUpdater){
        mActivity = activity;
        mAnchor = anchor;
        if(mSelectSourceView != null){
            mSelectSourceView.setAnchor(mAnchor);
        }
        mOrientationUpdater = orientationUpdater;
    }
    
    public void attachVideoProxy(VideoProxy videoProxy) {
        mVideoProxy = videoProxy;
        mMediaController.attachVideoProxy(mVideoProxy);
        mStatusBar.attachVideoProxy(mVideoProxy);
        if(mSelectSourceView != null){
            mSelectSourceView.attachVideoProxy(mVideoProxy);
        }
    }

    @Override
    public void onCompletion(IVideoView videoView) {
    }

    @Override
    public void onPrepared(IVideoView videoView) {
        mIsVideoLoading = false;
        hideController();
    }

    @Override
    public void onBufferingStart(IVideoView videoView) {
    }

    @Override
    public void onBufferingEnd(IVideoView videoView) {
    }

    @Override
    public void onBufferingPercent(IVideoView videoView, int percent) {
    }

    @Override
    public void onVideoLoadingStart(IVideoView videoView) {
        mIsVideoLoading = true;
        if(mIsShowing){
            //ignore
            return;
        }
        if(videoView == null || videoView.canBuffering()){
            showController();
            mUiHandler.removeCallbacks(mAutoDismiss);
        }
    }

    @Override
    public void onEpLoadingStart() {
        mIsVideoLoading = true;
        if(mIsShowing){
            mLeftLayout.setVisibility(View.GONE);
            mMenu.setVisibility(View.GONE);
            mMediaController.setVisibility(View.GONE);
        }else{
            showController();
        }
        mUiHandler.removeCallbacks(mAutoDismiss);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mMediaController = (MediaController)findViewById(R.id.media_controller);
        mLeftLayout = findViewById(R.id.left_layout);
        mLeftMask = (ImageView)findViewById(R.id.left_mask);
        mScreenLocker = (ImageView)findViewById(R.id.vp_screen_locker);
        mScreenLocker.setOnClickListener(mOnClickListener);
        mStatusBar = (StatusBar)findViewById(R.id.status_bar);
        mMenu = (Menu)findViewById(R.id.vp_menu);
        setVisibility(View.GONE);
    }    

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        resetAutoDismiss();
        return super.dispatchTouchEvent(event);
    }

    private OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if(view == mScreenLocker){
                if(mIsScreenLocked){
                    enterUnlockScreen();
                }else{
                    enterLockScreen();
                }
                mIsScreenLocked = !mIsScreenLocked;
            }
        }
    };

    private void enterUnlockScreen(){
//        LayoutParams params = (LayoutParams)mScreenLocker.getLayoutParams();
//        params.leftMargin =  getResources().getDimensionPixelSize(
//                R.dimen.vp_mc_screen_locker_margin_left);
        mScreenLocker.setImageResource(R.drawable.vp_screen_locker_bg);
        mScreenLocker.setTranslationX(getResources().getDimensionPixelSize(
                R.dimen.vp_mc_screen_locker_margin_left));
//        mScreenLocker.setLayoutParams(params);
        mLeftMask.setVisibility(View.VISIBLE);
        animateForLocker(false);
        mOrientationUpdater.enableRotation();
    }

    private void enterLockScreen(){
//        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)mScreenLocker.getLayoutParams();
//        params.leftMargin = getResources().getDimensionPixelSize(
//                R.dimen.vp_mc_screen_locker_big_margin_left);
        mScreenLocker.setTranslationX(getResources().getDimensionPixelSize(
              R.dimen.vp_mc_screen_locker_big_margin_left));
        mScreenLocker.setImageResource(R.drawable.screen_lock_icon_big);
//        mScreenLocker.setLayoutParams(params);
        mLeftMask.setVisibility(View.GONE);
        animateForLocker(true);
        mOrientationUpdater.disableRotation();
    }
    
    private void addSelectSourceView(){
        if(mCoreFragment != null && mCoreFragment.getUriLoader() instanceof OnlineLoader){
            if(mSelectSourceView == null){
                mSelectSourceView = (SelectSourceView)View.inflate(getContext(), R.layout.vp_select_source, null);
                mStatusBar.addCustomView(mSelectSourceView);
            }
            mSelectSourceView.attachOnlineLoader((OnlineLoader)mCoreFragment.getUriLoader());
            mSelectSourceView.attachVideoProxy(mVideoProxy);
            mSelectSourceView.setAnchor(mAnchor);
        }
    }

    public void attachMediaPlayer(CoreFragment fragment, MediaPlayerControl player){
        if(player != null && fragment != null){
            mCoreFragment = fragment;
            mPlayer = player;
            mMediaController.attachPlayer(mPlayer);
            mStatusBar.updateStatus(mCoreFragment.getVideoTitle(),  mCoreFragment.getVideoSubtitle());
            addSelectSourceView();
        }
    }

    public void showController(){
        if(!mIsShowing){
            mIsShowing = true;
            setVisibility(View.VISIBLE);
            clearDismissRunner();
            refreshViews();
            if(mIsScreenLocked){
                moveToState(STATE_LOCK_SCREEN);
                resetAutoDismiss();
            }else if(mIsVideoLoading){
                moveToState(STATE_LOADING);
            }else if(isAirkanPlaying()){
                moveToState(STATE_MILINK);
                resetAutoDismiss();
            }else{
                moveToState(STATE_FULL);
                resetAutoDismiss();
            }
            invalidate();
            bringToFront();
            requestLayout();
        }
    }
    
    private void refreshViews(){
        if(mCoreFragment != null){
            mMenu.setMenuActionListener(mCoreFragment);
            mMenu.setItems(mCoreFragment.getMenu());
            if(mCoreFragment.getUriLoader() != null && mCoreFragment.getUriLoader().hasNext()){
                mMediaController.setNextButtonVisible(true);
            }else{
                mMediaController.setNextButtonVisible(false);
            }
        }
    }
    
    private void moveToState(int state){
        clearAnimations();
        Log.d(TAG, "moveToState : " + state);
        switch(state){
        case STATE_IDLE :
            animateOut();
            break;
        case STATE_LOADING:
            mLeftLayout.setVisibility(View.GONE);
            mMenu.setVisibility(View.GONE);
            mMediaController.setVisibility(View.GONE);
            mAnimators.add(AnimatorFactory.animateInTopView(mStatusBar));
            break;
        case STATE_LOCK_SCREEN:
            mStatusBar.setVisibility(View.GONE);
            mMenu.setVisibility(View.GONE);
            mMediaController.setVisibility(View.GONE);
            mLeftLayout.setVisibility(View.VISIBLE);
            break;
        case STATE_MILINK:
            mLeftLayout.setVisibility(View.GONE);
            mAnimators.add(AnimatorFactory.animateInTopView(mStatusBar));
            mAnimators.add(AnimatorFactory.animateInBottomView(mMediaController));  
            mAnimators.add(AnimatorFactory.animateInRightView(mMenu));
            break;
        case STATE_FULL:
//            mStatusBar.setVisibility(View.VISIBLE);
            mAnimators.add(AnimatorFactory.animateInTopView(mStatusBar));
            mAnimators.add(AnimatorFactory.animateInBottomView(mMediaController));  
            mAnimators.add(AnimatorFactory.animateInLeftView(mLeftLayout));
            mAnimators.add(AnimatorFactory.animateInRightView(mMenu));
            break;
        }
    }

    public void hideController(){
        if(mIsShowing){
            mIsShowing = false;
            mUiHandler.removeCallbacks(mAutoDismiss);
            mUiHandler.removeCallbacks(mGoneRunner);
            mUiHandler.postDelayed(mGoneRunner, UIConfig.ANIMATE_DURATION);
            animateOut();
        }
    }
    
    private void clearDismissRunner(){
        mUiHandler.removeCallbacks(mGoneRunner);
        mUiHandler.removeCallbacks(mAutoDismiss);
    }

    private void resetAutoDismiss(){
        mUiHandler.removeCallbacks(mAutoDismiss);
        mUiHandler.postDelayed(mAutoDismiss, UIConfig.AUTO_DISMISS_TIMER);
    }
    
    public boolean isAirkanPlaying(){
        return mCoreFragment instanceof MilinkFragment;
    }

    public void toggleMe(){
        if(mIsShowing){
            hideController();
        }else{
            showController();
        }
    }

    private void adjustBrightness(float movementY){
        if(mIsScreenLocked){
            return;
        }
        if(mVolumn != null){
            mVolumn.hide();
        }
        if(mBrightness == null){
            mBrightness = GestureBrightness.create(mAnchor);
        }
        mBrightness.adjustBrightness(mActivity, movementY);
    }

    private void adjustVolumn(float movementY){
        if(mIsScreenLocked){
            return;
        }
        if(mBrightness != null){
            mBrightness.hide();
        }
        if(mVolumn == null){
            mVolumn = GestureVolumn.create(mAnchor);
        }
        mVolumn.adjustVolume(mActivity, movementY);
    }
    
    private void adjustSeekStart(float movementX){
        if(mIsScreenLocked){
            return;
        }
        if(mBrightness != null){
            mBrightness.hide();
        }
        if(mVolumn != null){
            mVolumn.hide();
        }
        if(mSeek == null){
            mSeek = GestureSeek.create(mAnchor, mMediaController);
        }
        mSeek.adjustSeekStart(movementX);
    }
    
    private void adjustSeekEnd(){
        if(mSeek != null){
            mSeek.adjustSeekEnd();
        }
    }

    private Runnable mAutoDismiss = new Runnable() {
        @Override
        public void run() {
            hideController();
        }
    };

    @Override
    public void onTouchMove(int region, float movementX, float movementY) {
        if(region == REGION_LEFT){
            adjustBrightness(movementY);
        }else if(region == REGION_RIGHT){
            adjustVolumn(movementY);
        }else if(region == REGION_CENTER){
            if(isSeekGestureEnable()){
                adjustSeekStart(movementX);
            }
        }
    }

    @Override
    public void onTouchUp(int region) {
        if(region == REGION_CENTER){
            if(isSeekGestureEnable()){
                adjustSeekEnd();
            }
        }
    }

    @Override
    public void onTap(int region) {
        if(region == REGION_LEFT){
            adjustBrightness(0);
        }else if(region == REGION_RIGHT){
            adjustVolumn(0);
        }else{
            if(mPlayer != null && mPlayer.canPause() && !mIsVideoLoading && 
                    !mPlayer.isAdsPlaying()){
                toggleMe();  
            }
        }
    }
    
    private boolean isSeekGestureEnable(){
        if(!mIsVideoLoading && mPlayer != null && mPlayer.canSeekForward() && !isShown() && 
                !mPlayer.isAdsPlaying()){
            return true;
        }
        return false;
    }

    private void clearAnimations(){
        for(Animator animator : mAnimators){
            animator.end();
        }
        mAnimators.clear();
    }

    private void animateForLocker(boolean isLocked){
        clearAnimations();
        if(isLocked){
            if(mStatusBar.getVisibility() == View.VISIBLE){
                mAnimators.add(AnimatorFactory.animateOutTopView(mStatusBar));
            }
            if(mMediaController.getVisibility() == View.VISIBLE){
                mAnimators.add(AnimatorFactory.animateOutBottomView(mMediaController));
            }
            if(mMenu.getVisibility() == View.VISIBLE){
                mAnimators.add(AnimatorFactory.animateOutRightView(mMenu));
            }
        }else{
            mAnimators.add(AnimatorFactory.animateInTopView(mStatusBar));
            mAnimators.add(AnimatorFactory.animateInRightView(mMenu));
            if(!mIsVideoLoading){
                mAnimators.add(AnimatorFactory.animateInBottomView(mMediaController));
            }
        }
    }

    private void animateOut(){
        clearAnimations();
        if(!mIsScreenLocked){
            if(mStatusBar.getVisibility() == View.VISIBLE){
                mAnimators.add(AnimatorFactory.animateOutTopView(mStatusBar));  
//                mStatusBar.setVisibility(View.GONE);
            }
            if(mMediaController.getVisibility() == View.VISIBLE){
                mAnimators.add(AnimatorFactory.animateOutBottomView(mMediaController));
            }
            if(mMenu.getVisibility() == View.VISIBLE){
                mAnimators.add(AnimatorFactory.animateOutRightView(mMenu));
            }
            if(mLeftLayout.getVisibility() == View.VISIBLE){
                mAnimators.add(AnimatorFactory.animateOutLeftView(mLeftLayout));
            }
        }else{
            mLeftLayout.setVisibility(View.GONE);
            mUiHandler.removeCallbacks(mGoneRunner);
            setVisibility(View.GONE);
        }
    }

    private Runnable mGoneRunner = new Runnable() {
        @Override
        public void run() {
            setVisibility(GONE);
        }
    };

}
