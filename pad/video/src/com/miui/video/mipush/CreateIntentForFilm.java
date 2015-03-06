package com.miui.video.mipush;

import com.miui.video.ChannelActivity;
import com.miui.video.type.Channel;
import com.xiaomi.miui.pushads.sdk.NotifyAdsCell;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class CreateIntentForFilm implements IntentCreator{

	private Context context;
	private NotifyAdsCell cell;
	public CreateIntentForFilm(Context context, NotifyAdsCell cell) {
		this.context = context;
		this.cell = cell;
	}

	@Override
	public Intent creatIntent() {
		Intent clickIntent = new Intent(context, ChannelActivity.class);
		Channel channel = new Channel();
		channel.id = Integer.valueOf(Uri.parse(cell.actionUrl).getQueryParameter("id"));
		channel.name = Uri.parse(cell.actionUrl).getQueryParameter("name");
		channel.type = 1; 
		clickIntent.putExtra(ChannelActivity.KEY_CHANNEL, channel);	
		return clickIntent;
	}
}
