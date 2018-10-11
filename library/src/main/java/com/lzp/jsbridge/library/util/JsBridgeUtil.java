package com.lzp.jsbridge.library.util;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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
}
