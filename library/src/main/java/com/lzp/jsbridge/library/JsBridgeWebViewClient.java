package com.lzp.jsbridge.library;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.lzp.jsbridge.library.util.JsBridgeUtil;

public class JsBridgeWebViewClient extends WebViewClient implements JsBridgeHandler {
    private static final String JS_Loaded_FILE = "JsBridge.js";
    private JsBridgeHandlerImpl mjsBHandler;

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        mjsBHandler = new JsBridgeHandlerImpl(view);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        Log.e("Test", url);
        Uri uri = Uri.parse(url);
        if (uri.getScheme().equals(JsBridgeConstants.SCHEMA)) {
            if (uri.getHost().equals(JsBridgeConstants.CMD_JS_CALL_NATIVE)) {//js调用native
                //去掉前面的反斜杠
                String msg = uri.getPath().substring(1, uri.getPath().length());
                receiveMessage(msg);
            }
        }
        return super.shouldOverrideUrlLoading(view, url);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        String sourceCode = JsBridgeUtil.readFromAssets(view.getContext(), JS_Loaded_FILE);
        view.loadUrl("javascript:" + sourceCode);
    }

    @Override
    public void sendMessage(String msg) {
        mjsBHandler.sendMessage(msg);
    }

    @Override
    public void sendMessage(String msg, JsBridgeCallback callback) {
        mjsBHandler.sendMessage(msg, callback);
    }

    @Override
    public void receiveMessage(String msg) {
        mjsBHandler.receiveMessage(msg);
    }
}
