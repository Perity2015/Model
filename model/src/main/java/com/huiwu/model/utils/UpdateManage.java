package com.huiwu.model.utils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.format.Formatter;

import com.huiwu.model.R;
import com.huiwu.model.http.ConnectionUtil;
import com.huiwu.model.http.StringConnectionCallBack;
import com.lzy.okhttputils.callback.FileCallback;
import com.lzy.okhttputils.request.BaseRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by HuiWu on 2015/9/23.
 */
public class UpdateManage {
    private String HOST = "http://www.yunrfid.com";
    private String CHECK_VERSION_URL = "/CoreSYS.SYS/GetNewAppVer.ajax";
    private Context mContext;
    private String apkVer;
    private String apkName;
    private ProgressDialog dialog;
    private boolean need_toast;

    public UpdateManage(Context context, ProgressDialog dialog, boolean need_toast) {
        this.mContext = context;
        this.need_toast = need_toast;
        this.dialog = dialog;

        this.apkVer = Utils.getAppVersionName(context);
    }

    public void setHOST(String host){
        HOST = host;
    }

    public void checkVersion(String appName) {
        if (TextUtils.isEmpty(apkVer)) {
            return;
        }
        HashMap map = new HashMap();
        this.apkName = appName;
        map.put("appname", appName);
        map.put("ver", this.apkVer);
        ConnectionUtil.postParams(HOST+CHECK_VERSION_URL, map, new StringConnectionCallBack() {
            @Override
            public void sendStart(BaseRequest request) {
                if (need_toast) {
                    UpdateManage.this.dialog.setMessage(mContext.getString(R.string.check_update_load));
                    UpdateManage.this.dialog.show();
                }
            }

            @Override
            public void sendFinish(boolean isFromCache, @Nullable String s, Call call, @Nullable Response response, @Nullable Exception e) {
                if (need_toast) {
                    UpdateManage.this.dialog.dismiss();
                }
            }

            @Override
            public void onParse(String s, Response response) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONObject e = jsonObject.getJSONObject("m_ReturnOBJ");
                    if (e.getBoolean("NewVer")) {
                        String apkUrl = HOST + e.getString("DownUrl");
                        String verName = e.getString("ver");
                        String updateMsg = mContext.getString(R.string.find_new_version_note) + verName;
                        UpdateManage.this.showNoticeDialog(apkUrl, updateMsg);
                    } else {
                        if (need_toast) {
                            Utils.showLongToast(mContext.getString(R.string.have_been_new_version), mContext);
                        }
                    }
                } catch (JSONException var7) {
                    var7.printStackTrace();
                }
            }

            @Override
            public void onParseFailed(@Nullable Response response) {

            }

            @Override
            public void onLost() {

            }
        });
    }

    private void showNoticeDialog(final String apkUrl, String updateMsg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.mContext);
        builder.setTitle(mContext.getString(R.string.new_version_notice));
        builder.setMessage(updateMsg);
        builder.setCancelable(false);
        builder.setPositiveButton(mContext.getString(R.string.new_version_confirm), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                ConnectionUtil.downloadFile(apkUrl, new DownloadFileCallBack(Environment.getExternalStorageDirectory() + "/updateApk", apkName + "_" + DateFormat.format("yyyyMMddkkmmss", System.currentTimeMillis()) + ".apk"));
            }
        });
        builder.show();
    }

    private class DownloadFileCallBack extends FileCallback {

        public DownloadFileCallBack(String destFileDir, String destFileName) {
            super(destFileDir, destFileName);
        }

        @Override
        public void onBefore(BaseRequest request) {
            dialog.setMessage(mContext.getString(R.string.download_load));
            dialog.show();
        }

        @Override
        public void onAfter(boolean isFromCache, @Nullable File file, Call call, @Nullable Response response, @Nullable Exception e) {
            super.onAfter(isFromCache, file, call, response, e);
            dialog.dismiss();
        }

        @Override
        public void onResponse(boolean isFromCache, File file, Request request, Response response) {
            if (file.exists()) {
                Intent i = new Intent("android.intent.action.VIEW");
                i.setDataAndType(Uri.parse("file://" + file.toString()), "application/vnd.android.package-archive");
                UpdateManage.this.mContext.startActivity(i);
            }
        }

        @Override
        public void downloadProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
            String netSpeed = Formatter.formatFileSize(mContext, networkSpeed);
            dialog.setMessage((int) (progress * 100) + "%" + "   " + netSpeed + "/S");
        }

        @Override
        public void onError(boolean isFromCache, Call call, @Nullable Response response, @Nullable Exception e) {
            super.onError(isFromCache, call, response, e);
            Utils.showLongToast(mContext.getString(R.string.download_error), mContext);
        }
    }
}

