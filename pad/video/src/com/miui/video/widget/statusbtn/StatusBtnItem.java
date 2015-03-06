package com.miui.video.widget.statusbtn;

import com.miui.video.api.def.MediaConstantsDef;
import com.miui.video.offline.OfflineMedia;

/**
 *@author tangfuling
 *
 */

/**
 * this class is defined for StatusBtnAdapter
 *
 */

public class StatusBtnItem {
	
	//common
	int style;
	public float percent;
	
	//StatusBtn
	public String text;
	public int episode;
	
	//StatusBtnLong
	public String date;
	public String videoName;
	
	//外部传入的UI显示标志，控制UI显示方式
	public int uiStatus;                    //StatusBtn的基本状态
	public boolean showIconOnly = false;    //仅显示图标，不显示文字，仅针对StatusBtn的loading与play状态，不针对StatusBtnLong
	public boolean clickable = true;        //是否可点击
	public boolean textColorEnable = true;  //TextColor有Enable与Disable两种状态
	
	/* 
	 * 外部传入的控制标志，由这些标志，来决定UiStatus
	 * 详情页面当前集, isInPlayMode, isLoading; 下载选择页面, 编辑模式, isInEditMode
	 * 
	 * 加入离线视频有时会耗时几百毫秒，这时UI需要被强制更新为下载状态
	 * 在这期间，如果离线视频的状态改变了，会重新刷新UI
	 * 由于离线视频还没有被加入，UI不会显示为下载状态，这是不对的
	 * 这时候就需要忽略离线视频数量的变化，这样UI就会继续显示为下载状态
	 * 
	 */
	public OfflineMedia offlineMedia;
	public boolean isShowDownloadStatus;  //是否显示下载状态
	public boolean isInEditMode;          //是否处于离线下载的编辑模式，下载选择页面有编辑模式与非编辑模式
	public boolean isLoading;             //是否处于加载模式
	public boolean isPlaying;             //是否处于播放模式
	
	public void refreshUiStatus() {
		refreshUiStatus(false);
	}
	
	public void refreshUiStatus(boolean ignoreOfflineMediaChange) {
		//更新百分比
		if(offlineMedia != null) {
			percent = offlineMedia.getPercent();
		} else {
			percent = 0;
		}
		
		//更新uiStatus
		if(!isShowDownloadStatus) {
			if(offlineMedia != null && offlineMedia.status == MediaConstantsDef.OFFLINE_STATE_FINISH) {
				uiStatus = StatusBtn.UI_STATUS_DONE;
				textColorEnable = false;
				return;
			}
			if(isLoading) {
				uiStatus = StatusBtn.UI_STATUS_LOADING;
				textColorEnable = true;
				return;
			}
			if(isPlaying) {
				uiStatus = StatusBtn.UI_STATUS_PLAY;
				textColorEnable = true;
				return;
			}
			uiStatus = StatusBtn.UI_STATUS_TEXT_ONLY;
		} else {
			if(offlineMedia != null) {
				if(isInEditMode) {
					uiStatus = StatusBtn.UI_STATUS_DELETE;
					textColorEnable = true;
				} else {
					int offlineMediaStatus = offlineMedia.status;
					switch (offlineMediaStatus) {
					case MediaConstantsDef.OFFLINE_STATE_INIT:
						uiStatus = StatusBtn.UI_STATUS_CONNECT;
						textColorEnable = false;
						break;
						
					case MediaConstantsDef.OFFLINE_STATE_FINISH:
						uiStatus = StatusBtn.UI_STATUS_DONE;
						textColorEnable = false;
						break;
					case MediaConstantsDef.OFFLINE_STATE_IDLE:
						uiStatus = StatusBtn.UI_STATUS_WAITING;
						textColorEnable = false;
						break;
					case MediaConstantsDef.OFFLINE_STATE_PAUSE:
						uiStatus = StatusBtn.UI_STATUS_PAUSE;
						textColorEnable = false;
						break;
					case MediaConstantsDef.OFFLINE_STATE_LOADING:
						uiStatus = StatusBtn.UI_STATUS_DOWNLOAD;
						textColorEnable = false;
						break;
						
					case MediaConstantsDef.OFFLINE_STATE_CONNECT_ERROR:
					case MediaConstantsDef.OFFLINE_STATE_FILE_ERROR:
					case MediaConstantsDef.OFFLINE_STATE_SOURCE_ERROR:
					default:
						uiStatus = StatusBtn.UI_STATUS_ERROR;
						textColorEnable = false;
						break;
					}
				}
			} else {
				if(!ignoreOfflineMediaChange) {
					uiStatus = StatusBtn.UI_STATUS_TEXT_ONLY;
					if(isInEditMode) {
						textColorEnable = false;
					} else {
						textColorEnable = true;
					}
				}
			}
		}
	}
}
