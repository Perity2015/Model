package com.huiwu.model.http;

import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Log;

import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.AbsCallback;
import com.lzy.okhttputils.callback.FileCallback;
import com.lzy.okhttputils.request.PostRequest;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import okhttp3.Response;

/**
 * Created by HuiWu on 2016/5/20.
 */
public class ConnectionUtil {
    private static final String TAG = ConnectionUtil.class.getSimpleName();

    public static String getResponse(String request_url, Map<String, String> map) throws IOException {
        return getResponse(TAG, request_url, map, null);
    }


    public static String getResponse(Object tag, String request_url, Map<String, String> map) throws IOException {
        return getResponse(tag, request_url, map, null);
    }


    public static String getResponse(String request_url, Map<String, String> map, Map<String, File> fileMap) throws IOException {
        PostRequest request = OkHttpUtils.post(request_url).tag(TAG);

        if (map != null && map.size() > 0) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                request.params(entry.getKey(), entry.getValue());
            }
        }
        if (fileMap != null && fileMap.size() > 0) {
            for (Map.Entry<String, File> entry : fileMap.entrySet()) {
                request.params(entry.getKey(), entry.getValue());
            }
        }

        Response response = request.execute();
        return response.body().string();
    }


    public static String getResponse(Object tag, String request_url, Map<String, String> map, Map<String, File> fileMap) throws IOException {
        PostRequest request = OkHttpUtils.post(request_url).tag(tag);

        if (map != null && map.size() > 0) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                request.params(entry.getKey(), entry.getValue());
            }
        }
        if (fileMap != null && fileMap.size() > 0) {
            for (Map.Entry<String, File> entry : fileMap.entrySet()) {
                request.params(entry.getKey(), entry.getValue());
            }
        }

        Response response = request.execute();
        return response.body().string();
    }


    public static void postParams(String request_url, Map<String, String> map, AbsCallback absCallback) {
        postParams(TAG, request_url, map, null, false, absCallback);
    }


    public static void postParams(Object tag, String request_url, Map<String, String> map, AbsCallback absCallback) {
        postParams(tag, request_url, map, null, false, absCallback);
    }


    public static void postParams(String request_url, Map<String, String> map, Map<String, File> fileMap, AbsCallback absCallback) {
        postParams(TAG, request_url, map, fileMap, true, absCallback);
    }


    public static void postParams(Object tag, String request_url, Map<String, String> map, Map<String, File> fileMap, AbsCallback absCallback) {
        postParams(tag, request_url, map, fileMap, true, absCallback);
    }


    public static void postParams(String request_url, Map<String, String> map, Map<String, File> fileMap, boolean needFile, AbsCallback absCallback) {
        PostRequest request = OkHttpUtils.post(request_url).tag(TAG);
        if (map != null && map.size() > 0) {
            Log.d(TAG, map.toString());
            for (Map.Entry<String, String> entry : map.entrySet()) {
                request.params(entry.getKey(), entry.getValue());
            }
        }
        if (fileMap != null && fileMap.size() > 0 && needFile) {
            for (Map.Entry<String, File> entry : fileMap.entrySet()) {
                request.params(entry.getKey(), entry.getValue());
            }
        }

        request.execute(absCallback);

    }


    public static void postParams(Object tag, String request_url, Map<String, String> map, Map<String, File> fileMap, boolean needFile, AbsCallback absCallback) {
        PostRequest request = OkHttpUtils.post(request_url).tag(tag);
        if (map != null && map.size() > 0) {
            Log.d(TAG, map.toString());
            for (Map.Entry<String, String> entry : map.entrySet()) {
                request.params(entry.getKey(), entry.getValue());
            }
        }
        if (fileMap != null && fileMap.size() > 0 && needFile) {
            for (Map.Entry<String, File> entry : fileMap.entrySet()) {
                request.params(entry.getKey(), entry.getValue());
            }
        }

        request.execute(absCallback);

    }


    public static void downloadFile(String apkUrl, FileCallback fileCallback) {
        OkHttpUtils.get(apkUrl)
                .tag(TAG)
                .execute(fileCallback);
    }


    public static void cancelHttp() {
        OkHttpUtils.getInstance().cancelTag(TAG);
    }


}
