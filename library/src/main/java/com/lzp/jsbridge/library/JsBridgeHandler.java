package com.lzp.jsbridge.library;

public interface JsBridgeHandler {

    /**
     * 向H5发消息
     *
     * @param msg
     */
    void sendMessage(String msg);

    /**
     * 向H5发消息
     *
     * @param msg
     * @param callback
     */
    void sendMessage(String msg, JsBridgeCallback callback);

    /**
     * 接收到H5发来的消息
     */
    void receiveMessage(String msg);
}
