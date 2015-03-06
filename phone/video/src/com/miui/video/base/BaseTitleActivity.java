package com.miui.video.base;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.miui.video.R;

/**
 *@author tangfuling
 *
 */
public abstract class BaseTitleActivity extends BaseActivity {

	private View mTitleTop;
	private TextView mTitleTopName;
	protected Button mBtnAction;
	
	protected void setTopTitle(CharSequence title) {
		mTitleTopName.setText(title);
	}
	
	protected void setTopTitle(int resid) {
		mTitleTopName.setText(resid);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getContentViewRes());
		initTitleTop();
	}
	
	//init
	private void initTitleTop() {
		mTitleTop = findViewById(R.id.title_top);
		mTitleTopName = (TextView) findViewById(R.id.title_top_name);
		mTitleTop.setOnClickListener(mOnClickListener);
		mBtnAction = (Button)findViewById(R.id.action_button);
		mBtnAction.setOnClickListener(mOnClickListener);
	}
	
	//UI callback
	private OnClickListener mOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			int id = v.getId();
			if(id == mTitleTop.getId()) {
				finish();
			}else if(id == R.id.action_button){
			    onActionClick();
			}
		}
	};
	
	protected abstract int getContentViewRes();
	
	protected void onActionClick(){
	    
	}
}
