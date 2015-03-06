package com.xiaomi.miui.pushads.sdk;

import java.io.File;
import java.util.Comparator;

/**
 * 用于删除过期的广告图片，比较广告图片的时间
 * @author liuwei
 *
 */

class FileComparatorByLastModifier implements Comparator<File> {

    public FileComparatorByLastModifier() {
    }

    @Override
    public int compare(File file1, File file2) {
        long  time1 = file1.lastModified();
        long  time2 = file2.lastModified();

        //the default order is from smaller to bigger, but we need bigger to smaller
        if (time1 > time2) return 1;
        else if(time1 < time2) return -1;
        else return 1;
    }
}