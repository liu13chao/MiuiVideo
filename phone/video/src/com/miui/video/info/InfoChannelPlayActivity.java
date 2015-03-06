package com.miui.video.info;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.miui.video.DKApp;
import com.miui.video.R;
import com.miui.video.base.BaseFragmentActivity;
import com.miui.video.info.InfoChannelDataManager.InfoPlayListener;
import com.miui.video.local.OnlinePlayHistory;
import com.miui.video.local.PlayHistoryManager;
import com.miui.video.model.ChannelInfoStore;
import com.miui.video.type.Channel;
import com.miui.video.type.InformationData;
import com.miui.videoplayer.fragment.VideoFragment;

public class InfoChannelPlayActivity extends BaseFragmentActivity{
	
	public final static String TAG = InfoChannelPlayActivity.class.getName();

	public static String KEY_CHANNEL_ID = "key_channelid";
	public static String KEY_ITEM_POSITION = "key_itemposition";
	
	public static String KEY_MEDIAID = "key_mediaid";
	public static String KEY_INFODATA = "key_infodata";

	// Data
	private int mCurChannelID = -1;
	private InformationData mInfoData;
	
	// UI
	private TextView mTitleView;
	FrameLayout mVideoFrame;
	VideoFragment mVideoFragment;
	InfoPlayFragment mInfoFragment;
	InfoChannelListFragment mInfoListFragment;
	private InfoChannelDataManager mDataManager = null;

	// dimension
	private int mScreenWidth;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_info_play);
		init();
	}
	
	private void init(){
	    initReceivedData();
	    initDimen();
	    initUI();
	    updateUI();
	    initData();
	    playVideo();
	}
	
	private void initData(){
	    if(mDataManager != null){
	        mInfoListFragment.setDataManager(mDataManager);
	        mDataManager.setInfoDataPlay(mInfoData);
	        mDataManager.setInfoPlayListener(mInfoPlayListener);
	        mDataManager.doGetInfoRecommendList();
	        InfoFragmentCreator creator = new InfoFragmentCreator(mDataManager, 
	                InfoPlayUtil.buildUri(mInfoData, 0, mInfoData.playurl));
	        mVideoFragment = new VideoFragment();
	        mVideoFragment.setWindowStyle(true);
	        mInfoListFragment.attachVideoFragment(mVideoFragment);
	        getFragmentManager().beginTransaction().add(R.id.video_container, 
	                mVideoFragment).commitAllowingStateLoss();
	        getFragmentManager().executePendingTransactions();
	        mVideoFragment.playByFragment(creator);
	    }
	}
	
	private void playVideo(){
	    if(mDataManager != null){
//	        mDataManager.playVideo(mInfoData);
	        DKApp.getSingleton(PlayHistoryManager.class).addPlayHistory(new OnlinePlayHistory(mInfoData));
	    }
	}

	private void initDimen(){
	    DisplayMetrics dm = new DisplayMetrics();
	    getWindowManager().getDefaultDisplay().getMetrics(dm);
	    mScreenWidth = Math.min(dm.heightPixels, dm.widthPixels);
//	    mScreenHeight = Math.max(dm.heightPixels, dm.widthPixels);
	}
	
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		updateUI();
	}
	
	protected void onDestroy(){
		super.onDestroy();
		release();
	}
	
	private void release(){
	    if(mDataManager != null){
	        mDataManager.removeDataChangeListener(mInfoListFragment);
	        mDataManager.setInfoPlayListener(null);
	    }
	}
	
	private void initUI(){
	    mVideoFrame = (FrameLayout)findViewById(R.id.video_container);
		mInfoListFragment = (InfoChannelListFragment) getFragmentManager().findFragmentById(R.id.list_fragment);
        mTitleView = (TextView) findViewById(R.id.title_top_name);
        findViewById(R.id.title_top).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
        Channel channel = DKApp.getSingleton(ChannelInfoStore.class).getChannel(mCurChannelID);
        if(channel != null){
            mTitleView.setText(channel.name);
        }
	}
	
	private void updateUI(){
//	    int systemGravity = 0;
//	    try {
//	        systemGravity = Settings.System.getInt(this.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION);
//	    } catch (Exception e) {
//	        e.printStackTrace();
//	    }
//	    if(systemGravity == 0){
////	        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
//	    }
		if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
			findViewById(R.id.title_top).setVisibility(View.GONE);
			mVideoFrame.getLayoutParams().height = mScreenWidth;
			((LinearLayout.LayoutParams)mVideoFrame.getLayoutParams()).topMargin = 0;
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}else{
			findViewById(R.id.title_top).setVisibility(View.VISIBLE);
			mVideoFrame.getLayoutParams().height = getResources().getDimensionPixelSize(
			        R.dimen.info_channel_play_window_height);
//			v.getLayoutParams().height = mScreenWidth * mScreenWidth / mScreenHeight;
			((LinearLayout.LayoutParams)mVideoFrame.getLayoutParams()).topMargin = getResources().
					getDimensionPixelOffset(R.dimen.video_common_interval_negtive_10);
//			mInfoPlayerFragment.configurationChange(mScreenWidth * mScreenWidth / mScreenHeight);
			final WindowManager.LayoutParams attrs = getWindow().getAttributes();
	        attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
	        getWindow().setAttributes(attrs);
	        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
		}
	}
	
	private void initReceivedData() {
		Intent intent = getIntent();
		if(intent.getSerializableExtra(KEY_INFODATA) instanceof InformationData){
		    mInfoData = (InformationData)intent.getSerializableExtra(KEY_INFODATA) ;
			mCurChannelID = mInfoData.channelid;
			mDataManager = InfoChannelDataFactory.getInstance().getManager(mCurChannelID);
			if(mDataManager == null){
				finish();
			}
		}
	}
	
//	public interface InfoChannelPlayListener{
//		public void play(List<Intent> intent, boolean autoplay);
//	}
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
	    if(event.getAction() == KeyEvent.ACTION_DOWN){
	        if(mVideoFragment != null){
	            boolean handled = mVideoFragment.onKeyDown(event);
	            if(handled){
	                return true;
	            }
	        }
	    }
	    return super.dispatchKeyEvent(event);
	}
	
	private InfoPlayListener mInfoPlayListener = new InfoPlayListener() {
        @Override
        public void onPlayInfoData(int position, InformationData infoData) {
            if(mInfoListFragment != null){
                mInfoListFragment.setSelection(position);
            }
        }
    };
}
