package com.miui.videoplayer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.*;
import android.content.DialogInterface.OnClickListener;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.WindowManager;
import com.miui.video.DKApp;
import com.miui.video.R;
import com.miui.video.model.AppSettings;
import com.miui.videoplayer.common.AndroidUtils;
import com.miui.videoplayer.fragment.VideoFragment;
import com.miui.videoplayer.playservice.PlayServiceDelegate;

public class VideoPlayerActivity extends Activity{
	private static final String TAG = VideoPlayerActivity.class.getSimpleName();

	private Intent mIntent;
	private AudioManager mAudioManager;
	private AudioFocusListener mAudioFocusListener = new AudioFocusListener();
	private boolean isScreenSaver = false;

    public static final String INTENT_KEY_BOOLEAN_SCREENSAVER = "screensaver";
    public static final String KEY_SHOW_PLAYER_WHEN_LOCKED = "ShowPlayerWhenLocked";
    
    private VideoFragment mController;

    public static final String ACTION_KILL = "screensaver-action-kill";
    private BroadcastReceiver mSuicideReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_KILL)) {
                finish();
            }
        }
    };
    
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

//	private Handler mHandler = new Handler();
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate: " + this);
		setContentView(R.layout.vp_activity_video_player);
		getWindow().setFormat(PixelFormat.TRANSLUCENT);
		mController = new VideoFragment();
		getFragmentManager().beginTransaction().add(R.id.video_player_container, 
				mController).commitAllowingStateLoss();
		getFragmentManager().executePendingTransactions();
		handleIntent();
		mAudioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
	}
	
	private void handleIntent(){
		mIntent = getIntent();
		Uri uri = mIntent.getData();
		boolean is_html = mIntent.getBooleanExtra("is_html", false);
		if (AndroidUtils.isOnlineVideo(uri) && AndroidUtils.isNetworkConncected(this) && 
				!AndroidUtils.isFreeNetworkConnected(this) &&
				DKApp.getSingleton(AppSettings.class).isOpenCellularPlayHint(this) && !is_html) {
			popupNoWifiAlertDialog(uri);
		}else{
			mController.playByIntent(mIntent);
		}
    	isScreenSaver = getIntent().getBooleanExtra(INTENT_KEY_BOOLEAN_SCREENSAVER, false);
        if (isScreenSaver) {
            Intent intent = new Intent();
            intent.setAction("com.miui.gallery.ACTION_SCREENSAVER");
            bindService(intent, mConnection, 0);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                    | WindowManager.LayoutParams.FLAG_FULLSCREEN
                    | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                    | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
                    | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

            IntentFilter filter = new IntentFilter(ACTION_KILL);
            registerReceiver(mSuicideReceiver, filter);
        }
        
        boolean mIsFromCameraAndLocked = getIntent().getBooleanExtra(KEY_SHOW_PLAYER_WHEN_LOCKED, false);
        if(mIsFromCameraAndLocked && !isScreenSaver){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                    | WindowManager.LayoutParams.FLAG_FULLSCREEN
                    | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                    | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
                    | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        }
	}
	
	private void popupNoWifiAlertDialog(final Uri videoUri) {
		final miui.app.AlertDialog dialog = new miui.app.AlertDialog.Builder(this, miui.R.style.Theme_Light_Dialog_Alert).create();
        String title   = getResources().getString(R.string.nowifi_alert_dialog_title);
        String message = getResources().getString(R.string.nowifi_alert_dialog_message);
        dialog.setMessage(message);
        dialog.setTitle(title);

		dialog.setCancelable(false);
		String continueString = this.getResources().getString(R.string.nowifi_alert_dialog_continue);
		String exitString = this.getResources().getString(R.string.nowifi_alert_dialog_exit);
		dialog.setButton(AlertDialog.BUTTON_POSITIVE, exitString, new OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int arg1) {
//				mAlertDialogShowing = false;
		 		dialog.dismiss();
		 		finish();
			}
		});
		dialog.setButton(AlertDialog.BUTTON_NEGATIVE, continueString, new OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int arg1) {
				dialog.dismiss();
				mController.playByIntent(mIntent);
			}
		});
		dialog.show();
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		Log.i(TAG, "onNewIntent: "+intent);
		super.onNewIntent(intent);
		setIntent(intent);
		handleIntent();
	}
	
	@Override
	protected void onStart() {
		Log.i(TAG, "onStart");
		super.onStart();
	}

	@Override
	protected void onResume() {
		Log.i(TAG, "onResume");
		super.onResume();
		mAudioManager.requestAudioFocus(mAudioFocusListener, 
		        AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
	}

	@Override
	protected void onPause() {
		Log.i(TAG, "onPause");
		mAudioManager.abandonAudioFocus(mAudioFocusListener);
		super.onPause();
	}

	@Override
	protected void onStop() {
		Log.i(TAG, "onStop: " + this);
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
		Log.i(TAG, "onDestroy Start");
		PlayServiceDelegate.getDefault(this).stopService();
		if (isScreenSaver) {
			try {
				unregisterReceiver(mSuicideReceiver);
			}
			catch (Exception e) {
				Log.e(TAG, "fail to unregister receiver!");
			}
			unbindService(mConnection);
		}
		Log.i(TAG, "Super onDestroy start");
		super.onDestroy();
		Log.i(TAG, "Super onDestroy end");	
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if(isScreenSaver){
			if(event.getAction() == KeyEvent.ACTION_DOWN){
				if(event.getKeyCode() != KeyEvent.KEYCODE_VOLUME_DOWN &&
						event.getKeyCode() != KeyEvent.KEYCODE_VOLUME_UP){
					Intent intent = new Intent(Intent.ACTION_MAIN);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.addCategory(Intent.CATEGORY_HOME);
					startActivity(intent);
					finish();
					return true;
				}
			}
		}
		if(event.getAction() == KeyEvent.ACTION_DOWN){
		    boolean handled = mController.onKeyDown(event);
		    if(handled){
		        return true;
		    }
		}
		return super.dispatchKeyEvent(event);
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if(isScreenSaver){
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.addCategory(Intent.CATEGORY_HOME);
			startActivity(intent);
			finish();
			return true;
		}
		return super.dispatchTouchEvent(ev);
	}

	public static class AudioFocusListener implements OnAudioFocusChangeListener{
        @Override
        public void onAudioFocusChange(int arg0) {
        }
	}
}
