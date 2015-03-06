package com.xiaomi.miui.pushads.sdk.common;

/**
 * 这个用于当APP 需要非广告message 时接入， 和冒泡相当，不过这中message 是由APP 的server 自己发送消息
 * 并且自己进行解析。 不受到广告服务器，客户端的接受次数限制。这样做只是让APP 减少接入mipush sdk的麻烦。
 * 所有的逻辑由APP 自己完成
 * 在SDK 层面，当showType == 1000 的时候， 认为是这种OUTER 的消息，不过SERVER 发送的category 必须和
 * 我们的内部category 一致
 *
 * @author liuwei
 */
public interface IOuterMsgListener {
    public void onRecOuterMsg(String message);
}
