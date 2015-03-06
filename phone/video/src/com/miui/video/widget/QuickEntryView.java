package com.miui.video.widget;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.miui.video.ChannelActivity;
import com.miui.video.DKApp;
import com.miui.video.FavoriteActivity;
import com.miui.video.HistoryActivity;
import com.miui.video.OfflineMediaActivity;
import com.miui.video.R;
import com.miui.video.model.ChannelInfoStore;
import com.miui.video.type.Channel;

public class QuickEntryView extends LinearLayout {

	private Context mContext;
	private View mContentView;
	
	private TextView mTvSeries;
	private TextView mFilm;
	private TextView mVariety;
	private TextView mAll;
	private TextView mPlayHis;
	private TextView mOffline;
	private TextView mFav;
	
//	private Channel mTvSeriesChannel;
//	private Channel mFilmChannel;
//	private Channel mVarietyChannel;
	
//	private final int TV_SERIES_ID = 33554432;
//	private final int FIME_ID = 16777216;
//	private final int VARIETY_ID = 67108864;
	
	private OnQuickEntryItemClickListener mListener;
	
	private ChannelInfoStore mChannelStore;
	
	public static final int POSITION_ALL = 3;
	
	public QuickEntryView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public QuickEntryView(Context context) {
		super(context);
		init(context);
	}
	
	public void setOnQuickEntryItemClickListener(OnQuickEntryItemClickListener listener) {
		this.mListener = listener;
	}
	
//	public void setChannels(List<Channel> channels) {
//		if(channels == null) {
//			return;
//		}
//		for(int i = 0; i < channels.size(); i++) {
//			Channel channel = channels.get(i);
//			if(channel != null) {
//				switch (channel.id) {
//				case TV_SERIES_ID:
//					mTvSeriesChannel = channel;
//					break;
//				case FIME_ID:
//					mFilmChannel = channel;
//					break;
//				case VARIETY_ID:
//					mVarietyChannel = channel;
//					break;					
//				default:
//					break;
//				}
//			}
//		}
//	}

	private void init(Context context) {
		mContext = context;
		mContentView = View.inflate(context, R.layout.quick_entry, null);
		mTvSeries = (TextView) mContentView.findViewById(R.id.quick_entry_tv_series);
		mTvSeries.setOnClickListener(mOnClickListener);
		mFilm = (TextView) mContentView.findViewById(R.id.quick_entry_film);
		mFilm.setOnClickListener(mOnClickListener);
		mVariety = (TextView) mContentView.findViewById(R.id.quick_entry_variety);
		mVariety.setOnClickListener(mOnClickListener);
		mAll = (TextView) mContentView.findViewById(R.id.quick_entry_all);
		mAll.setOnClickListener(mOnClickListener);
		mPlayHis = (TextView) mContentView.findViewById(R.id.quick_entry_play_his);
		mPlayHis.setOnClickListener(mOnClickListener);
		mOffline = (TextView) mContentView.findViewById(R.id.quick_entry_offline);
		mOffline.setOnClickListener(mOnClickListener);
		mFav = (TextView) mContentView.findViewById(R.id.quick_entry_fav);
		mFav.setOnClickListener(mOnClickListener);
		addView(mContentView);
		
		mChannelStore = DKApp.getSingleton(ChannelInfoStore.class);
	}
	
	private void startMyOffline() {
		Intent intent = new Intent();
		intent.setClass(mContext, OfflineMediaActivity.class);
		mContext.startActivity(intent);
	}
	
	private void startPlayHistory() {
		Intent intent = new Intent();
		intent.setClass(mContext, HistoryActivity.class);
		intent.putExtra(HistoryActivity.KEY_TITLE, mPlayHis.getText());
		mContext.startActivity(intent);
	}
	
	private void startTvSeries() {
	    startChannel(Channel.CHANNEL_ID_SERIES);
	}
	
	private void startFilm() {
	    startChannel(Channel.CHANNEL_ID_MOVIE);
	}
	
	private void startVariety() {
	    startChannel(Channel.CHANNEL_ID_VARIETY);
	}
	
	private void startChannel(int channelId){
	    Channel channel = mChannelStore.getChannel(channelId);
	    if(channel != null){
	        Intent intent = new Intent();
	        intent.putExtra(ChannelActivity.KEY_CHANNEL, channel);
	        intent.setClass(mContext, ChannelActivity.class);
	        mContext.startActivity(intent);
	    }else{
	        mChannelStore.load();
	    }
	}
	
	private void startFav() {
		Intent intent = new Intent(mContext, FavoriteActivity.class);
		mContext.startActivity(intent);
	}
	
	private void notifyItemClick(int position) {
		if(mListener != null) {
			mListener.onQuickEntryItemClick(position);
		}
	}
	
	private OnClickListener mOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(v == mTvSeries) {
				startTvSeries();
			} else if(v == mFilm) {
				startFilm();
			} else if(v == mVariety) {
				startVariety();
			} else if(v == mAll) {
				notifyItemClick(POSITION_ALL);
			} else if(v == mPlayHis) {
				startPlayHistory();
			} else if(v == mOffline) {
				startMyOffline();
			} else if(v == mFav) {
				startFav();
			}
		}
	};
	
	public interface OnQuickEntryItemClickListener {
		public void onQuickEntryItemClick(int position);
	}
}
