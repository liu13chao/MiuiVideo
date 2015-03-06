/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   GestureSeek.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-11-29
 */
package com.miui.videoplayer.widget;

import android.content.Context;
import android.widget.FrameLayout;

import com.miui.video.R;
import com.miui.video.util.TimeUtils;

/**
 * @author tianli
 *
 */
public class GestureSeek extends GestureView {

    private MediaController mController;

    public GestureSeek(Context context, MediaController mc) {
        super(context);
        mController = mc;
    }
    
    public static GestureSeek create(FrameLayout anchor, MediaController mc){
        GestureSeek view = new GestureSeek(anchor.getContext(), mc);
        view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 
                LayoutParams.MATCH_PARENT));
        anchor.addView(view);
        return view;
    }
    
    @Override
    protected int getIcon() {
        return R.drawable.vp_fas_forward_icon_big;
    }

    @Override
    protected int getIconMarginTop() {
        return getResources().getDimensionPixelSize(R.dimen.vp_volumn_icon_margin_top);
    }

    @Override
    protected int getTextMarginTop() {
        return getResources().getDimensionPixelSize(R.dimen.vp_volumn_percent_margin_top);
    }
    
    public void adjustSeekStart(float movementX){
        if(mController == null || mController.getDuration() == 0){
            return;
        }
        int duration = mController.getDuration();
        int currentPosition = mController.getCurrentPosition();
        int stepPosition = getStepPosition(movementX, duration);
        int seekToPosition = getSeekToPosition(stepPosition, currentPosition, 
                duration);
        mController.seekStepStart(seekToPosition);
        if(stepPosition >= 0){
            mIcon.setImageResource(R.drawable.vp_fas_forward_icon_big);
        }else{
            mIcon.setImageResource(R.drawable.vp_fas_back_icon_big);
        }
        updatePosition(seekToPosition, duration);
        show();
    }
    
    public void adjustSeekEnd(){
        if(mController == null){
            return;
        }
        mController.seekStepEnd();
        gone();
    }
    
//    public void seekStepStart(int seekPosition, int duration){
//        if (mPlayer.isPlaying()) {
//            pause();
//        }
//        int currentPosition = mPlayer.getCurrentPosition();
//        int duration = mPlayer.getDuration();
//        int seekToPosition = getSeekToPosition(stepPosition, videoViewWidth, 
//                currentPosition, duration);
//        if (currentPosition == seekToPosition) {
//        } else {
////          mSwitchMediaOrientation = 0;
//            sendSeekMesage(seekToPosition);
////          mSeekTime.updatePosition(seekToPosition);
//        }
//    }
    
    private int getSeekToPosition(int stepPosition, int currentPosition, 
            int duration) {
        int seekToPosition = 0;
        seekToPosition = currentPosition + stepPosition;
        if (stepPosition < 0) {
            if (seekToPosition < 0) {
                seekToPosition = 0;
            }
        } else {
            if (seekToPosition > duration) {
                seekToPosition = duration;
            }
        }
        return seekToPosition;
    }

    private void updatePosition(int seekPosition, int duration) {
        String seekString = TimeUtils.parseShortTime(seekPosition);
        String durationString = TimeUtils.parseShortTime(duration);
        mText.setText(seekString + " / " + durationString);
    }
    
    public void setOrientation(boolean forward) {
        if (forward) {
//          mOrientationImageView.setImageResource(R.drawable.vp_arrow_right_v5);
//            mOrientationImageView.setVisibility(View.VISIBLE);
//            mLeftOrientationImageView.setVisibility(View.INVISIBLE);
        } else {
//          mOrientationImageView.setImageResource(R.drawable.vp_arrow_right_v5);
//            mOrientationImageView.setVisibility(View.INVISIBLE);
//            mLeftOrientationImageView.setVisibility(View.VISIBLE);
        }
    }

    public int getStepPosition(float movementX, int duration){
//        int seekPosition = (int)(movementX / getResources().getDisplayMetrics().widthPixels
//                * duration);
        float seekStep = getResources().getDisplayMetrics().widthPixels / 120f;
//        Log.d("11111111111111", msg);
        int seekPosition = (int) (Math.abs(movementX) / seekStep * 1000) ;
//      Log.d("11111111111111", "seekPosition  " + seekPosition);
        if(movementX < 0){
            seekPosition = -seekPosition;
        }
        return seekPosition;
    }
}
