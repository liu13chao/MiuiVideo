package com.miui.video.widget.recommend;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.miui.video.R;

public class MediaPagerTab extends FrameLayout {


    public static final String TAG = MediaPagerTab.class.getName();

    private Context mContext;

    private List<String> mTabs = new ArrayList<String>();
    private List<TextView> mTabViews = new ArrayList<TextView>();
    private List<View> mDividerViews = new ArrayList<View>();
    private  View mFocusBg;

    private LinearLayout mContentView;

    private int mTextSize;
    private int mTextColorNormal;
    private int mTextColorSelected;

    private int mTextWidth;
    private int mTextHeight;

    private int mCurPage;

    private int mMaxPage = 4;

    private OnTitleSelectedListener mListener;

    public MediaPagerTab(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MediaPagerTab(Context context) {
        super(context);
        init();
    }

    protected void setOnTitleSelectedListener(OnTitleSelectedListener listener) {
        this.mListener = listener;
    }

    protected void setTitle(List<String> titles) {
        mTabs.clear();
        if(titles != null) {
            mTabs.addAll(titles);
        }
        refreshCurPage();
    }

    public void setCurPage(int curPage) {
        if(mCurPage == curPage) {
            return;
        }
        this.mCurPage = curPage;
        refreshCurPage();
    }

    private void init() {
        this.mContext = getContext();
        mTextSize = mContext.getResources().getDimensionPixelSize(R.dimen.text_size_normal);
        mTextColorNormal = mContext.getResources().getColor(R.color.color_tab);
        mTextColorSelected = mContext.getResources().getColor(R.color.color_focus);
        mTextWidth = mContext.getResources().getDimensionPixelSize(R.dimen.recommend_home_tab_width);
        mTextHeight = mContext.getResources().getDimensionPixelSize(R.dimen.recommend_home_tab_height);
        addView(createBgView());
        mContentView = new LinearLayout(getContext());
        mContentView.setOrientation(LinearLayout.HORIZONTAL);
        addView(mContentView);
        initTabs();
    }

    private void initTabs(){
        for(int i = 0; i < mMaxPage; i++) {
            LayoutParams params = new LayoutParams(mTextWidth, mTextHeight);
            TextView textView = createTextView();
            textView.setGravity(Gravity.CENTER);
            textView.setTag(i);
            mTabViews.add(textView);
            mContentView.addView(textView, params);
            if( i  != mMaxPage - 1 ) {
                int dividerWidth = mContext.getResources().getDimensionPixelSize(
                        R.dimen.divider);
                LayoutParams dividerParams = new LayoutParams(dividerWidth, mTextHeight);
                View dividerView = new View(mContext);
                dividerView.setBackgroundResource(R.color.color_tab_line);
                dividerView.setTag(i);
                mDividerViews.add(dividerView);
                mContentView.addView(dividerView, dividerParams);
            }
        }
    }

    private TextView createTextView() {
        TextView textView = new TextView(mContext);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
        textView.setOnClickListener(mOnClickListener);
        return textView;
    }

    private View createBgView() {
        mFocusBg = new View(mContext);
        mFocusBg.setLayoutParams(new LayoutParams(getResources().getDimensionPixelSize(
                R.dimen.recommend_home_tab_bg_width), getResources().getDimensionPixelOffset(
                        R.dimen.recommend_home_tab_bg_height)));
        return mFocusBg;
    }

    private void refreshCurPage() {
        // refresh tabs.
        refreshTabs();
        refreshDividers();
        refreshTabBg();
    }

    private void refreshTabs(){
        for(int i = 0; i < mTabViews.size(); i++) {
            TextView textView = mTabViews.get(i);
            if(i >= mTabs.size()){
                textView.setVisibility(View.INVISIBLE);
            }else{
                textView.setVisibility(View.VISIBLE);
                textView.setText(mTabs.get(i));
                Object tag = textView.getTag();
                int position = (Integer) tag;
                if(mCurPage == position) {
                    textView.setTextColor(mTextColorSelected);
                } else {
                    textView.setTextColor(mTextColorNormal);
                }
            }
        }
    }

    private void refreshDividers(){
        // refresh dividers
        for(int i = 0; i < mDividerViews.size(); i++) {
            View view = mDividerViews.get(i);
            if(i >= mTabs.size() - 1){
                view.setVisibility(View.INVISIBLE);
            }else{
                Object tag = view.getTag();
                int position = (Integer) tag;
                if(position == mCurPage || position == mCurPage - 1) {
                    view.setVisibility(View.INVISIBLE);
                } else {
                    view.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void refreshTabBg(){
        if(mCurPage == 0){
            mFocusBg.setBackgroundResource(R.drawable.media_pager_tab_left);
        }else if(mCurPage == mMaxPage - 1){
            mFocusBg.setBackgroundResource(R.drawable.media_pager_tab_mid);
        }else{
            mFocusBg.setBackgroundResource(R.drawable.media_pager_tab_mid);
        }
        mFocusBg.setTranslationX(mCurPage * mTextWidth);
    }

    private void notifyTitleSelected(int position) {
        if(mListener != null) {
            mListener.onTitleSelected(position);
        }
    }

    //UI callback
    private OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Object tag = v.getTag();
            int position = (Integer) tag;
            setCurPage(position);
            notifyTitleSelected(position);
        }
    };

    //self def class
    public interface OnTitleSelectedListener {
        public void onTitleSelected(int position);
    }
}
