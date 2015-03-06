/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   MenuFactory.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-11-29
 */
package com.miui.videoplayer.menu;

import com.miui.video.R;

/**
 * @author tianli
 *
 */
public class MenuFactory {

    public static MenuItem createSetting(){
        return new MenuItem(MenuIds.MENU_ID_COMMON_SETTING, R.string.vp_function, 
                R.drawable.play_setting_icon);
    }
    
    public static MenuItem createOffline(){
        return new MenuItem(MenuIds.MENU_ID_ONLINE_OFFLINE, R.string.vp_offline, 
                R.drawable.play_offline_icon);
    }
    
    public static MenuItem createMilink(){
        return new MenuItem(MenuIds.MENU_ID_COMMON_MILINK, R.string.vp_milink_function, 
                R.drawable.play_miracast_icon);
    }
    
    public static MenuItem createEp(){
        return new MenuItem(MenuIds.MENU_ID_ONLINE_EP, R.string.vp_select_ci, 
                R.drawable.play_series_icon);
    }
    
}
