/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   SelectSourceView.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-8-27
 */

package com.miui.videoplayer.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.miui.video.DKApp;
import com.miui.video.R;
import com.miui.videoplayer.common.AndroidUtils;
import com.miui.videoplayer.dialog.AlertDialogApkFactory;
import com.miui.videoplayer.dialog.AlertDialogSoFactory;
import com.miui.videoplayer.download.SourceManager;
import com.miui.videoplayer.download.SourceManager.OnDownloadApkListener;
import com.miui.videoplayer.download.SourceManager.OnDownloadSoListener;
import com.miui.videoplayer.fragment.VideoProxy;
import com.miui.videoplayer.menu.popup.SourceListPopupWindow;
import com.miui.videoplayer.menu.popup.SourceListPopupWindow.OnSourceSelectListener;
import com.miui.videoplayer.model.MediaConfig;
import com.miui.videoplayer.model.OnlineEpisodeSource;
import com.miui.videoplayer.model.OnlineLoader;
import com.miui.videoplayer.model.OnlineLoader.NotifyBuildSourcesListener;
import com.miui.videoplayer.model.OnlineUri;

/**
 * @author tianli
 *
 */
public class SelectSourceView extends RelativeLayout implements OnClickListener, OnSourceSelectListener {
	public static final String TAG = "SelectSourceView";

	private ImageView mIcon;
	private TextView mSelectPanel;
	private ViewGroup mAnchor;
	private OnlineLoader mOnlineLoader;
	private VideoProxy mVideoProxy;
	private SourceListPopupWindow mPopup;
	
	private SourceManager mSourceManager;
	
	public SelectSourceView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public SelectSourceView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SelectSourceView(Context context) {
		super(context);
	}
	
	@Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mIcon = (ImageView) findViewById(R.id.select_source_icon);
        mSelectPanel = (TextView) findViewById(R.id.select_source_text);
        mSourceManager = DKApp.getSingleton(SourceManager.class);
        mSelectPanel.setEnabled(false);
    }

	public void attachOnlineLoader(OnlineLoader loader) {
		if (loader == null /*|| loader == mOnlineLoader*/) {
			Log.e(TAG, "online loader: " + loader);
			return;
		}
		mOnlineLoader = loader;
		mIcon.setVisibility(View.GONE);
		mSelectPanel.setVisibility(View.GONE);
		if (mOnlineLoader.getPlayingUri() instanceof OnlineUri) {
			OnlineUri uri = (OnlineUri) mOnlineLoader.getPlayingUri();
			Log.d(TAG, "online uri, source: " + uri.getSource() + ", resolution: " + uri.getResolution());
			if (MediaConfig.needShowLogo(uri.getSource())) {
				mIcon.setImageDrawable(MediaConfig.getLogoBySource(
						getContext(), uri.getSource()));
				mIcon.setVisibility(View.VISIBLE);
				mIcon.setOnClickListener(this);
			}
			mSelectPanel.setText(mSourceManager.getSourceName(uri.getSource())
					+ MediaConfig.getResolutionName(getContext(), uri.getResolution()));
			mSelectPanel.setVisibility(View.VISIBLE);
			mOnlineLoader.setNotifyBuildSourcesListener(buildSourceListener);
			refreshSelectPanel();
		}
	}
	
	private OnlineUri getCurrentUri() {
		if (mOnlineLoader != null && mOnlineLoader.getPlayingUri() instanceof OnlineUri) {
			return (OnlineUri) mOnlineLoader.getPlayingUri();
		}
		return null;
	}
	
	private int getCurrentSource() {
		OnlineUri uri = getCurrentUri();
		if (uri == null) {
			return MediaConfig.MEDIASOURCE_UNKNOWN_TYPE_CODE;
		}
		return uri.getSource();
	}
	
	@Override
	public void onClick(View view) {
		final int id = view.getId();
		if (R.id.select_source_icon == id) {
		    mSourceManager.downloadApk(getCurrentSource(), new OnDownloadApkListener() {
				AlertDialog tAlertDialog;
				
				public void hideDialog() {
					if (tAlertDialog != null && tAlertDialog.isShowing()) {
						try {
							tAlertDialog.dismiss();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				
				public void showDialog() {
					if (tAlertDialog != null && !tAlertDialog.isShowing()) {
						try {
							tAlertDialog.show();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				@Override
				public void onApkDownloadStart(String name) {
					hideDialog();
					tAlertDialog = AlertDialogApkFactory.createStartDialogWithAsk(getContext(), name);
					tAlertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getContext().getText(R.string.vp_install), new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							mSourceManager.downloadApk();
							Toast.makeText(getContext(), R.string.vp_toast_downloadapk, Toast.LENGTH_SHORT).show();
						}
					} );
					
					tAlertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getContext().getText(R.string.vp_cancel), new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					} );
					showDialog();
				}
				
				@Override
				public void onApkDownloadProgress(int completed, int total) {
					StringBuilder sb = new StringBuilder();
					sb.append(getContext().getResources().getString(R.string.vp_loading));
					sb.append(AndroidUtils.formatPercent(completed, total));
					
					if(tAlertDialog == null || !(tAlertDialog.findViewById(R.id.hint_text) instanceof TextView)){
						hideDialog();
						tAlertDialog = AlertDialogApkFactory.createLoadingDialog(getContext());
						tAlertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getContext().getText(R.string.vp_cancel), new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
								mSourceManager.downloadApkStop();
							}
						} );
						showDialog();
					}
					TextView textView = (TextView) tAlertDialog.findViewById(R.id.hint_text);
					if(textView != null){
						textView.setText(sb.toString());
					}
				}
				
				@Override
				public void onApkDownloadError(int error) {
					hideDialog();
					tAlertDialog = AlertDialogApkFactory.createErrorDialog(getContext(), error);
					tAlertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getContext().getText(R.string.vp_retry), new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							mSourceManager.downloadApk();
						}
					} );
					
					tAlertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getContext().getText(R.string.vp_cancel), new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					} );
					showDialog();
				}

				@Override
				public void onApkDownloadComplete() {
					hideDialog();
				}
			});
		} else if (R.id.select_source_text == id) {
			if (isPopupShowing()) {
				hidePopup();
			} else {
				showPopup();
			}
		}
	}

	private boolean isPopupShowing() {
		if (mPopup == null) {
			return false;
		}
		return mPopup.isShowing();
	}
	
	private SourceListPopupWindow getPopup(){
        if (mPopup == null) {
            mPopup = new SourceListPopupWindow(getContext());
            mPopup.setOnSourceSelectListener(this);
        }
        if(mOnlineLoader != null && mOnlineLoader.getPlayingUri() instanceof OnlineUri){
            OnlineUri uri = (OnlineUri) mOnlineLoader.getPlayingUri();
            mPopup.setCurrentSource(new OnlineEpisodeSource(uri.getSource(), uri.getResolution()));  
            mPopup.setSources(mOnlineLoader.buildSources(uri.getCi()));
        }
        return mPopup;
	}
	
	private void showPopup() {
		getPopup().show(mAnchor);
		 if(mVideoProxy != null){
             mVideoProxy.hideController();
         }
	}
	
	private void hidePopup() {
		if (mPopup == null) {
			return;
		}
		mPopup.dismiss();
	}
	
	@Override
	public void onSourceSelect(int position, final OnlineEpisodeSource source) {
		hidePopup();
		if (mPopup != null && !source.equals(mPopup.getCurrentSource())) {
		    mSourceManager.downloadSo(source.getSource(), new OnDownloadSoListener() {
				AlertDialog tAlertDialog;
				
				public void hideDialog() {
					if (tAlertDialog != null && tAlertDialog.isShowing()) {
						try {
							tAlertDialog.dismiss();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				
				public void showDialog() {
					if (tAlertDialog != null && !tAlertDialog.isShowing()) {
						try {
							tAlertDialog.show();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}				
				@Override
				public void onSoReady(String path) {
					Log.d(TAG, "onSoReady, path: " + path);
					mSourceManager.setAppLibDir(path);
					if (MediaConfig.MEDIASOURCE_SOHU_TYPE_CODE == source.getSource()) {
//						SohuVideoPlayer.setMylibPath(getContext(), path);
					}
					playSourceResolution(source.getSource(), source.getResolution());
				}
				
				@Override
				public void onSoNotReady() {
					Log.d(TAG, "onSoNotReady");
					playSourceResolution(source.getSource(), source.getResolution());
				}

				@Override
				public void onSoDownloadStart() {
					hideDialog();
					tAlertDialog = AlertDialogSoFactory.createStartDialogWithAsk(getContext());
					tAlertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getContext().getText(R.string.vp_install), new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							mSourceManager.downloadSo();
						}
					} );
					
					tAlertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getContext().getText(R.string.vp_cancel), new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					} );
					showDialog();
				}

				@Override
				public void onSoDownloadProgress(int completed, int total) {
					StringBuilder sb = new StringBuilder();
					sb.append(getContext().getResources().getString(R.string.vp_loading));
					sb.append(AndroidUtils.formatPercent(completed, total));
					if(tAlertDialog == null || !(tAlertDialog.findViewById(R.id.hint_text) instanceof TextView)){
						hideDialog();
						tAlertDialog = AlertDialogSoFactory.createLoadingDialog(getContext());
						tAlertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getContext().getText(R.string.vp_cancel), new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
								mSourceManager.downloadSoStop();
							}
						} );						
						showDialog();
					}
					TextView textView = (TextView) tAlertDialog.findViewById(R.id.hint_text);
					if(textView != null){
						textView.setText(sb.toString());
					}
				}

				@Override
				public void onSoDownloadError(int error) {
					hideDialog();
					tAlertDialog = AlertDialogSoFactory.createErrorDialog(getContext(), error);
					tAlertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getContext().getText(R.string.vp_retry), new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							mSourceManager.downloadSo();
						}
					} );
					
					tAlertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getContext().getText(R.string.vp_cancel), new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					} );
					showDialog();
				}

				@Override
				public void onSoDownloadComplete() {
					hideDialog();
				}
			});
		}
	}
	
	private void playSourceResolution(int source, int resolution) {
		if(mVideoProxy != null){
			mVideoProxy.playSource(source, resolution);
		}
	}
	
	public NotifyBuildSourcesListener buildSourceListener = new NotifyBuildSourcesListener() {
		@Override
		public void OnBuildSourcesDown() {
		    refreshSelectPanel();
		}
	};
	
	private void refreshSelectPanel(){
	    if(mOnlineLoader != null && mOnlineLoader.getPlayingUri() instanceof OnlineUri){
	        OnlineUri uri = (OnlineUri) mOnlineLoader.getPlayingUri();
	        if (mOnlineLoader.canSelectSource(uri.getCi())) {
	            mSelectPanel.setEnabled(true);
	            mSelectPanel.setOnClickListener(SelectSourceView.this);
	            mSelectPanel.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(
	                    R.drawable.vp_source_panel_down), null);
	        }else {
	            mSelectPanel.setEnabled(false);
	            mSelectPanel.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
	        }
	    }
	}
	
	public void setAnchor(ViewGroup anchor){
		mAnchor = anchor;
	}
	
	public void attachVideoProxy(VideoProxy videoProxy){
		mVideoProxy =  videoProxy;
	}
}
