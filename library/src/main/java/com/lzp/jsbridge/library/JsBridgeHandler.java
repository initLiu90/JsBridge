package com.lzp.jsbridge.library;

public interface JsBridgeHandler {
    /**
     * 向JsBridgeHandler注册一个js传给native消息的处理器
     * @param handler
     */
    void registeMsgHandler(JsBridgeCallbackHandler handler);

    /**
     * native request js
     *
     * @param msg
     */
    void request(String msg);

    /**
     * native request js
     *
     * @param msg
     * @param callback
     */
    void request(String msg, JsBridgeCallback callback);

    /**
     * handle js response to native
     * @param msg
     */
    void handleJsResponse(String msg);

    /**
     * handle js request native
     * @param msg
     */
    void handleJsRequest(String msg);

    /**
     * 清除操作
     */
    void clear();
}
