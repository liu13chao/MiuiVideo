package com.miui.video.mipush;

import java.util.List;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.miui.video.DKApp;
import com.miui.video.util.DKLog;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.xiaomi.mipush.sdk.MiPushCommandMessage;
import com.xiaomi.mipush.sdk.MiPushMessage;
import com.xiaomi.mipush.sdk.PushMessageReceiver;
import com.xiaomi.miui.pushads.sdk.NotifyAdsManagerNew;

public class MediaPushMessageReceiver extends PushMessageReceiver {
	
	private String TAG = MediaPushMessageReceiver.class.getName();
	
	private final String FROM_SERVER = "notification from online video server";
	private Handler mHandler = new Handler(Looper.getMainLooper());
	
	//command result
	private long mResultCode = -1;
    private String mTopic;
    private String mRegId;
    private String mReason;
	
	//客户端向服务器发送命令后的返回响应
	@Override
	public void onCommandResult(Context context, MiPushCommandMessage message) {
            DKLog.d(TAG, "command result");
            if(message == null || NotifyAdsManagerNew.getInstance() == null){
                return;
            }
	    mResultCode = message.getResultCode();
            mReason = message.getReason();
            String command = message.getCommand();
            List<String> arguments = message.getCommandArguments();
            if(arguments != null) {
                if(MiPushClient.COMMAND_REGISTER.equals(command)
                    && arguments.size() == 1) {
                    mRegId = arguments.get(0);
                    NotifyAdsManagerNew.getInstance().onInitializeResult(mResultCode, mReason, mRegId);
                } else if((MiPushClient.COMMAND_SET_ALIAS .equals(command)
                    || MiPushClient.COMMAND_UNSET_ALIAS.equals(command))
                    && arguments.size() == 1) {
                } else if((MiPushClient.COMMAND_SUBSCRIBE_TOPIC.equals(command))
                    && arguments.size() == 1) {
                    mTopic = arguments.get(0);
                    NotifyAdsManagerNew.getInstance().onSubscribeResult(mResultCode, mReason, mTopic);
                } else if((MiPushClient.COMMAND_UNSUBSCRIBE_TOPIC.equals(command))
                    && arguments.size() == 1) {
                    mTopic = arguments.get(0);
                    NotifyAdsManagerNew.getInstance().onUnsubscribeResult(mResultCode, mReason, mTopic);
                } else if(MiPushClient.COMMAND_SET_ACCEPT_TIME.equals(command)
                    && arguments.size() == 2) {
                }
            }
        
            if(mResultCode != 0){
        	DKLog.e(TAG, "onCommandResult message:" + message.toString());
            }
        }

	//服务器向客户端发送的推送信息
	@Override
	public void onReceiveMessage(Context context, MiPushMessage message) {
		DKLog.d(TAG, "message result: " +message);
		//must run on ui thread
//		mHandler.post(new ProcessMessageRunnable(message));
	}
	
	//UI task
	private class ProcessMessageRunnable implements Runnable {
		
		MiPushMessage message;
		
		public ProcessMessageRunnable(MiPushMessage message) {
			this.message = message;
		}
		
		@Override
		public void run() {
			DKLog.d(TAG, "notification run");
			processMessage(message);
		}
	}
	
	//packaged method	
	private void processMessage(MiPushMessage message) {
		if(message == null) {
			return;
		}
		String title = message.getTitle();
		if(title != null && title.equalsIgnoreCase(FROM_SERVER)){
			processMessageFromServer(message);
		} else{
			processMessageFromWeb(message);
		}
	}
	
	private void processMessageFromServer(MiPushMessage message){
		if(message != null) {
			DKLog.d(TAG, "process msg from server");
			DKApp.getSingleton(MiPushMediaProcess.class).processMedia();
		}
	}
	
	private void processMessageFromWeb(MiPushMessage message){
		if(message != null) {
			DKLog.d(TAG, "process msg from web");
			DKApp.getSingleton(MiPushAdsProcess.class).processAds(message.getContent());
		}
	}
}
