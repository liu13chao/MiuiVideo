package com.miui.videoplayer;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.miui.videoplayer.dialog.NoWifiAlertDialog;
import com.miui.videoplayer.framework.DuoKanCodecConstants;
import com.miui.videoplayer.framework.airkan.AirkanManager.AirkanExistDeviceInfo;
import com.miui.videoplayer.framework.history.PlayHistoryManager;
import com.miui.videoplayer.framework.history.PlayHistoryManager.PlayHistoryEntry;
import com.miui.videoplayer.framework.ui.DuoKanMediaController;
import com.miui.videoplayer.framework.utils.AndroidUtils;
import com.miui.videoplayer.views.DuoKanCodecVideoView;
import com.miui.videoplayer.views.DuokanVideoView;
import com.miui.videoplayer.views.ITempVideoView;
import com.miui.video.R;
import com.miui.video.type.PlayerMediaSetInfo;

public class VideoPlayerActivity extends BaseActivity implements OnAudioFocusChangeListener{
	private static final String TAG = VideoPlayerActivity.class.getSimpleName();
	private ITempVideoView mVideoView;
	private VideoPlayState mVideoPlayState;
	
	private Intent mIntent;
	private AudioManager mAudioManager;
	
	private PhoneStateBroadcastReceiver mPhoneStateBroadcastReceiver;
	private static AsyncGetMediaSetInfoTask mGetMediaSetInfoTask = null;

	public static PlayHistoryManager PLAY_HISTORY_MANAGER;

	public static boolean isVideoComplete = false;
	public static boolean isVideoPaused = false;
	public static boolean isActivityPaused = false;
	public static boolean isScreenSaver = false;
	public static int videoPausedPosition = 0;

	public static int mediaId = -1;
	public static int curCi = -1;
	public static int nextCi = -1;
	public static int curMediaIndex = -1;
	public static int nextMediaIndex = -1;
	public static int mediaCount = -1;
	public static int curMediaSource = -1;
	public static int curClarity = -1;
	public static String mediaTitle = null;
	public static String mediaSubTitle = null;
	public static String curMediaHtml5Url = null;
	public static PlayerMediaSetInfo[] mediaSetInfo = null;
	public static int mediaStyle = Constants.MEDIA_TYPE_TELEPLAY;//0 tv, 1 variety 
	
	private boolean mDirectAirkan = false;

    public static final String INTENT_KEY_BOOLEAN_SCREENSAVER = "screensaver";

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

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate: " + this);
		isActivityPaused = false;
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		setContentView(R.layout.vp_app_activity_video_player);

		setupViews();

		init();
		mVideoView.onActivityCreate();
		
		mAudioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
		mPhoneStateBroadcastReceiver = new PhoneStateBroadcastReceiver();

	}

	private void init() {
		mIntent = getIntent();
		Uri uri = mIntent.getData();
		mediaId = mIntent.getIntExtra(Constants.MEDIA_ID, -1);
		curClarity = mIntent.getIntExtra(Constants.MEDIA_CLARITY, -1);
		curCi = mIntent.getIntExtra(Constants.CURRENT_EPISODE, -1);
		curMediaIndex = mIntent.getIntExtra(Constants.INTENT_KEY_INT_PLAY_INDEX, 0);
		mediaCount = mIntent.getIntExtra(Constants.AVAILABLE_EPISODE_COUNT, 0);
		curMediaSource = mIntent.getIntExtra(Constants.MEDIA_SOURCE, -1);
		curMediaHtml5Url = mIntent.getStringExtra(Constants.MEDIA_HTML5_URL);
		mediaTitle = mIntent.getStringExtra(Constants.INTENT_KEY_STRING_MEDIA_TITLE);
		mediaSubTitle = mIntent.getStringExtra(Constants.MEDIA_SUBTITLE);
		mediaStyle = mIntent.getIntExtra(Constants.MEDIA_SET_STYLE, Constants.MEDIA_TYPE_VARIETY);
		String[] uris = mIntent.getStringArrayExtra(Constants.INTENT_KEY_STRING_ARRAY_URI_LIST);
//		mediaSetInfo = (PlayerMediaSetInfo[]) mIntent.getSerializableExtra(Constants.MEDIA_SETINFO);
		mediaSetInfo = null;
		Log.i(TAG, " mediaId: " + mediaId + " curCi: " + curCi  + " curMediaIndex: " + curMediaIndex + 
				" mediaCount: " + mediaCount);

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

		if (uris != null) {
			//local media list
			curMediaIndex = 0;
			mediaCount = uris.length;
		} else if ((mediaId > 0)&&(mediaCount > 0)) {
			LoadMediaSetInfos(this, null);
			uris = new String[mediaCount];
		} else {
			uris = new String[1];
		}
		if (curMediaIndex >= mediaCount) {
			curMediaIndex = 0;
		}
		if (uri != null) {
			uris[curMediaIndex] = uri.toString();
		}
		isVideoPaused = false;
		videoPausedPosition = 0;
		
		workaroundForNoPermissionContentVideo(uris);
		
		mDirectAirkan = mIntent.getBooleanExtra(Constants.INTENT_KEY_BOOLEAN_DIRECT_AIRKAN, false);
		String existDeviceName = mIntent.getStringExtra(Constants.INTENT_KEY_STRING_DEVICE_NAME);
		if (mDirectAirkan && existDeviceName != null) {
			AirkanExistDeviceInfo airkanExistDeviceInfo = new AirkanExistDeviceInfo(existDeviceName);
			mVideoView.setInput(uris[curMediaIndex], this, airkanExistDeviceInfo);
			mVideoView.setDirectAirkanUri(Uri.parse(uris[curMediaIndex]));
		} else {
			mVideoView.setInput(uris, curMediaIndex, this);
			Uri videoUri = Uri.parse(uris[curMediaIndex]);
			if (AndroidUtils.isOnlineVideo(videoUri) && AndroidUtils.isNetworkConncected(this) && !AndroidUtils.isFreeNetworkConnected(this)) {
				popupNoWifiAlertDialog(videoUri);
			} else {
				mVideoView.setVideoURI(videoUri);
			}
		}

		String title = mediaTitle;
		Log.i(TAG, "media title :" + title + " subtitle :" + mediaSubTitle);
		if (title != null) {
			PlayHistoryEntry entry = new PlayHistoryEntry(uris[curMediaIndex]);
			entry.setVideoName(title);
			Map<String, PlayHistoryEntry> metaDataMap = new HashMap<String, PlayHistoryEntry>();
			metaDataMap.put(uris[curMediaIndex].toString(), entry);
			Log.i(TAG, "put url: " + uris[curMediaIndex]);
			mVideoView.setTitleMap(metaDataMap);
		}

		if (!isScreenSaver) {
			PLAY_HISTORY_MANAGER = new PlayHistoryManager(this);
			PLAY_HISTORY_MANAGER.load();
		}
	}

	private boolean mAlertDialogShowing = false;
	private void popupNoWifiAlertDialog(final Uri videoUri) {	
		final NoWifiAlertDialog dialog = new NoWifiAlertDialog(this);
		dialog.setCancelable(false);
		String continueString = this.getResources().getString(R.string.nowifi_alert_dialog_continue);
		String exitString = this.getResources().getString(R.string.nowifi_alert_dialog_exit);
		
		dialog.setButton(AlertDialog.BUTTON_POSITIVE, exitString, new OnClickListener(){

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				mAlertDialogShowing = false;
		 		dialog.dismiss();
		 		VideoPlayerActivity.this.finish();
			}
			
		});
		
		dialog.setButton(AlertDialog.BUTTON_NEGATIVE, continueString, new OnClickListener(){

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				mAlertDialogShowing = false;
				dialog.dismiss();
				mVideoView.setVideoURI(videoUri);
				
				mVideoView.start();
			}
			
		});
		dialog.show();
		mAlertDialogShowing = true;
	}
	
	public boolean isDirectAirkan() {
		return mDirectAirkan;
	}
	
	private void workaroundForNoPermissionContentVideo(String[] uris) {
		if (DuoKanCodecConstants.sUseDuokanCodec != DuoKanCodecConstants.IS_DUOKAN_CODEC_PHONE) {
			DuoKanCodecConstants.sUseDuokanCodec = DuoKanCodecConstants.IS_DUOKAN_CODEC_PHONE;
			Log.i(TAG, "used origin codec, now back to duokan codec");
			chooseCodecVideoView();
			((View) mVideoView).setVisibility(View.VISIBLE);
		}
		if (!DuoKanCodecConstants.sUseDuokanCodec) {
			return;
		}
		if (uris == null || uris.length != 1) {
			return;
		}
		String uriString = uris[curMediaIndex];
		Uri uri = Uri.parse(uriString);
		if (uri == null) {
			return;
		}
		String scheme = uri.getScheme();
		if (scheme == null || !scheme.equals("content")) {
			return;
		}
		String realPath = getRealFilePathFromContentUri(this, uri);
		if (realPath == null) {
			return;
		}
		File file = new File(realPath);
		boolean canRead = file.canRead();
		boolean exist = file.exists();
		
		if (!exist || !canRead) {
			Log.i(TAG, "does not exist or can not read uri: " + uriString);
			mVideoView = (ITempVideoView) this.findViewById(R.id.video_view);
			((View) mVideoView).setVisibility(View.VISIBLE);
			DuoKanCodecVideoView duokanCodecVideoView = (DuoKanCodecVideoView) this.findViewById(R.id.duokan_codec_video_view);
			duokanCodecVideoView.setVisibility(View.GONE);
			DuoKanCodecConstants.sUseDuokanCodec = false;
		}
	}

	private void chooseCodecVideoView() {
		if (DuoKanCodecConstants.IS_DUOKAN_CODEC_PHONE) {
			mVideoView = (ITempVideoView) this.findViewById(R.id.duokan_codec_video_view);
			DuokanVideoView duokanVideoView = (DuokanVideoView) this.findViewById(R.id.video_view);
			duokanVideoView.setVisibility(View.GONE);
		} else {
			mVideoView = (ITempVideoView) this.findViewById(R.id.video_view);
			DuoKanCodecVideoView duokanCodecVideoView = (DuoKanCodecVideoView) this.findViewById(R.id.duokan_codec_video_view);
			duokanCodecVideoView.setVisibility(View.GONE);
		}
	}

	private String getRealFilePathFromContentUri(Context context, Uri contentUri) {
    	String[] columns = new String[]{MediaStore.Video.Media.DATA};
    	Cursor cursor = null;
    	try {
	    	cursor = context.getContentResolver().query(contentUri, columns, null, null, null);
	    	if (cursor == null) {
	    		return null;
	    	}
	    	int index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
	    	cursor.moveToFirst();
	    	String result = cursor.getString(index);
	    	cursor.close();
	    	return result;
		} catch (Exception e) {
			e.printStackTrace();
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
			return null;
		}
    }    
	
	private void setupViews() {
		mVideoPlayState = new VideoPlayState();
        chooseCodecVideoView();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		Log.i(TAG, "onNewIntent: "+intent);
		super.onNewIntent(intent);
		mVideoPlayState.isActivityFinish = true;
		mVideoView.onNewIntent();
		setIntent(intent);
		init();
		mVideoView.onActivityCreate();
	}

	
	@Override
	protected void onStart() {
		Log.i(TAG, "onStart");
		super.onStart();
		isActivityPaused = false;
		mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
		if (!mVideoPlayState.isActivityFinish && !mRinging) {
			int videoViewWidth = mVideoView.getAdjustWidth();
			int videoViewHeight = mVideoView.getAdjustHeight();
			if ((videoViewWidth == 0 || videoViewWidth == -1) && (videoViewHeight == 0 || videoViewHeight == -1)) {
				mVideoView.adjustVideoPlayViewSize(-1, -1, true);
			} else {
				mVideoView.adjustVideoPlayViewSize(videoViewWidth, videoViewHeight, false);
			}

			mVideoPlayState.isActivityFinish = false;
		}
		mVideoView.onActivityStart();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("android.intent.action.PHONE_STATE");
		this.registerReceiver(mPhoneStateBroadcastReceiver, intentFilter);
		Log.i(TAG, "on start video is pause:"+mVideoPlayState.paused);
	}

	@Override
	protected void onResume() {
		Log.i(TAG, "onResume");
		isActivityPaused = false;
		if (!mAlertDialogShowing) {
			Log.i(TAG, "onResume ,setVideoURI " + mVideoPlayState.url + " seekto " + mVideoPlayState.position);

			if (!isVideoPaused) {
				mVideoView.start();
			}
		}
		super.onResume();
	}

	@Override
	protected void onPause() {
		Log.i(TAG, "onPause");
		isActivityPaused = true;
		mVideoPlayState.url = mVideoView.getPlayingUri();
		if (!mRinging)
			mVideoPlayState.position = mVideoView.getCurrentPosition();
		mVideoPlayState.paused = mVideoView.isPaused();
		videoPausedPosition = mVideoView.getCurrentPosition();
		Log.i(TAG, "video time before pause:"+videoPausedPosition);
		mAudioManager.abandonAudioFocus(this);
		mVideoView.pause();
		mVideoView.onActivityPause();

		super.onPause();
	}

	@Override
	protected void onStop() {
		Log.i(TAG, "onStop: " + this);
		isActivityPaused = true;
		mVideoPlayState.isActivityFinish = this.isFinishing();
		mVideoView.onActivityStop();
		if (mPhoneStateBroadcastReceiver != null) {
			this.unregisterReceiver(mPhoneStateBroadcastReceiver);
		}

		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
		Log.i(TAG, "onDestroy Start");
		if (isScreenSaver) {
        		try {
           			 unregisterReceiver(mSuicideReceiver);
        		}
        		catch (Exception e) {
            			Log.e(TAG, "fail to unregister receiver!");
        		}
        		unbindService(mConnection);
		}
		mVideoView.onActivityDestroy();
		Log.i(TAG, "Super onDestroy start");
		super.onDestroy();
		Log.i(TAG, "Super onDestroy end");	
	}
	
	private static class VideoPlayState {
		boolean isActivityFinish = true;
		int position = 0;
		boolean paused = false;
		String url = "";
	}
    
	@Override
	public void onAudioFocusChange(int focusChange) {
	
	}

	private boolean  mRinging = false;
	private class PhoneStateBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(TAG, "PhoneStateBroadcastReceiver intent action : " + intent.getAction());
			TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
			tm.listen(new PhoneStateListener(){

				@Override
				public void onCallStateChanged(int state, String incomingNumber) {
					super.onCallStateChanged(state, incomingNumber);
					switch (state) {
						case TelephonyManager.CALL_STATE_IDLE :
							Log.i(TAG, "TelephonyManager CALL_STATE_IDLE, ring: " + mRinging);
							
							if (mRinging) {
								mRinging = false;
								if (mVideoView != null) {
									Log.i(TAG, "CALL_STATE_IDLE :start play, seek to position: " + mVideoPlayState.position);
									mVideoView.seekTo(mVideoPlayState.position);
									mVideoView.start();
								}
							}
							break;
						case TelephonyManager.CALL_STATE_OFFHOOK :
							Log.i(TAG, "TelephonyManager CALL_STATE_OFFHOOK");
							break;
						case TelephonyManager.CALL_STATE_RINGING :
							Log.i(TAG, "TelephonyManager CALL_STATE_RINGING");
							if (!mRinging) {
								if (mVideoView != null && mVideoView.isPlaying()) {
									Log.i(TAG, "CALL_STATE_RINGING : pause ");
									mVideoPlayState.position = mVideoView.getCurrentPosition();
									mVideoView.pause();
								}
								mRinging = true;
							}
							break;
					}
				}

			}, PhoneStateListener.LISTEN_CALL_STATE);
		}
	}
//	
	public static boolean isShowCiSelect() {
		if (mediaSetInfo == null || mediaSetInfo.length < 2) {
			return false;
		}
		return true;
	}
	
	public static boolean isShowNextCi() {
		if (mediaSetInfo == null || mediaSetInfo.length < 2 || 
				curMediaIndex >= mediaCount - 1) {
			return false;
		}
		return true;
	}
	
	public static boolean isShowCiDownload() {
		if (mediaSetInfo == null || mediaSetInfo.length == 0 || VideoPlayerActivity.curMediaIndex >= mediaSetInfo.length ||
				mediaSetInfo[VideoPlayerActivity.curMediaIndex].ci_available_download_source == null ||
					mediaSetInfo[VideoPlayerActivity.curMediaIndex].ci_available_download_source.length == 0 ||
					mediaSetInfo[VideoPlayerActivity.curMediaIndex].offlineStatus != Constants.OFFLINE_NONE) {
			return false;
		}
		return true;
	}
	
	public static PlayerMediaSetInfo getMediaSetInfo(int position) {
		if (mediaSetInfo == null || position < 0 || position >= mediaSetInfo.length) {
			return null;
		}
		return mediaSetInfo[position];
	}
	
	public static int getCi(int index) {
		//tmp, need to fix
		if (mediaSetInfo == null || index < 0 || index >= mediaSetInfo.length) {
			return -1;
		}
		return mediaSetInfo[index].ci;
	}
	
	public static void LoadMediaSetInfos(Context context, Handler handler) {
		Log.i(TAG, "getMediaSetInfos");
		if (mGetMediaSetInfoTask != null) {
			mGetMediaSetInfoTask.cancel(true);
		}
		mGetMediaSetInfoTask = new AsyncGetMediaSetInfoTask(handler);
		mGetMediaSetInfoTask.execute(context);
	}
	
	public static class AsyncGetMediaSetInfoTask extends AsyncTask<Context, Void, Void> {
		
		private Handler mHandler = null;
		
		public AsyncGetMediaSetInfoTask(Handler handler) {
			mHandler = handler;
		}
		
		@Override
		protected Void doInBackground(Context... param) {
			// TODO Auto-generated method stub
			// TODO Auto-generated method stub
			Log.i(TAG, "AsyncGetMediaSetInfoTask doInBackground");
			ContentResolver cr = param[0].getContentResolver();
			Cursor cursor = null;
			try {
				String contentUriStr = Constants.CONTENT_MEDIAINFO_URI.toString() + "?"
									+ Constants.MEDIA_ID + "=" + VideoPlayerActivity.mediaId;
				Uri contentUri = Uri.parse(contentUriStr);
				Log.i(TAG, "contentUri: " + contentUri);
				cursor = cr.query(contentUri, null, Constants.MEDIA_ID + "=?",
						new String[]{String.valueOf(VideoPlayerActivity.mediaId)}, null);

				if (cursor != null && cursor.getCount() > 0) {
					int count = cursor.getCount();
					PlayerMediaSetInfo[] mediaSetInfo = new PlayerMediaSetInfo[count];
					cursor.moveToFirst();
					for (int i = 0; i < count; i++) {
						mediaSetInfo[i] = new PlayerMediaSetInfo();
						mediaSetInfo[i].ci = cursor.getInt(cursor.getColumnIndex(Constants.CURRENT_EPISODE));
						mediaSetInfo[i].date = cursor.getString(cursor.getColumnIndex(Constants.MEDIA_DATE));
						mediaSetInfo[i].videoname = cursor.getString(cursor.getColumnIndex(Constants.MEDIA_NAME));
						mediaSetInfo[i].offlineStatus = cursor.getInt(cursor.getColumnIndex(Constants.OFFLINE_STATUS));
						mediaSetInfo[i].ci_available_download_source = string2IntArray(
								cursor.getString(cursor.getColumnIndex(Constants.OFFLINE_SOURCE)));
						cursor.moveToNext();
					}
					VideoPlayerActivity.mediaSetInfo = mediaSetInfo;
					VideoPlayerActivity.mediaCount = mediaSetInfo.length;
					if (mHandler != null) {
						mHandler.sendEmptyMessage(DuoKanMediaController.MEDIASETINFO_UPDATED);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (cursor != null)
				try {
					cursor.close();
				} catch (Exception e2) {
					// TODO: handle exception
				}
			}
			return null;
		}
		
		private int[] string2IntArray(String source) {
			if (source == null || source.length() == 0) {
				return null;
			}
			String[] data = source.split("#");
			int[] ret = new int[data.length];
			for (int i=0; i<ret.length; i++) {
				ret[i] = Integer.parseInt(data[i]);
			}
			return ret;
		}
	}
}
