/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   MilinkView.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-12-1
 */
package com.miui.videoplayer.widget;

import com.miui.video.R;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @author tianli
 *
 */
public class MilinkView extends RelativeLayout {

    TextView mMilinkText;
    
    public MilinkView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public MilinkView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MilinkView(Context context) {
        super(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mMilinkText = (TextView)findViewById(R.id.milink_text);
    }

    public void setPlayingDevice(String deviceName){
        if(!TextUtils.isEmpty(deviceName)){
            String playInString = mContext.getString(R.string.airkan_playing_to);
            mMilinkText.setText(playInString + deviceName);
        }
    }
    
}
