/**
 *   Copyright(c) 2012 DuoKan TV Group
 *
 *   NetworkErrorException.java
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
public class NetworkErrorException extends Exception
{
    private static final long serialVersionUID = -5699387398793554670L;

    public NetworkErrorException()
    {
        super();
    }

    public NetworkErrorException(String detailMessage, Throwable throwable)
    {
        super(detailMessage, throwable);
    }

    public NetworkErrorException(String detailMessage)
    {
        super(detailMessage);
    }

    public NetworkErrorException(Throwable throwable)
    {
        super(throwable);
    }

}
