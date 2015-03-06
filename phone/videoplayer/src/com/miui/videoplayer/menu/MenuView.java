/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   MenuView.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-11-29
 */
package com.miui.videoplayer.menu;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.miui.video.R;

/**
 * @author tianli
 *
 */
public class MenuView extends RelativeLayout {

    private ImageView mIcon;
    private TextView mText;
    private View mLine;
    
    private MenuItem mItem;
    
    public MenuView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public MenuView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MenuView(Context context) {
        super(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mIcon = (ImageView)findViewById(R.id.menu_icon);
        mText = (TextView)findViewById(R.id.menu_text);
        mLine = findViewById(R.id.menu_line);
    }
    
    public void setMenuItem(MenuItem item){
        mItem = item;
        if(mItem != null){
            mIcon.setImageResource(mItem.getIcon());
            mText.setText(mItem.getText());
        }
    }
    
    public MenuItem getMenuItem(){
        return mItem;
    }
    
    public void setLineVisible(boolean visible){
        mLine.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

}
