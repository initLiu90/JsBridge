package com.lzp.jsbridge.library;

public interface JsBridgeNativeInterface {
    /**
     * 设置JsBridgeInterface
     *
     * @param jsBridgeInterce
     */
    void setJsBridgeInterce(Object jsBridgeInterce);

    /**
     * Native端处理js请求消息的Handler
     *
     * @param handler
     */
    void registeMsgHandler(JsBridgeMsgHandler handler);

    /**
     * Native请求js
     *
     * @param msg
     */
    void request(String msg);

    /**
     * Native请求js
     *
     * @param msg
     * @param callback
     */
    void request(String msg, JsBridgeCallback callback);

    void clear();
}
