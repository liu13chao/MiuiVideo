/**
 *   Copyright(c) 2013 XiaoMi TV Group
 *
 *   AbsParser.java
 *
 *   @author tianli (tianli@xiaomi.com)
 *
 *   @date 2013-10-13
 */
package com.xiaomi.mitv.common.webservice;

/**
 * @author tianli
 *
 */
public interface AbsParser<T> {
    public abstract T parse(byte[] buf, String encode);
}
