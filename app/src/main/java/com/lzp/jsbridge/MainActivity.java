//package com.lzp.jsbridge;
//
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.webkit.ConsoleMessage;
//import android.webkit.WebChromeClient;
//import android.webkit.WebView;
//
//import com.lzp.jsbridge.library.JsBridgeCallback;
//import com.lzp.jsbridge.library.JsBridgeCallbackHandler;
//import com.lzp.jsbridge.library.JsBridgeWebViewClient;
//
//import org.json.JSONObject;
//
//import java.util.HashSet;
//
//public class MainActivity extends AppCompatActivity implements JsBridgeCallbackHandler {
//    WebView mWebView;
//    JsBridgeWebViewClient mWebViewClient;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        mWebView = findViewById(R.id.webview);
//        initWebView();
//
//        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mWebViewClient.request("hello", new JsBridgeCallback() {
//                    @Override
//                    public void onCallback(String msg) {
//                        Log.e("Test", "native:response--->" + msg);
//                    }
//                });
//                mWebViewClient.request("hello222", new JsBridgeCallback() {
//                    @Override
//                    public void onCallback(String msg) {
//                        Log.e("Test", "native:response--->" + msg);
//                    }
//                });
//            }
//        });
//    }
//
//    private void initWebView() {
//        mWebView.getSettings().setJavaScriptEnabled(true);
//        mWebView.setWebChromeClient(new WebChromeClient() {
//            @Override
//            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
//                Log.e("Test", "consoleMessage:" + consoleMessage.message());
//                return super.onConsoleMessage(consoleMessage);
//            }
//        });
//        mWebViewClient = new JsBridgeWebViewClient(mWebView);
//        mWebViewClient.registeMsgHandler(this);
//        mWebView.setWebViewClient(mWebViewClient);
//        mWebView.loadUrl("file:///android_asset/index.html");
//    }
//
//    @Override
//    public void handleCallback(String msg, JsBridgeCallback callback) {
//        Log.e("Test", "receive js call native msg:" + msg);
//        if (callback != null) {
//            callback.onCallback(msg);
//        }
//    }
//}
