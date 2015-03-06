package com.miui.video.mipush;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.miui.video.MainActivity;
import com.xiaomi.miui.pushads.sdk.NotifyAdsCell;

public class PendingIntentFactory {

	private static final String FILM = "film";
	private static final String SUBJECT = "subject";
	private static final String OPENHTML = "openhtml";
	private static final String MEDIADETAIL = "mediadetail";
	private static final String SHORTVIDEO = "shortvideo";
	
	public PendingIntentFactory() {
	
	}

	public static PendingIntent createPendingIntent(Context context, NotifyAdsCell cell, String actiontype){
		
		PendingIntent clickPendingIntent = null;
		IntentCreator createIntent = null;
		if(actiontype.equalsIgnoreCase(FILM)){
			createIntent = new CreateIntentForFilm(context, cell);
		}else if(actiontype.equalsIgnoreCase(SUBJECT)){
			createIntent = new CreateIntentForSubject(context, cell);
		}else if(actiontype.equalsIgnoreCase(OPENHTML)){
			createIntent = new CreateIntentForHtml(context, cell);
		}else if(actiontype.equalsIgnoreCase(MEDIADETAIL)){
			createIntent = new CreateIntentForMediaDetail(context, cell);
		}else if(actiontype.equalsIgnoreCase(SHORTVIDEO)){
			createIntent = new CreateIntentForShortVideo(context, cell);
		}

		if(createIntent != null){
			Intent[] intents = new Intent[2];
			intents[0] = Intent.makeRestartActivityTask(new ComponentName(context, MainActivity.class));
			intents[1] = createIntent.creatIntent();
			clickPendingIntent = PendingIntent.getActivities(context, 0, 
					intents, PendingIntent.FLAG_UPDATE_CURRENT);
		}
        return clickPendingIntent;
	}
}
