package com.miui.video;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout.LayoutParams;
import android.widget.TextView;

import com.miui.video.adapter.ChannelFilterAdapter;
import com.miui.video.base.BaseFragmentActivity;
import com.miui.video.model.ChannelInfoStore;
import com.miui.video.type.Channel;
import com.miui.video.widget.ListViewEx;
import com.miui.video.widget.bg.OnlineBg;

public class ChannelFilterActivity extends BaseFragmentActivity{

	private View mTitleTop;
	private TextView mTitleName;
	private ListViewEx mListView;
	private Channel mChannel;
	ChannelFilterAdapter mAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.channel_filter);
		init();
	}
	
	private void init() {
		initReceivedData();
		initUI();
	}
	
	private void initReceivedData() {
		Intent intent = getIntent();
		Object obj = intent.getSerializableExtra(ChannelActivity.KEY_CHANNEL);
		if(obj instanceof Channel) {
			mChannel = DKApp.getSingleton(ChannelInfoStore.class).getChannel((Channel) obj);
		}
	}
	
	private void initUI() {
		initDecorView();
		mTitleTop = findViewById(R.id.title_top);
		mTitleName = (TextView) findViewById(R.id.title_top_name);
		mTitleTop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		mListView = (ListViewEx) findViewById(R.id.channel_filter_list);
		mListView.setPadding(0, getResources().getDimensionPixelSize(R.dimen.size_30),
				0, getResources().getDimensionPixelSize(R.dimen.size_30));
		mListView.setClipToPadding(false);
		mListView.setDividerHeight(getResources().getDimensionPixelSize(R.dimen.video_common_interval_21));
		if(mChannel != null) {
			mTitleName.setText(mChannel.name + getString(R.string.filter));
			mAdapter = new ChannelFilterAdapter(this, mChannel);
			mListView.setAdapter(mAdapter);
		}
		findViewById(R.id.channel_filter_btn).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mAdapter != null){
					Channel[] channels = mAdapter.getIDs();
					if(channels == null || channels.length == 0){
						return;
					}
					int[] ids = new int[channels.length];
					String[] names = new String[channels.length];
					for(int i = 0 ; i < channels.length; i ++){
						ids[i] = channels[i].id;
						names[i] = channels[i].name;
					}
					Intent intent = new Intent();
					intent.putExtra(ChannelActivity.KEY_CHANNEL, mChannel);
					intent.putExtra(ChannelActivity.KEY_CHANNEL_IDS, ids);
					intent.putExtra(ChannelActivity.KEY_CHANNEL_NAMES, names);
					intent.setClass(ChannelFilterActivity.this, ChannelFilterResultActivity.class);
					startActivity(intent);
					ChannelFilterActivity.this.finish();
				}
			}
		});
	}
	
	private void initDecorView() {
		ViewGroup decorView = (ViewGroup) getWindow().getDecorView();
		OnlineBg onlineBg = new OnlineBg(this);
		LayoutParams onlineBgParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		decorView.addView(onlineBg, 0, onlineBgParams);
	}
	
}
