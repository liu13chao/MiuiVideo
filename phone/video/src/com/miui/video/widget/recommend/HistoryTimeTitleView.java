/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   HistoryTimeTitleView.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-11-16
 */
package com.miui.video.widget.recommend;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.miui.video.R;
import com.miui.video.util.TimeUtils;

/**
 * @author tianli
 *
 */
public class HistoryTimeTitleView  extends FrameLayout{

    private TextView mDateView;
    
    public HistoryTimeTitleView(Context context, AttributeSet attrs,
            int defStyle) {
        super(context, attrs, defStyle);
    }

    public HistoryTimeTitleView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HistoryTimeTitleView(Context context) {
        super(context);
    }
    
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        View view = findViewById(R.id.time_text);
        if(view instanceof TextView){
            mDateView = (TextView)view;
        }
    }

    public void setPlayDate(String date){
        if(mDateView != null){
            if(date == null){
                date = "";
            }
            mDateView.setText(parseDate(date));
        }
    }
    
    private String parseDate(String date){
        String[] split = null;
        if(date != null){
            split = date.split("-");
        }
        if(split != null && split.length == 3){
            int thisYear = TimeUtils.getYear();
            int thisMonth = TimeUtils.getMonth();
            int thisDay = TimeUtils.getDay();
            int YesYear = TimeUtils.getYesterdayYear();
            int YesMonth = TimeUtils.getYesterdayMonth();
            int YesDay = TimeUtils.getYesterdayDay();
            int year = 0, month = 0, day = 0;
            try{
                year = Integer.parseInt(split[0]);
                month = Integer.parseInt(split[1]);
                day = Integer.parseInt(split[2]);
            }catch(Exception e){
            }
            if(year == thisYear && month == thisMonth && day == thisDay){
                return getContext().getString(R.string.today);
            }else if(year == YesYear && month == YesMonth && day == YesDay){
                return getContext().getString(R.string.yesterday);
            }
        }
        return date;
    }
}
