/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   MainActivity.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-11-1
 */
package com.miui.video;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.miui.video.base.BaseActivity;
import com.miui.video.model.AppConfig;
import com.miui.video.model.AppSettings;
import miui.app.AlertDialog;

/**
 * @author tianli
 *
 */
public class HomeActivity extends BaseActivity{
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        go();
    }
    
    @Override
    protected void initStyle() {
        setTheme(miui.R.style.Theme_Light_Dialog);
    }

    private void go(){
        if(DKApp.getSingleton(AppSettings.class).isAlertNetworkOn()){
            showDeclaration();
        }else{
            goToMain();
        }
    }
    
    private void goToMain(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
    
    private void showDeclaration() {
        View contentView = View.inflate(this, R.layout.declaration_view, null);
        TextView declarationView = (TextView) contentView.findViewById(R.id.declaration_content);
        declarationView.setMovementMethod(LinkMovementMethod.getInstance());
        String negativeStr = getResources().getString(R.string.cancel);
        String positiveStr = "";
        AppConfig config = DKApp.getSingleton(AppConfig.class);
        if(config.isOnlineVideoOn()){
            positiveStr = getResources().getString(R.string.declaration_allow);
        }else{
            positiveStr = getResources().getString(R.string.ok);
            declarationView.setText(R.string.declaration_content_td_custom);
        }
        final CheckBox checkBox = (CheckBox) contentView.findViewById(R.id.no_longer_tips);
        checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                DKApp.getSingleton(AppSettings.class).setAlertNetworkOn(!isChecked);
            }
        });
        AlertDialog dialog = new AlertDialog.Builder(this, miui.R.style.Theme_Light_Dialog_Alert).create();
        dialog.setTitle(R.string.declaration);
        dialog.setView(contentView);
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, negativeStr, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        } );
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, positiveStr, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DKApp.getSingleton(AppSettings.class).setAlertNetworkOn(!checkBox.isChecked());
                dialog.dismiss();
                goToMain();
            }
        });
        try {
            dialog.setCancelable(false);
            dialog.show();
        } catch (Exception e) {
        }
    }
}
