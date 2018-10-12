package com.lzp.jsbridge.library;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONObject;

class JsBridgeMsg {
    private String data;
    private String callbackId;
    private String responseId;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getCallbackId() {
        return callbackId;
    }

    public void setCallbackId(String callbackId) {
        this.callbackId = callbackId;
    }

    public String getResponseId() {
        return responseId;
    }

    public void setResponseId(String responseId) {
        this.responseId = responseId;
    }

    @Override
    public String toString() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("data", data);
            if (!TextUtils.isEmpty(callbackId)) {
                jsonObject.put("callbackId", callbackId);
            }
            if (!TextUtils.isEmpty(responseId)) {
                jsonObject.put("responseId", responseId);
            }
        } catch (Exception e) {
            Log.e("Test", "convert json string error", e);
        }
        return jsonObject.toString();
    }
}
