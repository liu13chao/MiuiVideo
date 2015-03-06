package com.miui.video.base;

import miui.app.Activity;
import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

import com.miui.video.SettingActivity;

public class BaseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initStyle();
        try{
            super.onCreate(savedInstanceState);
            ActionBar actionBar = getActionBar();
            if(actionBar != null) {
                actionBar.hide();
            }
        }catch(Exception e){
        }
    }

    protected void initStyle() {
        setTheme(miui.R.style.Theme_Light_NoTitle);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_MENU
                && event.getRepeatCount() > 0){
            startActivity(new Intent(this, SettingActivity.class));
        }
        return super.dispatchKeyEvent(event);
    }
    
    
}
