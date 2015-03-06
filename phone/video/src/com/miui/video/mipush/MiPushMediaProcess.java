package com.miui.video.mipush;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;

import com.miui.video.DKApp;
import com.miui.video.FavoriteActivity;
import com.miui.video.R;
import com.miui.video.model.AppSingleton;
import com.miui.video.util.DKLog;

public class MiPushMediaProcess extends AppSingleton {

	private final String TAG = MiPushMediaProcess.class.getName();
	
	private int curCount = 0;
    private final int NOTIFICATION_ID = 1000;
	private final int PENDINGINTENT_REQUESTCODE = 2000;
	
	public void clearCurCount() {
		curCount = 0;
	}
	
	public void processMedia() {
		showCurNewSetNotification();
	}
	
	//packaged method
	private void incCurCount() {
		curCount++;
	}
	
	private void showCurNewSetNotification() {
		DKLog.d(TAG, "show cur new set notification");
		incCurCount();
		Context context = DKApp.getAppContext();
		Resources res = context.getResources();
		StringBuilder strBuilder = new StringBuilder();
		Intent[] intents= null;
		
		intents = makeMessageIntentStack(context);
		strBuilder.append(res.getString(R.string.updatedalready));
		String str = res.getString(R.string.count_ge);
		str = String.format(str, curCount);
		strBuilder.append(str);
		strBuilder.append(res.getString(R.string.video));

		String notificationContent = strBuilder.toString();
		String tickerText = notificationContent;
		String notificationTitle = res.getString(R.string.app_name);
		
		NotificationManager notificationManager = 
	            (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		PendingIntent contentIntent = PendingIntent.getActivities(context, PENDINGINTENT_REQUESTCODE,
				intents, PendingIntent.FLAG_CANCEL_CURRENT);
		Notification notification = new Notification.Builder(context)
		.setAutoCancel(true)
		.setTicker(tickerText)
		.setContentTitle(notificationTitle)
		.setContentText(notificationContent)
		.setContentIntent(contentIntent)
		.setSmallIcon(R.drawable.ic_launcher)
		.setWhen(System.currentTimeMillis())
		.build();
		
		notificationManager.notify(NOTIFICATION_ID, notification);
	}
	
	private Intent[] makeMessageIntentStack(Context context) {
		Intent[] intents = new Intent[1];
		intents[0] = Intent.makeRestartActivityTask(new ComponentName(context, FavoriteActivity.class));
		intents[0].putExtra(FavoriteActivity.KEY_FROMNOTIFICATION, true);
		return intents;
	}
}
