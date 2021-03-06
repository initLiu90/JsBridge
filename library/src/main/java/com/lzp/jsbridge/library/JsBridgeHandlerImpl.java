package com.lzp.jsbridge.library;

import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebView;

import com.lzp.jsbridge.library.util.JsBridgeUtil;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class JsBridgeHandlerImpl implements JsBridgeHandler {
    private WebView mWebView;
    private long mSeq = 0;
    private ArrayMap<String, JsBridgeCallback> mCallbackMap = new ArrayMap<>();
    private JsBridgeMsgHandler mJsbMsgHandler;
    private Object mjsbInterface;

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
        mJsbMsgHandler = handler;
    }

    /**
     * 向js注册提供给js调用的方法
     */
    @Override
    public void registerJsBridgeInterface(Object jsbInterface) {
        mjsbInterface = jsbInterface;
        String methodNames = JsBridgeUtil.scanJsbridgeInterceMethod(jsbInterface);
        String instanceName = JsBridgeUtil.getJsbridgeInstanceName(jsbInterface);
        Map<String, String> data = new HashMap<>();
        data.put("instanceName", instanceName);
        data.put("methodNames", methodNames);
        String dataStr = JsBridgeUtil.ecodeString2Json(data);

        JsBridgeMsg msg = new JsBridgeMsg();
        msg.setData(dataStr);
        realRequest(msg, JsBridgeConstants.CMD_NATIVE_REGISTE_REQUEST_JS, null);
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
        realRequest(jsMsg, JsBridgeConstants.CMD_NATIVE_REQUEST_JS, callback);
    }

    /**
     * real native request js
     *
     * @param jsbMsg
     * @param callback
     */
    private void realRequest(JsBridgeMsg jsbMsg, String cmd, JsBridgeCallback callback) {
        if (callback != null) {
            String id = generateCallbackId();
            jsbMsg.setCallbackId(id);
            mCallbackMap.put(id, callback);
        }
        String jsCmd = JsBridgeConstants.PREFIX + ":" + JsBridgeConstants.JSBRIDGE_INSTANCE + "." + cmd + "('" + JsBridgeUtil.formatJsBridgeMsgJsonStr(jsbMsg.toString()) + "')";
        mWebView.loadUrl(jsCmd);
    }

    /**
     * handle js request native
     *
     * @param msg
     */
    @Override
    public void handleJsRequest(final String msg) {
        Log.e("Test", "native:handleJsRequest--->" + msg);
        final JsBridgeMsg jsbMsg = JsBridgeUtil.decodeJsBridgeMsg(msg);
        if (!callJsBridgeInterfaceMethod(jsbMsg)) {
            if (mJsbMsgHandler != null) {
                mJsbMsgHandler.handleMessage(jsbMsg.getData(), TextUtils.isEmpty(jsbMsg.getCallbackId()) ? null : new JsBridgeCallback() {
                    @Override
                    public void onCallback(String responseData) {
                        response(jsbMsg, responseData);
                    }
                });
            }
        }
    }

    /**
     * 根据js调用的native端的方法名，调用native端对应的方法
     *
     * @param jsbMsg
     * @return true 找到了native端对应的方法并调用成功，false 没有找到native端方法或调用失败
     */
    private boolean callJsBridgeInterfaceMethod(final JsBridgeMsg jsbMsg) {
        if (jsbMsg.getMethodName() != null && !jsbMsg.getMethodName().equals("")) {
            Class<?>[] params;
            if (!TextUtils.isEmpty(jsbMsg.getCallbackId())) {
                params = new Class<?>[]{String.class, JsBridgeCallback.class};
            } else {
                params = new Class<?>[]{String.class};
            }
            try {
                Method method = mjsbInterface.getClass().getDeclaredMethod(jsbMsg.getMethodName(), params);
                method.setAccessible(true);
                if (!TextUtils.isEmpty(jsbMsg.getCallbackId())) {
                    method.invoke(mjsbInterface, jsbMsg.getData(), new JsBridgeCallback() {
                        @Override
                        public void onCallback(String responseData) {
                            response(jsbMsg, responseData);
                        }
                    });
                } else {
                    method.invoke(mjsbInterface, jsbMsg.getData());
                }
                return true;
            } catch (Exception e) {
                Log.e("Test", "call method:" + jsbMsg.getMethodName() + " error:", e);
            }
        }
        return false;
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
