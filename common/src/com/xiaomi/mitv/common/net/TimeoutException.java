/**
 *   Copyright(c) 2012 DuoKan TV Group
 *
 *   TimeoutException.java
 *
 *   @author tianli (tianli@duokan.com)
 *
 *   @date 2012-6-23
 */
package com.xiaomi.mitv.common.net;

/**
 * @author tianli
 *
 */
public class TimeoutException extends Exception
{

    private static final long serialVersionUID = -7968626919407570100L;

    public TimeoutException()
    {
        super();
    }

    public TimeoutException(String detailMessage, Throwable throwable)
    {
        super(detailMessage, throwable);
    }

    public TimeoutException(String detailMessage)
    {
        super(detailMessage);
    }

    public TimeoutException(Throwable throwable)
    {
        super(throwable);
    }

}
