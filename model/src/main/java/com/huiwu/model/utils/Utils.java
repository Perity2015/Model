package com.huiwu.model.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.nfc.NdefRecord;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.format.DateFormat;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Locale;

/**
 * Created by HuiWu on 2015/9/23.
 */
public class Utils {
    public Utils() {
    }

    /**
     * 创建图片文件名
     *
     * @return
     */
    public static String createFileName() {
        return DateFormat.format("\'IMG\'_yyyyMMdd_kkmmss", System.currentTimeMillis()) + ".jpg";
    }

    /**
     * 创建图片文件路径
     *
     * @param appName     应用名称->创建相关目录
     * @param pictureName 图片文件名
     * @return
     */
    public static File getPictureFile(String appName, String pictureName) {
        File file_dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File file_picture = new File(file_dir, appName);
        if (!file_picture.exists()) {
            file_picture.mkdirs();
        }
        File file = new File(file_picture, pictureName);
        return file;
    }

    /**
     * 创建NDEF信息
     *
     * @param type  text 文本类容 url 网址类容
     * @param value 类容
     * @return
     */
    public static NdefRecord createNdefRecord(String type, String value) {
        //生成语言编码的字节数组，中文编码
        byte[] langBytes = Locale.CHINA.getLanguage().getBytes(Charset.forName("US-ASCII"));
        //将要写入的文本以UTF_8格式进行编码
        Charset utfEncoding = Charset.forName("UTF-8");
        //由于已经确定文本的格式编码为UTF_8，所以直接将payload的第1个字节的第7位设为0
        byte[] valueBytes = value.getBytes(utfEncoding);
        int utfBit = 0;
        //定义和初始化状态字节
        char status = (char) (utfBit + langBytes.length);
        //创建存储payload的字节数组
        byte[] data = new byte[1 + langBytes.length + valueBytes.length];
        //设置状态字节
        data[0] = (byte) status;
        //设置语言编码
        System.arraycopy(langBytes, 0, data, 1, langBytes.length);
        //设置实际要写入的文本
        System.arraycopy(valueBytes, 0, data, 1 + langBytes.length, valueBytes.length);
        //根据前面设置的payload创建NdefRecord对象
        NdefRecord record;
        if (type.equals("text")) {
            record = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], data);
        } else {
            record = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_URI, new byte[0], data);
        }
        return record;
    }

    /**
     * 格式化服务端返回的时间 /Date(1463801868000)/
     *
     * @param datetime
     * @return
     */
    public static String formatDateTime(String datetime) {
        try {
            return DateFormat.format("yyyy-MM-dd kk:mm:ss", Long.parseLong(datetime.substring(6, datetime.length() - 2))).toString();
        } catch (Exception var2) {
            return DateFormat.format("yyyy-MM-dd kk:mm:ss", System.currentTimeMillis()).toString();
        }
    }

    /**
     * 解析Long时间
     *
     * @param datetime
     * @return
     */
    public static String formatDateTimeOffLine(long datetime) {
        try {
            return DateFormat.format("yyyy-MM-dd kk:mm:ss", datetime).toString();
        } catch (Exception var3) {
            return DateFormat.format("yyyy-MM-dd kk:mm:ss", System.currentTimeMillis()).toString();
        }
    }

    /**
     * byte[] 转为指定长度的HEX String
     *
     * @param bytes
     * @param size
     * @return
     */
    public static String toHexString(byte[] bytes, int size) {
        if (bytes != null && bytes.length >= 1) {
            StringBuilder hexString = new StringBuilder(2 * size);

            for (int i = 0; i < size; ++i) {
                String sTemp = Integer.toHexString(255 & bytes[i]);
                if (sTemp.length() < 2) {
                    hexString.append(0);
                }
                hexString.append(sTemp.toUpperCase());
            }

            return hexString.toString();
        } else {
            throw new IllegalArgumentException("this bytes must not be null or empty");
        }
    }

    /**
     * byte[] 转为HEX String
     *
     * @param bytes
     * @return
     */
    public static String bytesToHexString(byte[] bytes) {
        StringBuffer sb = new StringBuffer(bytes.length);

        for (int i = 0; i < bytes.length; ++i) {
            String sTemp = Integer.toHexString(255 & bytes[i]);
            if (sTemp.length() < 2) {
                sb.append(0);
            }
            sb.append(sTemp.toUpperCase());
        }

        return sb.toString();
    }

    /**
     * 检查是否有网络连接
     *
     * @param context
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                NetworkInfo.State state = mNetworkInfo.getState();
                return mNetworkInfo.isAvailable() && (state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING);
            }
        }

        return false;
    }

    /**
     * 检查是否有WIFI连接
     *
     * @param context
     * @return
     */
    public static boolean isWifiConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWiFiNetworkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (mWiFiNetworkInfo != null) {
                NetworkInfo.State state = mWiFiNetworkInfo.getState();
                return mWiFiNetworkInfo.isAvailable() && (state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING);
            }
        }

        return false;
    }

    /**
     * 检查是否有移动网络连接
     *
     * @param context
     * @return
     */
    public static boolean isMobileConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mMobileNetworkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (mMobileNetworkInfo != null) {
                NetworkInfo.State state = mMobileNetworkInfo.getState();
                return mMobileNetworkInfo.isAvailable() && (state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING);
            }
        }

        return false;
    }

    /**
     * 检查语言环境 是否为中文
     *
     * @param context
     * @return
     */
    public static boolean isZh(Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        return language.endsWith("zh");
    }

    public static void showShortToast(String message, Context context) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    public static void showShortToast(int message, Context context) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    public static void showLongToast(String message, Context context) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        toast.show();
    }

    public static void showLongToast(int message, Context context) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        toast.show();
    }

    public static String bytes2HexString(byte[] bytes, int start, int end) {
        StringBuffer sb = new StringBuffer();

        for (int i = start; i < start + end; ++i) {
            sb.append(String.format("%02X", 255 & bytes[i]));
        }

        return sb.toString();
    }

    public static byte[] string2Bytes(String s, int length) {
        byte[] bytes1 = s.getBytes();
        byte[] bytes2 = new byte[length];
        System.arraycopy(bytes1, 0, bytes2, 0, Math.min(length, bytes1.length));
        return bytes2;
    }

    public static String getDeviceId(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId();
    }

    public static Bitmap dealWithPicture(String path, int dealWidth, int dealHeight) {
        FileOutputStream b = null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 4;
            Bitmap e = BitmapFactory.decodeFile(path, options);
            int width = e.getWidth();
            int height = e.getHeight();
            if (width > height) {
                Matrix m = new Matrix();
                m.setRotate(90.0F);
                e = Bitmap.createScaledBitmap(e, dealWidth, (int) ((float) height * ((float) dealWidth / (float) width)), true);
                e = Bitmap.createBitmap(e, 0, 0, e.getWidth(), e.getHeight(), m, true);
            } else {
                e = Bitmap.createScaledBitmap(e, dealHeight, (int) ((float) height * ((float) dealHeight / (float) width)), true);
            }

            b = new FileOutputStream(path);
            e.compress(Bitmap.CompressFormat.JPEG, 100, b);
            return e;
        } catch (FileNotFoundException var16) {
            var16.printStackTrace();
            return null;
        } finally {
            if (b != null) {
                try {
                    b.close();
                } catch (IOException var15) {
                    var15.printStackTrace();
                }
            }

        }

    }

    /**
     * View视图转为BitMap
     *
     * @param view
     * @return
     */
    public static Bitmap convertViewToBitmap(View view) {
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();

        return bitmap;
    }

    public static void fileSave(String filename, String content, Context context) {
        FileOutputStream fos = null;

        try {
            fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
            fos.write(content.getBytes());
            fos.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

    }

    public static String fileRead(String filename, Context context) {
        FileInputStream fis = null;
        ByteArrayOutputStream bos = null;

        String result;
        try {
            fis = context.openFileInput(filename);
            byte[] e = new byte[fis.available()];
            bos = new ByteArrayOutputStream();

            while (fis.read(e) != -1) {
                if (e.length == 0) {
                    return null;
                }

                bos.write(e);
                bos.flush();
            }

            result = new String(bos.toByteArray());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        return result;
    }

    public static boolean saveRecordToFile(String file_dir, String message) {
        String dateFormat = "yyyy-MM-dd";
        String monthFormat = "yyyy-MM";
        String timeFormat = "yyyy-MM-dd kk:mm:ss";
        String file_name = DateFormat.format(dateFormat, System.currentTimeMillis()) + ".txt";

        try {
            File folder = new File(Environment.getExternalStorageDirectory(), file_dir);
            if (!folder.exists()) {
                folder.mkdir();
            }

            File files = new File(folder, DateFormat.format(monthFormat, System.currentTimeMillis()).toString());
            if (!files.exists()) {
                files.mkdir();
            }

            File file = new File(files, file_name);
            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fos = new FileWriter(file, true);
            fos.append(String.format("%n%s%n", new Object[]{DateFormat.format(timeFormat, System.currentTimeMillis())}));
            fos.append(String.format("%n%s%n", new Object[]{message}));
            fos.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static void saveUserImage(Context context, Bitmap bitmap, String filename) {
        FileOutputStream fos = null;

        try {
            fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
            if (bitmap != null) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

    }

    public static Bitmap getUserImage(Context context, String filename) {
        FileInputStream fis = null;

        try {
            fis = context.openFileInput(filename);
            Bitmap var5 = BitmapFactory.decodeStream(fis);
            return var5;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        return null;
    }

    /**
     * 关闭软键盘
     *
     * @param activity
     */
    public static void hideInputSoft(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 判断当前Activity所属进程是否为前台进程
     *
     * @param context
     * @return
     */
    public static boolean isAppOnForeground(Context context) {
        // 首先获取activity管理器和当前进程的包名
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = context.getPackageName();

        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null)
            return false;

        // 遍历当前运行的所有进程，查看本进程是否是前台进程，如果不是就代表当前应用进入了后台，执行相关关闭操作
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            // The name of the process that this object is associated with.
            if (appProcess.processName.equals(packageName)
                    && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }
        return false;
    }

}

