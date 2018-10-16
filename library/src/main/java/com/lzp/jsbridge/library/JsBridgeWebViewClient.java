package com.lzp.jsbridge.library;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.lzp.jsbridge.library.util.JsBridgeUtil;

public class JsBridgeWebViewClient extends WebViewClient implements JsBridgeNativeInterface{
    private static final String JS_Loaded_FILE = "JsBridge.js";
    private JsBridgeHandler mjsBHandler;
    private Object mjsbInterface;

    public JsBridgeWebViewClient(WebView webView) {
        mjsBHandler = new JsBridgeHandlerImpl(webView);
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        String sourceCode = JsBridgeUtil.readFromAssets(view.getContext(), JS_Loaded_FILE);
        view.loadUrl("javascript:" + sourceCode);
        if (mjsbInterface != null) {
            mjsBHandler.registerJsBridgeInterface(mjsbInterface, JsBridgeUtil.scanJsbridgeInterceMethod(mjsbInterface));
        }

        //页面加载结束，清除JsBridgeHandler中没有处理的callback
        clear();
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        Log.e("Test", url);
        Uri uri = Uri.parse(url);
        //dispatch msg
        if (uri.getScheme().equals(JsBridgeConstants.SCHEMA)) {
            if (uri.getHost().equals(JsBridgeConstants.CMD_JS_RESPONSE)) {//handle js response
                mjsBHandler.handleJsResponse(JsBridgeUtil.getJsBridgeMsg(uri));
            } else if (uri.getHost().equals(JsBridgeConstants.CMD_JS_REQUEST)) {//handle js request
                mjsBHandler.handleJsRequest(JsBridgeUtil.getJsBridgeMsg(uri));
            }
        }
        return super.shouldOverrideUrlLoading(view, url);
    }

    @Override
    public void setJsBridgeInterce(Object jsBridgeInterce) {
        mjsbInterface = jsBridgeInterce;
    }

    @Override
    public void registeMsgHandler(JsBridgeMsgHandler handler) {
        mjsBHandler.registeMsgHandler(handler);
    }

    @Override
    public void request(String msg) {
        mjsBHandler.request(msg);
    }

    @Override
    public void request(String msg, JsBridgeCallback callback) {
        mjsBHandler.request(msg, callback);
    }

    @Override
    public void clear() {
        mjsBHandler.clear();
    }
}
