package com.lzp.jsbridge.library;

import android.util.Log;

import org.json.JSONObject;

class JsBridgeMsg {
    private String data;
    private long callbackId;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public long getCallbackId() {
        return callbackId;
    }

    public void setCallbackId(long callbackId) {
        this.callbackId = callbackId;
    }

    @Override
    public String toString() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("data", data);
            jsonObject.put("callbackId", callbackId);
        } catch (Exception e) {
            Log.e("Test", "convert json string error", e);
        }
        return jsonObject.toString();
    }
}
