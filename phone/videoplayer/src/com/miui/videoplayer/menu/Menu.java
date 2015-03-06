/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   Menu.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-11-29
 */
package com.miui.videoplayer.menu;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.miui.video.R;

/**
 * @author tianli
 *
 */
public class Menu extends FrameLayout{

    private List<MenuItem> mItems = new ArrayList<MenuItem>();
    private List<MenuView> mViews = new ArrayList<MenuView>();
    
    private ImageView mMask;
    private LinearLayout mViewContainer;
    
    private MenuActionListener mActionListener;
    
    public Menu(Context context) {
        super(context);
        init();
    }
    
    public Menu(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public Menu(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        mMask = new ImageView(getContext());
        LayoutParams p = new LayoutParams(LayoutParams.WRAP_CONTENT, 
                LayoutParams.MATCH_PARENT);
        mMask.setLayoutParams(p);
        mMask.setScaleType(ScaleType.FIT_XY);
        mMask.setImageResource(R.drawable.play_mask_right_s);
        p.gravity = Gravity.RIGHT;
        addView(mMask);
        
        mViewContainer = new LinearLayout(getContext());
        p = new LayoutParams(LayoutParams.WRAP_CONTENT, 
                LayoutParams.WRAP_CONTENT);
        mViewContainer.setOrientation(LinearLayout.VERTICAL);
        p.gravity = Gravity.RIGHT|Gravity.CENTER_VERTICAL;
        mViewContainer.setLayoutParams(p);
        addView(mViewContainer);
    }

    public void setMenuActionListener(MenuActionListener listener) {
        this.mActionListener = listener;
    }

    public boolean isEmpty(){
        return mItems.size() == 0;
    }
    
    public void setItems(List<MenuItem> items){
        mItems.clear();
        if(items != null){
            mItems.addAll(items);
        }
        if(mItems.size() > 0){
            mMask.setVisibility(View.VISIBLE);
            setVisibility(VISIBLE);
         }else{
            mMask.setVisibility(View.GONE);
            setVisibility(GONE);
        }
        refreshViews();
    }
    
    private void refreshViews(){
        int diff = mItems.size() - mViews.size();
        for(int i = 0; i < diff; i++){
            MenuView view = (MenuView)LayoutInflater.from(getContext()).inflate(R.layout.vp_menu_view, 
                    mViewContainer, false);
            mViewContainer.addView(view);
            view.setOnClickListener(mViewClickListener);
            mViews.add(view);
        }
        for(int i = 0; i < mViews.size(); i++){
            MenuView view = mViews.get(i);
            if(i < mItems.size()){
                view.setMenuItem(mItems.get(i));
                view.setVisibility(View.VISIBLE);
                view.setLineVisible(i != mItems.size() - 1);
            }else{
                view.setVisibility(View.GONE);
            }
        }
    }
    
    private OnClickListener mViewClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if(view instanceof MenuView){
                MenuItem item = ((MenuView)view).getMenuItem();
                if(item != null && mActionListener != null){
                    mActionListener.onMenuClick(item);
                }
            }
        }
    };
    
}
