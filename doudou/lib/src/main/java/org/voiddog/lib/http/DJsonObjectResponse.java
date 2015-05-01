package org.voiddog.lib.http;

import com.google.gson.Gson;
import com.loopj.android.http.BaseJsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;
import org.voiddog.lib.util.ToastUtil;

import java.lang.reflect.Type;

/**
 *
 * Created by Dog on 2015/4/14.
 */
public abstract class DJsonObjectResponse extends BaseJsonHttpResponseHandler<JSONObject>{
    DResponse response;

    @Override
    public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, JSONObject response) {
        try {
            this.response = new DResponse();
            this.response.message = response.getString("message");
            this.response.code = response.getInt("code");
            this.response.data = response.getString("data");
            onSuccess(statusCode, this.response);
        } catch (JSONException e) {
            e.printStackTrace();
            onFailure(statusCode, e);
        }
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, JSONObject errorResponse) {
        onFailure(statusCode, throwable);
    }

    @Override
    protected JSONObject parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
        if(isFailure) {
            return null;
        }
        return new JSONObject(rawJsonData);
    }

    public static class DResponse{
        private static Gson gson = new Gson();
        public int code;
        public String message;
        public String data;

        public <T> T getData(Type type) {
            try {
                if (code != 0) {
                    ToastUtil.showToast(message);
                    return null;
                }
                return gson.fromJson(data, type);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public abstract void onSuccess(int statusCode, DResponse response);

    public abstract void onFailure(int statusCode, Throwable throwable);
}
