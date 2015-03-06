package com.miui.video.widget.detail;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.miui.video.R;

public class DetailIntroduceView extends FrameLayout {

	private Context mContext;
	
	private View mDetailIntroduceView;
	private TextView mIntroduceView;
	private View mArrow;
	
	private int MAX_LINE = 4;
	
	public DetailIntroduceView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
		init();
	}

	public DetailIntroduceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		init();
	}

	public DetailIntroduceView(Context context) {
		super(context);
		this.mContext = context;
		init();
	}
	
	protected void setIntroduce(String introduce) {
		if(introduce == null) {
			return;
		}
		mIntroduceView.setText(introduce);
	}
	
	//init
	private void init() {
		mDetailIntroduceView = View.inflate(mContext, R.layout.detail_introduce, this);
		
		mIntroduceView = (TextView) mDetailIntroduceView.findViewById(R.id.detail_introduce);
		mIntroduceView.setMaxLines(MAX_LINE);
		mArrow = mDetailIntroduceView.findViewById(R.id.detail_introduce_arrow);
		mArrow.setOnClickListener(mOnClickListener);
	}
	
	//UI callback
	private OnClickListener mOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(v == mArrow) {
				boolean isSelected = mArrow.isSelected();
				mArrow.setSelected(!isSelected);
				if(isSelected) {
					mIntroduceView.setMaxLines(MAX_LINE);
				} else {
					mIntroduceView.setMaxLines(Integer.MAX_VALUE);
				}
			}
		}
	};
}
