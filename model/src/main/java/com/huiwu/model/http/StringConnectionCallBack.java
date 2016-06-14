package com.huiwu.model.http;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.lzy.okhttputils.callback.AbsCallback;
import com.lzy.okhttputils.request.BaseRequest;

import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by HuiWu on 2016/5/20.
 */
public abstract class StringConnectionCallBack extends AbsCallback<String> {
    private final String TAG = StringConnectionCallBack.class.getSimpleName();

    @Override
    public void onBefore(BaseRequest request) {
        super.onBefore(request);
        sendStart(request);
    }

    @Override
    public String parseNetworkResponse(Response response) throws Exception {
        return response.body().string();
    }

    @Override
    public void onResponse(boolean isFromCache, String s, Request request, @Nullable Response response) {
        if (response != null) {
            Headers responseHeadersString = response.headers();
            String sessionStatus = responseHeadersString.get("sessionStatus");
            if (TextUtils.equals(sessionStatus, "clear")) {
                onLost();
                return;
            }
            try {
                onParse(s, response);
            } catch (Exception e) {
                Log.d(TAG,e.getLocalizedMessage());
                onParseFailed(response);
            }
        } else {
            onParseFailed(response);
        }
    }

    @Override
    public void onAfter(boolean isFromCache, @Nullable String s, Call call, @Nullable Response response, @Nullable Exception e) {
        super.onAfter(isFromCache, s, call, response, e);
        sendFinish(isFromCache,s,call,response,e);
    }

    @Override
    public void onError(boolean isFromCache, Call call, @Nullable Response response, @Nullable Exception e) {
        super.onError(isFromCache, call, response, e);
        onParseFailed(response);
    }

    @Override
    public void upProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
        super.upProgress(currentSize, totalSize, progress, networkSpeed);
        updateProgress(progress, networkSpeed);
    }

    public abstract void sendStart(BaseRequest request);

    public abstract void sendFinish(boolean isFromCache, @Nullable String s, Call call, @Nullable Response response, @Nullable Exception e);

    public abstract void onParse(String s, Response response);

    public abstract void onParseFailed(@Nullable Response response);

    public abstract void onLost();

    public void updateProgress(float progress, long networkSpeed) {

    }
}
