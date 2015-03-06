package com.miui.video.widget.searchbox;

import com.miui.video.R;
import com.miui.video.util.Util;
import com.miui.video.widget.searchbox.EditTextIME.DispatchKeyEventPreImeListener;
import android.content.Context;
import android.content.res.Resources;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView.OnEditorActionListener;

public class SearchBox extends RelativeLayout {

	private Context mContext;
	
	private EditTextIME mEditText;
	private Button mBtnDel;
	
	private int mBtnDelWidth;
	private int mBtnDelHeight;
	
	public SearchBox(Context context) {
		super(context);
		this.mContext = context;
		init();
	}
	
	public SearchBox(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		init();
	}
	
	public SearchBox(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
		init();
	}
	
	public void setText(CharSequence text) {
        mEditText.setText(text);
    }
	
	public void setText(int resid) {
		mEditText.setText(resid);
    }
	
	public Editable getText() {
	    return mEditText.getText();
	}
	
	public void setHint(CharSequence hint) {
		mEditText.setHint(hint);
	}
	
	public void setHint(int resid) {
        mEditText.setHint(resid);
    }
	
	public void setCursorVisible(boolean visible) {
		mEditText.setCursorVisible(visible);
	}
	
	public CharSequence getHint() {
	    return mEditText.getHint();
	}
	
	public void addTextChangedListener(TextWatcher watcher) {
		mEditText.addTextChangedListener(watcher);
	}
	
	public void setOnEditorActionListener(OnEditorActionListener l) {
		mEditText.setOnEditorActionListener(l);
	}
	
	public void setDispatchKeyEventPreImeListener(DispatchKeyEventPreImeListener listener) {
		mEditText.setDispatchKeyEventPreImeListener(listener);
	}
	
	public void setOnTouchListener(OnTouchListener l) {
		mEditText.setOnTouchListener(l);
	}
	
	//init
	private void init() {
		initDimen();
		initUI();
	}
	
	private void initDimen() {
		Resources res = getResources();
		mBtnDelWidth = res.getDimensionPixelSize(R.dimen.search_box_btn_del_width);
		mBtnDelHeight = res.getDimensionPixelSize(R.dimen.search_box_btn_del_height);
	}
	
	private void initUI() {
		setBackgroundResource(R.drawable.search_bg);
		
		mEditText = (EditTextIME) View.inflate(mContext, R.layout.search_box_edit_text, null);
		mEditText.setPadding(0, 0, 0, 0);
		mEditText.addTextChangedListener(mTextWatcher);
		LayoutParams editTextParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		editTextParams.addRule(RelativeLayout.CENTER_VERTICAL);
		editTextParams.rightMargin = mBtnDelWidth;
		addView(mEditText, editTextParams);
		
		mBtnDel = new Button(mContext);
		mBtnDel.setOnClickListener(mOnClickListener);
		mBtnDel.setBackgroundResource(R.drawable.btn_search_delete);
		LayoutParams btnDelParams = new LayoutParams(mBtnDelWidth, mBtnDelHeight);
		btnDelParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		btnDelParams.addRule(RelativeLayout.CENTER_VERTICAL);
		addView(mBtnDel, btnDelParams);
	}
	
	//UI callback
	private OnClickListener mOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(v == mBtnDel) {
				mEditText.setText("");
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
			if(!Util.isEmpty(text)) {
				mBtnDel.setVisibility(View.VISIBLE);
			} else {
				mBtnDel.setVisibility(View.INVISIBLE);
			}
		}
	};
}
