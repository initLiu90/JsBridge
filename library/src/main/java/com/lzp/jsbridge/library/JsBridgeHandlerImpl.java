package com.lzp.jsbridge.library;

import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebView;

import com.lzp.jsbridge.library.util.JsBridgeUtil;

public class JsBridgeHandlerImpl implements JsBridgeHandler {
    private WebView mWebView;
    private long mSeq = 0;
    private ArrayMap<String, JsBridgeCallback> mCallbackMap = new ArrayMap<>();
    private JsBridgeCallbackHandler mJsbCbHandler;

    public JsBridgeHandlerImpl(WebView webView) {
        this.mWebView = webView;
    }

    /**
     * 向JsBridgeHandler注册一个js传给native消息的处理器
     *
     * @param handler
     */
    @Override
    public void registeMsgHandler(JsBridgeCallbackHandler handler) {
        mJsbCbHandler = handler;
    }

    /**
     * native request js
     *
     * @param msg
     */
    @Override
    public void request(String msg) {
        request(msg, null);
    }

    /**
     * native request js
     *
     * @param msg
     * @param callback
     */
    @Override
    public void request(String msg, JsBridgeCallback callback) {
        JsBridgeMsg jsMsg = new JsBridgeMsg();
        if (!TextUtils.isEmpty(msg)) {
            jsMsg.setData(msg);
        }
        realRequest(jsMsg, callback);
    }

    /**
     * real native request js
     *
     * @param jsbMsg
     * @param callback
     */
    private void realRequest(JsBridgeMsg jsbMsg, JsBridgeCallback callback) {
        if (callback != null) {
            String id = generateCallbackId();
            jsbMsg.setCallbackId(id);
            mCallbackMap.put(id, callback);
        }
        String jsCmd = JsBridgeConstants.PREFIX + ":" + JsBridgeConstants.JSBRIDGE_INSTANCE + "." + JsBridgeConstants.CMD_NATIVE_REQUEST_JS + "('" + JsBridgeUtil.formatJsBridgeMsgJsonStr(jsbMsg.toString()) + "')";
        mWebView.loadUrl(jsCmd);
    }

    /**
     * handle js request native
     *
     * @param msg
     */
    @Override
    public void handleJsRequest(String msg) {
        Log.e("Test", "native:handleJsRequest--->" + msg);
        final JsBridgeMsg jsbMsg = JsBridgeUtil.decodeJsBridgeMsg(msg);
        if (mJsbCbHandler != null) {
            mJsbCbHandler.handleCallback(jsbMsg.getData(), TextUtils.isEmpty(jsbMsg.getCallbackId()) ? null : new JsBridgeCallback() {
                @Override
                public void onCallback(String responseData) {
                    response(jsbMsg, responseData);
                }
            });
        }
    }

    /**
     * native response to js
     *
     * @param reqMsg       js发来的请求
     * @param responseData response的内容
     */
    private void response(JsBridgeMsg reqMsg, String responseData) {
        JsBridgeMsg rspMsg = new JsBridgeMsg();
        rspMsg.setData(responseData);
        rspMsg.setResponseId(reqMsg.getCallbackId());

        String jsCmd = JsBridgeConstants.PREFIX + ":" + JsBridgeConstants.JSBRIDGE_INSTANCE + "." + JsBridgeConstants.CMD_NATIVE_RESPONSE_JS + "('" + JsBridgeUtil.formatJsBridgeMsgJsonStr(rspMsg.toString()) + "')";
        mWebView.loadUrl(jsCmd);
    }

    /**
     * handle js response to native
     *
     * @param msg
     */
    @Override
    public void handleJsResponse(String msg) {
        Log.e("Test", "native:handleJsResponse--->" + msg);
        JsBridgeMsg jsbMsg = JsBridgeUtil.decodeJsBridgeMsg(msg);
        //根据responseId，查找callback
        //responseId等于navtive调用js时的callbackId
        JsBridgeCallback callback = mCallbackMap.get(jsbMsg.getResponseId());
        if (callback != null) {
            callback.onCallback(jsbMsg.getData());
        }
    }

    private String generateCallbackId() {
        return "cb_" + (++mSeq) + "_" + System.currentTimeMillis();
    }

    @Override
    public void clear() {
        mCallbackMap.clear();
    }
}
