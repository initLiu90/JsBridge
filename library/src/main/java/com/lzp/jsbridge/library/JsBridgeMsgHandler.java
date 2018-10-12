package com.lzp.jsbridge.library;

public interface JsBridgeMsgHandler {
    void onReceive(String msg, JsBridgeCallback callback);
}
