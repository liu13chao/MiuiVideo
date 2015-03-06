package com.miui.video;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout.LayoutParams;
import android.widget.TextView;

import com.miui.video.api.DKApi;
import com.miui.video.base.BaseActivity;
import com.miui.video.model.AuthenticateAccountManager;
import com.miui.video.statistic.CommentStatisticInfo;
import com.miui.video.type.MediaInfo;
import com.miui.video.util.AlertMessage;
import com.miui.video.util.DKLog;
import com.miui.video.util.Util;
import com.miui.video.widget.ScoringView;
import com.miui.video.widget.bg.OnlineBg;
import com.xiaomi.mitv.common.webservice.ServiceRequest;
import com.xiaomi.mitv.common.webservice.ServiceRequest.Observer;
import com.xiaomi.mitv.common.webservice.ServiceResponse;
import miui.app.AlertDialog;

/**
 *@author tangfuling
 *
 */

public class CommentEditActivity extends BaseActivity {
	private static String TAG = CommentEditActivity.class.getName();
	
	public static final String KEY_MEDIA_INFO = "mediaInfo";
	
	//UI
	private TextView mBtnBack;
	private TextView mBtnSend;
	private TextView mTopTitle;
	private EditText mEditText;
	
	private ScoringView mScoringView;
	
	//received data
	private MediaInfo mMediaInfo;
	
	//data
	private String mCommentStr = "";
	private int mScore = 10;
	
	private int MIN_COMMENT_LEN;
	private int MAX_COMMENT_LEN;
	
	//manager
	AuthenticateAccountManager mAuthenticateAccountManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.comment_edit);
		init();
		new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Util.showInputMethodWindow(CommentEditActivity.this, mEditText);
            }
        }, 300);
	}
	
	//init
	private void init() {
		initReceivedData();
		initAuthenticateAccountManager();
		initDimen();
		initDecorView();
		initTopTitle();
	}
	
	private void initReceivedData() {
		Intent intent = getIntent();
		Object obj = intent.getSerializableExtra(KEY_MEDIA_INFO);
		if(obj instanceof MediaInfo) {
			mMediaInfo = (MediaInfo) obj;
		}
	}
	
	private void initDimen() {
		MIN_COMMENT_LEN = getResources().getInteger(R.integer.comment_min_length);
		MAX_COMMENT_LEN = getResources().getInteger(R.integer.comment_max_length);
	}
	
	private void initDecorView() {
		ViewGroup decorView = (ViewGroup) getWindow().getDecorView();
		OnlineBg onlineBg = new OnlineBg(this);
		LayoutParams onlineBgParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		decorView.addView(onlineBg, 0, onlineBgParams);
	}
	
	private void initTopTitle() {
		mTopTitle = (TextView) findViewById(R.id.comment_edit_top_title);
		mEditText = (EditText) findViewById(R.id.comment_edit_et);
		mEditText.addTextChangedListener(mTextWatcher);
		mBtnBack = (TextView) findViewById(R.id.comment_edit_back);
		mBtnSend = (TextView) findViewById(R.id.comment_edit_send);
		mBtnBack.setOnClickListener(mOnClickListener);
		mBtnSend.setOnClickListener(mOnClickListener);
		mScoringView = (ScoringView) findViewById(R.id.comment_edit_score);
		String str = getResources().getString(R.string.media_name_comment);
		if(mMediaInfo != null) {
			str = String.format(str, mMediaInfo.medianame);
		}
		mTopTitle.setText(str);
	}
	
	//get data
	private void startAuthAccount() {
		if(mAuthenticateAccountManager.needAuthenticate()) {
			if(mAuthenticateAccountManager.isNoAccount()) {
				String msgStr = getResources().getString(R.string.login_tip_content);
				String negativeStr = getResources().getString(R.string.cancel);
				String positiveStr = getResources().getString(R.string.ok);
				
				AlertDialog loginTipDlg = new AlertDialog.Builder(this, miui.R.style.Theme_Light_Dialog_Alert).create();
				loginTipDlg.setCancelable(true);
				loginTipDlg.setTitle(R.string.login_tip_title);
				loginTipDlg.setMessage(msgStr);
		        loginTipDlg.setButton(AlertDialog.BUTTON_NEGATIVE, negativeStr, new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				 } );
		        loginTipDlg.setButton(AlertDialog.BUTTON_POSITIVE, positiveStr, new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						mAuthenticateAccountManager.authAccount();
					}
		        });
		        
				try {
					loginTipDlg.show();
				} catch(Exception e) {
					DKLog.d(TAG, e.getLocalizedMessage());
				}
			} else {
				mAuthenticateAccountManager.authAccount();
			}
		} else {
			uploadComment();
		}
	}
	
	//packaged method
	private void sendComment() {
		mCommentStr = getEditTextUser();
		int length = mCommentStr.length();
		if(length < MIN_COMMENT_LEN) {
			String str = getString(R.string.comment_count_less_tip);
			str = String.format(str, MIN_COMMENT_LEN);
			AlertMessage.show(str);
		} else if(!mScoringView.isUserRated()) {
			AlertMessage.show(R.string.click_star_rate);
		} else {
			startAuthAccount();
		}
	}
	
	private String getEditTextUser() {
		return mEditText.getText().toString().trim();
	}
	
	private void uploadComment() {
		mScore = mScoringView.geCurtScore();
		if(mMediaInfo != null) {
			DKApi.comment(mMediaInfo.mediaid, mScore, mCommentStr, 
					prepareCommentEditStatistic(), mObserver);
		}
	}
	
	//UI callback
	private OnClickListener mOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			int id = v.getId();
			if(id == R.id.comment_edit_back) {
	             Util.closeInputMethodWindow(CommentEditActivity.this);
	             finish();
			} else if(id == R.id.comment_edit_send) {
				sendComment();
			}
		}
	};
	
	private TextWatcher mTextWatcher = new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {

		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {

		}
		
		@Override
		public void afterTextChanged(Editable s) {
			String text = s.toString();
			if(text.length() == MAX_COMMENT_LEN) {
				String str = getString(R.string.comment_count_much_tip);
				str = String.format(str, MAX_COMMENT_LEN);
				AlertMessage.show(str);
			}
		}
	};
	
	//data callback
	private Observer mObserver = new Observer() {
		
		@Override
		public void onRequestCompleted(ServiceRequest request,
				ServiceResponse response) {
			if(response.isSuccessful()) {
				CommentEditActivity.this.finish();
			} else {
				AlertMessage.show(response.getDesc());
			}
		}

		@Override
		public void onProgressUpdate(ServiceRequest request, int progress) {
			
		}
	};
	
	//auth callback
	private void initAuthenticateAccountManager() {
		mAuthenticateAccountManager = new AuthenticateAccountManager(this) {
			
			@Override
			protected void onAuthSuccess() {
				uploadComment();
			}

			@Override
			protected void onAuthFailed(String failedReason) {
				AlertMessage.show(failedReason);
			}

			@Override
			protected void onAuthNoAccount() {
				
			}
			
		};
	}
	
	//statistic
	private String prepareCommentEditStatistic() {
		if(mMediaInfo != null) {
			CommentStatisticInfo statisticInfo = new CommentStatisticInfo();
			statisticInfo.mediaId = mMediaInfo.mediaid;
			statisticInfo.score = mScore;
			return statisticInfo.formatToJson();
		}
		return "";
	}
}
