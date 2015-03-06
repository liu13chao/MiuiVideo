/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   MediaController.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-6-20
 */

package com.miui.videoplayer.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.miui.video.R;
import com.miui.videoplayer.common.DKTimeFormatter;
import com.miui.videoplayer.fragment.VideoProxy;
import com.miui.videoplayer.media.MediaPlayerControl;

/**
 * @author tianli
 *
 */
public class MediaController extends RelativeLayout {

	public static final String TAG = "MediaController";
	
	private MediaPlayerControl mPlayer;
	private ImageView mPauseButton;
	private ImageView mNextButton;
	private SeekBar mSeekBar;
	private TextView mEndTime, mCurrentTime;
		
	private boolean mIsSeeking = false;
	
	private Handler mHandler = new Handler(Looper.getMainLooper());
	
    private VideoProxy mVideoProxy;
	public boolean mVideoPause = false;
	
	private OnSeekEventListener mSeekEventListener;
	private OnPauseOrStartListener mOnPauseOrStartListener;
	
	private int mCachedSeekPosition = -1;
	
	/* Airkan */
//	private AirkanManager mAirkanManager;
//	private ImageView mAirKanButton;
//	private AirKanDevicesPopupWindow mAirkanPopup;
	
	public MediaController(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public MediaController(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public MediaController(Context context) {
		super(context);
		init();
	}
	
	private void init(){
		this.setFocusable(true);
		this.setFocusableInTouchMode(true);
	}
	
	public void attachPlayer(MediaPlayerControl player){
		mPlayer = player;
	}
	
    public void attachVideoProxy(VideoProxy mVideoProxy) {
        this.mVideoProxy = mVideoProxy;
    }
    
    public int getCurrentPosition(){
        if(mCachedSeekPosition >= 0){
            return mCachedSeekPosition;
        }else if(mPlayer != null){
            return mPlayer.getCurrentPosition();
        }
        return 0;
    }
    
    public int getDuration(){
        if(mPlayer != null){
            return mPlayer.getDuration();
        }
        return 0;
    }
	
	@Override
	public void onFinishInflate() {
		mPauseButton = (ImageView)findViewById(R.id.pause);
		mPauseButton.setOnClickListener(mClickListener);
		mNextButton = (ImageView)findViewById(R.id.next);
		mNextButton.setOnClickListener(mClickListener);
		mSeekBar = (SeekBar)findViewById(R.id.mediacontroller_progress);
		mSeekBar.setOnSeekBarChangeListener(mSeekListener);
		mSeekBar.setMax(1000);
		mEndTime = (TextView)findViewById(R.id.time);
		mCurrentTime = (TextView)findViewById(R.id.time_current);
	}
	
	public void togglePause(){
	    if(mPlayer == null){
	        return;
	    }
	    if(mVideoPause){
	        start();
	        if(mOnPauseOrStartListener != null){
	            mOnPauseOrStartListener.onPauseStart();
	        }
	    }else{
	        pause();
	        if(mOnPauseOrStartListener != null){
	            mOnPauseOrStartListener.onPauseEnd();
	        }
		}
	}
	
	public OnSeekEventListener getSeekEventListener() {
		return mSeekEventListener;
	}

	public void setSeekEventListener(OnSeekEventListener seekEventListener) {
		this.mSeekEventListener = seekEventListener;
	}
	
	public void setNextButtonVisible(boolean visible){
	    mNextButton.setVisibility(visible ? View.VISIBLE : View.GONE);
	}
	
	public void pause(){
	    if(mPlayer == null){
	        return;
	    }
	    Log.d(TAG, "pause");
	    mVideoPause = true;
	    mPlayer.pause();
	    updatePlayingState();
	}
	
	public void start(){
	    if(mPlayer == null){
	        return;
	    }
	    Log.d(TAG, "start");
	    mVideoPause = false;
	    mPlayer.start();
	    updatePlayingState();
	}
	
	private void updatePlayingState() {
		if (!mVideoPause) {
			mPauseButton.setImageResource(R.drawable.vp_mc_pause);
		} else {
			mPauseButton.setImageResource(R.drawable.vp_mc_play);
		}
	}
	
	public void seekStepStart(int seekTo){
	    if(mPlayer == null){
	        return;
	    }
	    if(!mIsSeeking){
	        checkPlayingState();
	    }
	    mIsSeeking = true;
	    sendSeekMesage(seekTo);
	}
	
	public void seekStepEnd(){
		if(mPlayer == null){
			return;
		}
        mIsSeeking = false;
		if(!mVideoPause){
		    start();		    
		}
	}
	
	private void sendSeekMesage(int seekTo){
//	    mCachedSeekPosition = seekTo;
//	    mHandler.removeCallbacks(mSeekRunner);
//	    mHandler.postDelayed(mSeekRunner, 0);
//	    mHandler.removeMessages(UIConfig.MSG_WHAT_VIDEO_SEEK);
//	    Message msg = Message.obtain();
//	    msg.what = UIConfig.MSG_WHAT_VIDEO_SEEK;
//	    msg.arg1 = mCachedSeekPosition;
//	    mHandler.sendMessage(msg);
		Log.d(TAG, "seekTo " + seekTo);
		if(seekTo <= mPlayer.getDuration()){
		      mPlayer.seekTo(seekTo);
		}
	}
	
	private Runnable mSeekRunner = new Runnable() {
        @Override
        public void run() {
            if(mPlayer == null){
                return;
            }
            if(mCachedSeekPosition >= 0 ){
                mPlayer.seekTo(mCachedSeekPosition);
            }
            mCachedSeekPosition = -1;
        }
    };
	
	private View.OnClickListener mClickListener = new View.OnClickListener() {
		public void onClick(View v) {
		    if(v == mPauseButton){
		        togglePause();
		    }else if(v == mNextButton){
		        if(mVideoProxy != null){
		            mVideoProxy.playNext();
		        }
		    }
		}
	};
	
	// There are two scenarios that can trigger the seekbar listener to trigger:
	// The first is the user using the touchpad to adjust the posititon of the
	// seekbar's thumb. In this case onStartTrackingTouch is called followed by
	// a number of onProgressChanged notifications, concluded by
	// onStopTrackingTouch.
	// We're setting the field "mDragging" to true for the duration of the
	// dragging
	// session to avoid jumps in the position in case of ongoing playback.
	//
	// The second scenario involves the user operating the scroll ball, in this
	// case there WON'T BE onStartTrackingTouch/onStopTrackingTouch
	// notifications,
	// we will simply apply the updated position without suspending regular
	// updates.
	private OnSeekBarChangeListener mSeekListener = new OnSeekBarChangeListener() {
		public void onStartTrackingTouch(SeekBar bar) {
			if(mPlayer == null){
				// ignore when mPlayer is not ready.
				return;
			}
			if(!mIsSeeking){
		         checkPlayingState();
			}
			mIsSeeking = true;
			if(mSeekEventListener != null){
				mSeekEventListener.onSeekStart();
			}
		}

		public void onProgressChanged(SeekBar bar, int progress,
				boolean fromuser) {
			if (!fromuser) {
				// We're not interested in programmatically generated changes to
				// the progress bar's position.
				return;
			}
			if(mPlayer == null || !mPlayer.canSeekBackward() || !mPlayer.canSeekForward()
					|| mPlayer.getDuration() <= 0){
				// seek can not be supported.
				return;
			}
			long duration = mPlayer.getDuration();
			long newposition = (duration * progress) / 1000L;
			sendSeekMesage((int) newposition);
			if (mCurrentTime != null){
				mCurrentTime.setText(DKTimeFormatter.getInstance().stringForTime((int) newposition));
			}
			if(mSeekEventListener != null){
				mSeekEventListener.onSeeking();
			}
		}

		public void onStopTrackingTouch(SeekBar bar) {
		    mSeekRunner.run();
		    if(!mVideoPause){
		        start();            
		    }
		    mIsSeeking = false;
			mHandler.removeCallbacks(mUpdateProgressRunner);
			mHandler.post(mUpdateProgressRunner);
			if(mSeekEventListener != null){
				mSeekEventListener.onSeekEnd();
			}
		}
	};
	
	private int setProgress() {
		if (mPlayer == null || mIsSeeking) {
			return 0;
		}
		int position = mPlayer.getCurrentPosition();
		int duration = mPlayer.getDuration();
		if (duration <  0) {
			return 0;
		}
		if (mSeekBar != null) {
			long pos = 0;
			if (duration > 0) {
				// use long to avoid overflow
				pos = 1000L * position / duration;
				mSeekBar.setProgress((int) pos);
			}
			int percent = mPlayer.getBufferPercentage();
			mSeekBar.setSecondaryProgress(percent * 10);
		}
		if (duration == 0) {
			return position;
		}
		if (mEndTime != null){
			mEndTime.setText(DKTimeFormatter.getInstance().stringForTime(duration));
		}
		if (mCurrentTime != null){
			mCurrentTime.setText(DKTimeFormatter.getInstance().stringForTime(position));
		}
		return position;
	}
	
	private Runnable mUpdateProgressRunner = new Runnable() {
		@Override
		public void run() {
			mHandler.removeCallbacks(mUpdateProgressRunner);
			if(mPlayer != null){
				if (!mIsSeeking && mPlayer.isPlaying()) {
					int pos = setProgress();
					mHandler.postDelayed(mUpdateProgressRunner, 1000 - (pos % 1000));
				}else{
					mHandler.postDelayed(mUpdateProgressRunner, 1000);
				}
			}
		}
	};
	
	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		mHandler.removeCallbacks(mUpdateProgressRunner);
		mHandler.post(mUpdateProgressRunner);
//		if(mAirkanManager != null){
//			mAirkanManager.registeOnDeviceChangeListener(mAirkanDeviceListener);
//		}
//		checkAirkan();
	}
	
	private void checkPlayingState(){
        if(mPlayer != null && !mPlayer.isPlaying()){
            mVideoPause = true;
        }else{
            mVideoPause = false;
        }
	}

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if(changedView == this){
            if(visibility == VISIBLE){
                checkPlayingState();
                updatePlayingState();
                mHandler.removeCallbacks(mUpdateProgressRunner);
                mHandler.post(mUpdateProgressRunner);
            }else{
                mHandler.removeCallbacks(mUpdateProgressRunner);
            }
        }
    }

    @Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		mHandler.removeCallbacks(mUpdateProgressRunner);
	}

//	public static class ControllerCenter extends SafeHandler<MediaController>{
//		
//		public ControllerCenter(MediaController controller) {
//			super(controller);
//		}
//
//		@Override
//		public void handleMessage(Message msg) {
//			final MediaController controller = getReference();
//			if(controller == null){
//				return;
//			}
//			MediaPlayerControl player = controller.mPlayer;
//			if(player == null){
//				return;
//			}
//			switch (msg.what) {
//			case UIConfig.MSG_WHAT_VIDEO_SEEK:
//				player.seekTo(msg.arg1);
//				break;
//			default:
//				break;
//			}
//			super.handleMessage(msg);
//		}
//	}
	
	public static interface OnSeekEventListener{
		public void onSeekStart();
		public void onSeeking();
		public void onSeekEnd();
	}
	
	public void setOnPauseOrStartListener(OnPauseOrStartListener listener){
		mOnPauseOrStartListener = listener;
	}
	
	public interface OnPauseOrStartListener{
		public void onPauseStart();
		public void onPauseEnd();
	}
}
