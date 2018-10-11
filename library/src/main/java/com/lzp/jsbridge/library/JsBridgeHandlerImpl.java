package com.lzp.jsbridge.library;

import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebView;

import org.json.JSONObject;

public class JsBridgeHandlerImpl implements JsBridgeHandler {
    private WebView mWebView;
    private long mCallbackId = 0;
    private ArrayMap<Long, JsBridgeCallback> mCallbackMap = new ArrayMap<>();

    public JsBridgeHandlerImpl(WebView webView) {
        this.mWebView = webView;
    }

    /**
     * 向H5发消息
     *
     * @param msg
     */
    @Override
    public void sendMessage(String msg) {
        sendMessage(msg, null);
    }

    /**
     * 向H5发消息，带回调
     *
     * @param msg
     * @param callback
     */
    @Override
    public void sendMessage(String msg, JsBridgeCallback callback) {
        //javascript:JsBridge._handleNativeCall('{data:xxxxxx}')
        JsBridgeMsg jsMsg = new JsBridgeMsg();
        if (!TextUtils.isEmpty(msg)) {
            jsMsg.setData(msg);
        }
        if (callback != null) {
            long id = generateCallbackId();
            jsMsg.setCallbackId(id);
            mCallbackMap.put(id, callback);
        }

        String jsCmd = JsBridgeConstants.PREFIX + ":" + JsBridgeConstants.JSBRIDGE_INSTANCE + "." + JsBridgeConstants.CMD_NATIVE_CALL_JS + "('" + jsMsg.toString() + "')";
        mWebView.loadUrl(jsCmd);
    }

    @Override
    public void receiveMessage(String msg) {
        Log.e("Test", "receiveMessage=" + msg);
        long responseId = -1l;
        String data = null;
        try {
            JSONObject jsonObject = new JSONObject(msg);
            responseId = jsonObject.getLong("responseId");
            data = jsonObject.getString("data");
        } catch (Exception e) {
            Log.e("Test", "decode receivemsg error", e);
        }
        //根据responseId，查找callback
        //responseId等于navtive调用js时的callbackId
        JsBridgeCallback callback = mCallbackMap.get(responseId);
        if (callback != null) {
            callback.onCallback(data);
        }
    }

    private long generateCallbackId() {
        return ++mCallbackId;
    }
}
