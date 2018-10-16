package com.lzp.jsbridge.library;

public interface JsBridgeMsgHandler {
    void handleMessage(String msg, JsBridgeCallback callback);
}
