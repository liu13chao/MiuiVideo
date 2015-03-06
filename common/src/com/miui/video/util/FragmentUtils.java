/**
 *  Copyright(c) 2014 XiaoMi TV Group
 *    
 *  FragmentUtils.java
 *
 *  @author tianli(tianli@xiaomi.com)
 *
 *  2014-11-9
 */
package com.miui.video.util;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;

/**
 * @author tianli
 *
 */
public class FragmentUtils {

    public static void removeFragment(Activity activity, Fragment fragment){
        if(activity != null){
            try{
                FragmentManager fragmentManager = activity.getFragmentManager();
                fragmentManager.executePendingTransactions();
                if(fragment.isAdded()){
                    FragmentTransaction ft = fragmentManager.beginTransaction();
                    ft.remove(fragment);
                    ft.commitAllowingStateLoss();
                    fragmentManager.executePendingTransactions();
                }
            }catch(Exception e){
            }
        }
    }
    
    public static void addFragment(Activity activity, int containerId, Fragment fragment){
        if(activity != null){
            try{
                FragmentManager fragmentManager = activity.getFragmentManager();
                fragmentManager.executePendingTransactions();
                if(!fragment.isAdded()){
                    FragmentTransaction ft = fragmentManager.beginTransaction();
                    ft.add(containerId, fragment);
                    ft.commitAllowingStateLoss();
                }
            }catch(Exception e){
            }
        }
    }
}
