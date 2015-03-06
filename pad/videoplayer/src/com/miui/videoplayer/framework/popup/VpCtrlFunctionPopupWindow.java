package com.miui.videoplayer.framework.popup;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout.LayoutParams;

import com.miui.video.R;
import com.miui.videoplayer.framework.DuoKanCodecConstants;
import com.miui.videoplayer.framework.DuoKanConstants;
import com.miui.videoplayer.framework.history.PlayHistoryManager.PlayHistoryEntry;
import com.miui.videoplayer.framework.ui.LocalVideoPlaySizeAdjustable;
import com.miui.videoplayer.framework.utils.DisplayInformationFetcher;
import com.miui.videoplayer.framework.views.OriginMediaController;
import com.miui.videoplayer.VideoPlayerActivity;

public class VpCtrlFunctionPopupWindow extends MenuPopupWindow {
	private View mAnchor;
	
	private Activity mActivity;
	private LocalVideoPlaySizeAdjustable mSizeAdjustable;

	private VpCtrlScaleScreenPopupWindow mOptionSubMenuPopupWindow;
	private AboutPopupWindow mAboutPopupWindow;
	
	private String[] mDuokanCodecValues;
	private String[] mOriginCodecValues;
	
	private OriginMediaController mMediaController;
	private Handler mHandler;
	public static boolean IS_AUDIO_EFFECT_ENHANCE = false;
	public static boolean IS_3D_VIDEO_SUPPORTED = false;
	public static boolean IS_3D_ENABLED = false;
	
	private Toast mToast;
	public VpCtrlFunctionPopupWindow(Activity activity, View anchor) {
		super(activity);
		this.mAnchor = anchor;
		this.mActivity = activity;
		this.mHandler = new Handler();
		
		int width = (int) activity.getResources().getDimension(R.dimen.vp_ctrl_function_pop_width);
		this.setWidth(width);
		this.setHeight(LayoutParams.WRAP_CONTENT);
		
		setupViews();
	}

	private void setupViews() {
        if (DuoKanCodecConstants.sUseDiracSound) {
			mDuokanCodecValues = new String[]{mActivity.getResources().getString(R.string.vp_media_info),
					//mActivity.getResources().getString(R.string.menu_item_orientation_fixed_by_videosize_action),
					mActivity.getResources().getString(R.string.vp_screen_shot),
					mActivity.getResources().getString(R.string.vp_scale_screen),
					mActivity.getResources().getString(R.string.menu_item_enable_audio_enhance),
					mActivity.getResources().getString(R.string.menu_item_about_label)
			};

			if (IS_AUDIO_EFFECT_ENHANCE) {
				mDuokanCodecValues[3] = mActivity.getResources().getString(R.string.menu_item_disable_audio_enhance);
			} else {
				mDuokanCodecValues[3] = mActivity.getResources().getString(R.string.menu_item_enable_audio_enhance);
			}
        } else {
			mDuokanCodecValues = new String[]{mActivity.getResources().getString(R.string.vp_media_info),
				//mActivity.getResources().getString(R.string.menu_item_orientation_fixed_by_videosize_action),
				mActivity.getResources().getString(R.string.vp_screen_shot),
				mActivity.getResources().getString(R.string.vp_scale_screen),
				mActivity.getResources().getString(R.string.menu_item_about_label)
            };
		}

		mOriginCodecValues =  new String[]{
				//mActivity.getResources().getString(R.string.menu_item_orientation_fixed_by_videosize_action),
				mActivity.getResources().getString(R.string.vp_screen_shot),
				mActivity.getResources().getString(R.string.vp_scale_screen),
				mActivity.getResources().getString(R.string.menu_item_about_label)
		};
		
		//SharedPreferences sp = mActivity.getPreferences(Context.MODE_PRIVATE);
		//boolean isOrientationSensor = sp.getBoolean(DuoKanConstants.SHARED_PEREFERENCE_KEY_ORIENTATION_SENSOR, false);
		
		//updateOrientationShow(isOrientationSensor);
		
		updateListView();
		
		View contentView = getContentView();
        TextView topTitleName = (TextView) contentView.findViewById(R.id.vp_popup_ctrl_top_title_name);
        topTitleName.setText(R.string.vp_select_function);
		
		getOptionMenuListView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
				if (DuoKanCodecConstants.sUseDuokanCodec) {
					if (position == 0) {
						onMediaInfoClick(view);
						dismiss();
					}
					/*
					if (position == 1) {
						onOrientationSelectClicked();
						dismiss();
					}
					*/
					if (position == 1) {
						onScreenShotClicked();
					}
					
					if (position == 2 && mSizeAdjustable != null) {
						setMenuDismissed(false);
						onSwitchVideoWhClicked();
					}
					if (DuoKanCodecConstants.sUseDiracSound) {
						if (position == 3) {
							onAudioEffectClicked();
							dismiss();
						}

						if (position == 4) {
							dismiss();
							onAboutClicked();
						}
					} else {
						if (position == 3) {
							dismiss();
							onAboutClicked();
						}
					}

				} else {
					/*
					if (position == 0) {
						onOrientationSelectClicked();
						dismiss();
					}*/
					if (position == 0) {
						onScreenShotClicked();
					}
					if (position == 1 && mSizeAdjustable != null) {
						setMenuDismissed(false);
						onSwitchVideoWhClicked();
					}
					if (position == 2) {
						dismiss();
						onAboutClicked();
						
					}
				}
			}
		});
	}
	
	private void onAudioEffectClicked(){
	/*	DiracSound diracSound = new DiracSound(0, 0);
		int headSetType = diracSound.getHeadsetType();
		diracSound.release();
		Log.i("EffectDiracSound", "onAudioEffectClicked,headSetType:"+headSetType);
		if(headSetType == 0){		
			if (mToast == null) {
				mToast = Toast.makeText(mActivity, R.string.toast_message_set_misound, 300);
				}
			if(mToast.getView() != null && mToast.getView().isShown()){
				return;
			}
		//	String tip = mActivity.getResources().getString(R.string.toast_message_set_misound);
			mToast.show();
			return;
		}*/
		boolean isOpenDir = IS_AUDIO_EFFECT_ENHANCE;
		isOpenDir = !isOpenDir;
		IS_AUDIO_EFFECT_ENHANCE = isOpenDir;
		if (IS_AUDIO_EFFECT_ENHANCE){
			mDuokanCodecValues[3] = mActivity.getResources().getString(R.string.menu_item_disable_audio_enhance);
		} else {
			mDuokanCodecValues[3] = mActivity.getResources().getString(R.string.menu_item_enable_audio_enhance);
		}
		updateListView();

	}

	private void on3dModeClicked() {
		IS_3D_ENABLED = mMediaController.get3dMode();
		Log.e("3D_MODE", "old 3d mode: " + IS_3D_ENABLED);
		if (IS_3D_ENABLED){
			mDuokanCodecValues[4] = mActivity.getResources().getString(R.string.menu_item_enable_3d_mode);
		} else {
			mDuokanCodecValues[4] = mActivity.getResources().getString(R.string.menu_item_disable_3d_mode);
		}
		mMediaController.set3dMode(!IS_3D_ENABLED);
		updateListView();
	}

	public Toast getToast(){
		return mToast;
	}

	private void onAboutClicked() {
		if (mAboutPopupWindow == null) {
			mAboutPopupWindow = new AboutPopupWindow(mActivity);
		}
		mAboutPopupWindow.show(mAnchor, this.getLocalMediaPlayerControl());
	}
	
	private void onSubTitleClicked() {
		List<PlayHistoryEntry> testList = new ArrayList<PlayHistoryEntry>();
		PlayHistoryEntry entry1 = new PlayHistoryEntry("/file/test1");
		entry1.setPosition(20);
		entry1.setTimeStamp(System.currentTimeMillis());
		testList.add(entry1);
		PlayHistoryEntry entry2 = new PlayHistoryEntry("http://eeee");
		entry2.setTimeStamp(System.currentTimeMillis());
		testList.add(entry2);
//		PlayHistoryManager.writeXML(mActivity, testList);
	}
	
	private void onScreenShotClicked() {
		this.setMenuDismissed(false);
		super.dismiss();
		if (getFullScreenPopupWindow() != null && getFullScreenPopupWindow().isShowing()) {
			getFullScreenPopupWindow().dismiss();
		}
		if (mMediaController != null && mMediaController.isShowing()) {
			mMediaController.hide();
		}
		VpCtrlMediaInfoPopupWindow mediaInfoPopupWindow = VpCtrlMediaInfoPopupWindow.getInstance(mActivity);
		boolean showMediaInfo = false;
		if (mediaInfoPopupWindow.isShowing()) {
//			mDuokanCodecValues[0] = mActivity.getResources().getString(R.string.menu_item_media_info_label);
//			updateListView();
			showMediaInfo = true;
		}
		PopupWindowManager.getInstance().dimissAllManagedPopupWindow();
		if (showMediaInfo) {
			mediaInfoPopupWindow.show(mAnchor);
		}
		
		mHandler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				Intent broadcast = new Intent("android.intent.action.CAPTURE_SCREENSHOT");
				broadcast.putExtra("capture_delay", 0L);
				mActivity.sendOrderedBroadcast(broadcast, null, mCaptureScreenshotResultBroadcastReceiver, null, Activity.RESULT_OK, null, null);
			}
		}, 500);
		
	}
	
	private BroadcastReceiver mCaptureScreenshotResultBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context paramContext, Intent paramIntent) {
			if (getLocalMediaPlayerControl() != null) {
				mHandler.postDelayed(new Runnable() {
					
					@Override
					public void run() {
						if (!VideoPlayerActivity.isVideoPaused) {
							getLocalMediaPlayerControl().start();
						}
					}
				}, 1000);
				
			}
		}
		
	}; 
/*
	private void onOrientationSelectClicked() {
		SharedPreferences sp = mActivity.getPreferences(Context.MODE_PRIVATE);
		boolean isOrientationSensor = sp.getBoolean(DuoKanConstants.SHARED_PEREFERENCE_KEY_ORIENTATION_SENSOR, false);
		
		boolean newValue = !isOrientationSensor;
		updateOrientationShow(newValue);
		updateListView();
		
		if (newValue) {
			mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
		} else if (mSizeAdjustable != null){
			int videoWidth = mSizeAdjustable.getVideoWidth();
			int videoHeight = mSizeAdjustable.getVideoHeight();
			if (videoWidth < videoHeight) {
				mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
			} else {
				mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
			}
		}
		
		Editor editor = sp.edit();
		editor.putBoolean(DuoKanConstants.SHARED_PEREFERENCE_KEY_ORIENTATION_SENSOR, newValue);
		editor.commit();
	}
	
	private void updateOrientationShow(boolean isOrientationSensor) {
//		Log.e("isOrientationSensor: ", isOrientationSensor + "");
		if (isOrientationSensor) {
			mDuokanCodecValues[1] = mActivity.getResources().getString(R.string.menu_item_orientation_fixed_by_videosize_action);
			mOriginCodecValues[0] = mActivity.getResources().getString(R.string.menu_item_orientation_fixed_by_videosize_action);
		} else {
			mDuokanCodecValues[1] = mActivity.getResources().getString(R.string.menu_item_orientation_auto_sensor_action);
			mOriginCodecValues[0] = mActivity.getResources().getString(R.string.menu_item_orientation_auto_sensor_action);
		}
//		updateListView();
	}
*/
	private void onSwitchVideoWhClicked() {
		if (mOptionSubMenuPopupWindow == null) {
			return;
		}
		mOptionSubMenuPopupWindow.setFullScreenPopupWindow(this.getFullScreenPopupWindow());
//		mOptionSubMenuPopupWindow.setDuoKanMediaController(mDuoKanMediaController);
		mOptionSubMenuPopupWindow.setLocalMediaPlayerControl(this.getLocalMediaPlayerControl());
		if (!mOptionSubMenuPopupWindow.isShowing()) {
			boolean isPlaying = this.getPlayingStatus();
			this.setPlayingStatus(false);
			this.dismiss();
			mOptionSubMenuPopupWindow.show(mAnchor);
			mOptionSubMenuPopupWindow.setPlayingStatus(isPlaying);
		}
	}
	
//	private void onAboutClick() {
//		if (mAboutPopupWindow == null) {
//			mAboutPopupWindow = new AboutPopupWindow(mActivity, mLocalMediaPlayerControl);
//		}
//		mAboutPopupWindow.show(mAnchor);
//	}
	
	private void onMediaInfoClick(View view) {
		VpCtrlMediaInfoPopupWindow mediaInfoPopupWindow = VpCtrlMediaInfoPopupWindow.getInstance(mActivity);
		mediaInfoPopupWindow.updateValues(this.getLocalMediaPlayerControl().getMediaInfo());
		if (!mediaInfoPopupWindow.isShowing()) {
			mediaInfoPopupWindow.show(mAnchor);
			mDuokanCodecValues[0] = mActivity.getResources().getString(R.string.vp_hide_media_info);
		} else {
			mediaInfoPopupWindow.dismiss();
			mDuokanCodecValues[0] = mActivity.getResources().getString(R.string.vp_media_info);
		}
		
		updateListView();
	}

	private void updateListView() {
		String[] showValues = null;
		if (DuoKanCodecConstants.sUseDuokanCodec) {
			showValues = mDuokanCodecValues;
		} else {
			showValues = mOriginCodecValues;
		}
		
		String[] newValues = new String[showValues.length - 1];
		for(int i = 0; i < newValues.length; i++) {
			newValues[i] = showValues[i];
		}
		showValues = newValues;

		FunctionAdapter adapter = new FunctionAdapter(mActivity, showValues);
		getOptionMenuListView().setAdapter(adapter);
	}
	
	public void setSizeAdjustable(LocalVideoPlaySizeAdjustable sizeAdjustable) {
		this.mSizeAdjustable = sizeAdjustable;
	}
	
	public void setOptionSubMenuPopupWindow(VpCtrlScaleScreenPopupWindow optionSubMenuPopupWindow) {
		this.mOptionSubMenuPopupWindow = optionSubMenuPopupWindow;
	}
	
	public void setMediaController(OriginMediaController mediaController) {
		this.mMediaController = mediaController;
	}

	@Override
	protected int getMenuHeightForPortScreen() {
		int itemHeight = (int) mActivity.getResources().getDimension(R.dimen.popup_right_menu_option_listview_item_height);
		if (DuoKanConstants.ENABLE_V5_UI) {
			itemHeight = (int) mActivity.getResources().getDimension(R.dimen.popup_left_menu_option_listview_item_height_v5);
		}
		int divideHeight = (int) mActivity.getResources().getDimension(R.dimen.popup_left_menu_option_listview_item_divider_height);
		int space = 5;
		if (DuoKanConstants.ENABLE_V5_UI) {
			int verticalSpace = (int) mActivity.getResources().getDimension(R.dimen.popup_bottom_menu_vertical_space);
			space = space + verticalSpace;
		}
		int count = 0;
		if (DuoKanCodecConstants.sUseDuokanCodec) {
			if (DuoKanCodecConstants.sUseDiracSound)
				count = 5;
			else {
				count = 4;
			}
		} else {
			count = 3;
		}

		return (itemHeight + divideHeight) * count + space;
	}

	@Override
	protected int getBackgroundResId(int orientation) {
		if (orientation == DisplayInformationFetcher.SCREEN_LAND) {
			return R.drawable.vp_menu_background_v5;
		} else {
			return R.drawable.vp_menu_background_vertical_v5;
		}
	}

	@Override
	protected int getMenuWidthForLandScreen() {
		int width = (int) mActivity.getResources().getDimension(R.dimen.popup_left_menu_option_width);
		if (DuoKanConstants.ENABLE_V5_UI) {
			width = (int) mActivity.getResources().getDimension(R.dimen.popup_right_menu_option_width_v5);
		}
		return width;
	}
	
	private class FunctionAdapter extends BaseAdapter {
		
		private Context context;
		private String[] items;
		
		public FunctionAdapter(Context context, String[] items) {
			this.context = context;
			this.items = items;
		}
		
		private class ViewHolder {
			private TextView  itemTv;
		}
		
		@Override
		public String getItem(int position) {
			if(items != null && position < items.length) {
				return items[position];
			}
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}
		
		@Override
		public int getCount() {
			if(items != null) {
				return items.length;
			}
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder vHolder = null;
			if (convertView == null) {
				vHolder = new ViewHolder();
				convertView = View.inflate(context, R.layout.vp_popup_ctrl_function_item, null);
				vHolder.itemTv = (TextView) convertView.findViewById(R.id.vp_ctrl_function_title);
				convertView.setTag(vHolder);
			} else {
				vHolder = (ViewHolder) convertView.getTag();
			}
			String item = getItem(position);
			if(item != null) {
				vHolder.itemTv.setText(item);
			}
			return convertView;
		}
	}
}
