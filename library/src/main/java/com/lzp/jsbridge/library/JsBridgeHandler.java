package com.lzp.jsbridge.library;

public interface JsBridgeHandler {
    /**
     * 向JsBridgeHandler注册一个js传给native消息的处理器
     * @param handler
     */
    void registeMsgHandler(JsBridgeMsgHandler handler);

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
