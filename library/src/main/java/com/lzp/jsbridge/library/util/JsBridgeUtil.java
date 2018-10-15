package com.lzp.jsbridge.library.util;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.lzp.jsbridge.library.JsBridgeMsg;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Parameter;
import java.util.regex.Pattern;

public class JsBridgeUtil {
    private static final String TAG = "JsBridgeUtil";

    public static String readFromAssets(Context context, String fileName) {
        BufferedReader bRead = null;
        try {
            bRead = new BufferedReader(new InputStreamReader(context.getAssets().open(fileName)));
            String str;
            StringBuilder sb = new StringBuilder();
            while ((str = bRead.readLine()) != null) {
                sb.append(str);
            }
            return sb.toString();
        } catch (IOException e) {
            throw new RuntimeException("read file:" + fileName + " from asstes error", e);
        } finally {
            try {
                if (bRead != null) {
                    bRead.close();
                    bRead = null;
                }
            } catch (Exception e) {
                Log.e("Test", "error:", e);
            }
        }
    }

    /**
     * 将字符串类型的jsBridgeMsg转为JsBridgeMsg对象
     *
     * @param jsBridgeMsg
     * @return JsBridgeMsg
     */
    public static JsBridgeMsg decodeJsBridgeMsg(String jsBridgeMsg) {
        JsBridgeMsg msg = new JsBridgeMsg();
        try {
            JSONObject jsonObject = new JSONObject(jsBridgeMsg);
            if (jsonObject.has("responseId")) {
                msg.setResponseId(jsonObject.getString("responseId"));
            }
            if (jsonObject.has("callbackId")) {
                msg.setCallbackId(jsonObject.getString("callbackId"));
            }
            msg.setData(jsonObject.getString("data"));
        } catch (Exception e) {
            Log.e("Test", "decode receivemsg error", e);
        }
        return msg;
    }

    public static String getJsBridgeMsg(Uri uri) {
        //去掉前面的反斜杠
        String msg = uri.getPath().substring(1, uri.getPath().length());
        return msg;
    }

    /**
     * @return
     */
    public static String formatJsBridgeMsgJsonStr(String jsonJsbMsg) {
        //{"data":"{"tttttt":"js send msg to native"}","responseId":"cb_1_1539339480568"}
        jsonJsbMsg = jsonJsbMsg.replaceAll("\"\\{", "\\{");
        jsonJsbMsg = jsonJsbMsg.replaceAll("\\}\"", "\\}");
        return jsonJsbMsg;
    }
}
