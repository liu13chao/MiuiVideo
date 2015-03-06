/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   MyActivity.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-12-9
 */
package com.miui.video;

import android.os.Bundle;

import com.miui.video.base.BaseTitleActivity;
import com.miui.video.fragment.MyVideoFragment;

/**
 * @author tianli
 *
 */
public class MyActivity extends BaseTitleActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }
    
    private void init(){
        setTopTitle(R.string.personal_center);
        MyVideoFragment fragment = new MyVideoFragment();
        getFragmentManager().beginTransaction().add(R.id.fragment_container, 
                fragment).commitAllowingStateLoss();
        getFragmentManager().executePendingTransactions();
    }
    
    @Override
    protected int getContentViewRes() {
        return R.layout.activity_personal_center;
    }

}
