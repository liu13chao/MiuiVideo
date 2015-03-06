/**
 *   Copyright(c) 2012 DuoKan TV Group
 *
 *   ServerErrorException.java
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
public class ServerErrorException extends Exception
{
    private static final long serialVersionUID = 7827668695113603186L;
    private int errorCode; 
    private String errorMessage;
    
    public ServerErrorException(int errorCode)
    {
        super();
        this.errorCode = errorCode;
    }

    public ServerErrorException(int errorCode , String detailMessage)
    {
        super(detailMessage);
        this.errorCode = errorCode;
        this.errorMessage = detailMessage;
    }

    public int getErrorCode()
    {
        return errorCode;
    }

    public void setErrorCode(int errorCode)
    {
        this.errorCode = errorCode;
    }

    public String getErrorMessage()
    {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage)
    {
        this.errorMessage = errorMessage;
    }
    
}
