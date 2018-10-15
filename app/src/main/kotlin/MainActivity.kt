package com.lzp.jsbridge

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.webkit.ConsoleMessage
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.Button
import com.lzp.jsbridge.library.JsBridgeCallback
import com.lzp.jsbridge.library.JsBridgeCallbackHandler
import com.lzp.jsbridge.library.JsBridgeWebViewClient

class MainActivity : AppCompatActivity(), JsBridgeCallbackHandler {
    private var mWebView: WebView? = null
    private var mWebViewClient: JsBridgeWebViewClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mWebView = findViewById(R.id.webview)
        findViewById<Button>(R.id.btn).setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                mWebViewClient?.request("hello", object : JsBridgeCallback {
                    override fun onCallback(msg: String?) {
                        Log.e("Test", "native:response--->" + msg)
                    }
                });
            }
        })
        initWebView()
    }

    private fun initWebView() {
        mWebView?.getSettings()?.setJavaScriptEnabled(true);
        mWebView?.setWebChromeClient(object : WebChromeClient() {
            override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                Log.e("Test", "consoleMessage:" + consoleMessage?.message())
                return super.onConsoleMessage(consoleMessage)
            }
        });
        mWebViewClient = JsBridgeWebViewClient(mWebView);
        mWebViewClient?.registeMsgHandler(this);
        mWebView?.setWebViewClient(mWebViewClient);
        mWebView?.loadUrl("file:///android_asset/index.html");
    }

    override fun handleCallback(msg: String?, callback: JsBridgeCallback?) {
        callback?.onCallback(msg)
    }
}