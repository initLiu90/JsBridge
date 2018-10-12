package com.lzp.jsbridge.library;

import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebView;

import org.json.JSONObject;

public class JsBridgeHandlerImpl implements JsBridgeHandler {
    private WebView mWebView;
    private long mSeq = 0;
    private ArrayMap<String, JsBridgeCallback> mCallbackMap = new ArrayMap<>();
    private JsBridgeMsgHandler mJsBMsgHandler;

    public JsBridgeHandlerImpl(WebView webView) {
        this.mWebView = webView;
    }

    /**
     * 向JsBridgeHandler注册一个js传给native消息的处理器
     *
     * @param handler
     */
    @Override
    public void registeMsgHandler(JsBridgeMsgHandler handler) {
        mJsBMsgHandler = handler;
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
        _sendMessage(jsMsg, callback);
    }

    private void _sendMessage(JsBridgeMsg jsMsg, JsBridgeCallback callback) {
        if (callback != null) {
            String id = generateCallbackId();
            jsMsg.setCallbackId(id);
            mCallbackMap.put(id, callback);
        }

        String jsCmd = JsBridgeConstants.PREFIX + ":" + JsBridgeConstants.JSBRIDGE_INSTANCE + "." + JsBridgeConstants.CMD_NATIVE_CALL_JS + "('" + jsMsg.toString() + "')";
        mWebView.loadUrl(jsCmd);
    }

    @Override
    public void receiveMessage(String msg) {
        Log.e("Test", "receiveMessage=" + msg);
        String responseId = null;
        String callbackId = null;
        String data = null;
        boolean isResponse = false;//native调用js后，js的callback
        try {
            JSONObject jsonObject = new JSONObject(msg);
            if (jsonObject.has("responseId")) {
                responseId = jsonObject.getString("responseId");
                isResponse = true;
            }
            if (jsonObject.has("callbackId")) {
                callbackId = jsonObject.getString("callbackId");
            }
            data = jsonObject.getString("data");
        } catch (Exception e) {
            Log.e("Test", "decode receivemsg error", e);
        }

        if (isResponse) {
            //根据responseId，查找callback
            //responseId等于navtive调用js时的callbackId
            JsBridgeCallback callback = mCallbackMap.get(responseId);
            if (callback != null) {
                callback.onCallback(data);
            }
        } else {//js 直接调用native
            Log.e("Test", "receive js call:" + data);
            if (mJsBMsgHandler != null) {
                final String tmpResponseId = callbackId;
                mJsBMsgHandler.onReceive(data, TextUtils.isEmpty(callbackId) ? null : new JsBridgeCallback() {
                    @Override
                    public void onCallback(String msg) {
                        JsBridgeMsg jsMsg = new JsBridgeMsg();
                        jsMsg.setData(msg);
                        jsMsg.setResponseId(tmpResponseId);
                        _sendMessage(jsMsg, null);
                    }
                });
            }
        }
    }

    private String generateCallbackId() {
        return "cb_" + (++mSeq) + "_" + System.currentTimeMillis();
    }
}
