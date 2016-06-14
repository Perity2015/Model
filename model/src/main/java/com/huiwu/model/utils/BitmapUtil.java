package com.huiwu.model.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by HuiWu on 2016/6/4.
 */
public class BitmapUtil {

    /**
     * 质量压缩
     *
     * @param image
     * @param imageSize
     * @return
     */
    public static Bitmap compressImage(Bitmap image, int imageSize) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > imageSize) {  //循环判断压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

    /**
     * 按指定大小不降质量存储
     *
     * @param bitmap
     * @param file
     * @param imageSize
     */
    public static void saveSpecifiedSizeBitmap(Bitmap bitmap, File file, int imageSize) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        FileOutputStream fos = null;
        try {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            float inSampleSize = 1.0f;

            while (baos.toByteArray().length / 1024 > imageSize) {  //循环判断压缩后图片是否大于100kb,大于继续压缩
                baos.reset();
                Matrix matrix = new Matrix();
                matrix.postScale(inSampleSize, inSampleSize); //长和宽放大缩小的比例
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);//这里压缩options%，把压缩后的数据存放到baos中
                inSampleSize -= 0.1f;
            }

            fos = new FileOutputStream(file);

            fos.write(baos.toByteArray());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
