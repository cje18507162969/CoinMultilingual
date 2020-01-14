package com.coin.market.util;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.coin.market.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 图片转Base64
 * Created by zhangchengyan on 2017/6/9.
 */
public class Base64Utils {

    private static final String SD_PATH = "/sdcard/dskqxt/pic/";
    private static final String IN_PATH = "/dskqxt/pic/";

    public static String strToBase64(String str) {
        if (!TextUtils.isEmpty(str)) {
            byte[] bytes = str.getBytes();
            str = Base64.encodeToString(bytes, Base64.DEFAULT);
        }
        return str;
    }

    /**
     * 文件转换成Base64
     *
     * @param file 文件
     * @return 转换出来的结果
     */
    public static String imgToBase64(File file) {
        InputStream fis = null;
        String base64 = null;
        try {
            fis = new FileInputStream(file.getPath());
            byte[] bts = new byte[fis.available()];
            Log.i("xxx", "imgToBase64: buts = " + bts.length);
            fis.read(bts);
            base64 = Base64.encodeToString(bts, Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != fis) {
                try {
                    fis.close();
                    fis = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return base64;
    }

    public static String imgToBase64(String path) {
        File file = new File(path);
        return imgToBase64(file);
    }

    /**
     * 位图转换成Base64格式
     *
     * @param bitmap 位图
     * @return 转换成的结果
     */
    public static String imgToBase64(Bitmap bitmap) {
        if (null == bitmap) {
            return null;
        }
        ByteArrayOutputStream out = null;
        try {
            out = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            try {
                out.flush();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            byte[] imgBytes = out.toByteArray();
            return Base64.encodeToString(imgBytes, Base64.DEFAULT);
        } catch (Exception e) {
            return null;
        } finally {
            try {
                out.flush();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //读取Bitmap
    public static Bitmap reaBitmap(String path) {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeFile(path);
        } catch (Exception e) {
            Log.e("xxx", "reaBitmap: 图片读取失败，可能是图片地址无效");
            e.printStackTrace();
        } finally {
            return bitmap;
        }
    }

    /**
     * Base64二进制转为Bitmap
     *
     * @param base64Data 二进制
     * @return
     */
    public static Bitmap decodeBase64Str(String base64Data) {
        if (TextUtils.isEmpty(base64Data)) {
            return null;
        }
        String[] split = new String[2];
        if (base64Data.contains(",")) {
            split = base64Data.split(",");
        }
        byte[] bytes = Base64.decode(split[1], Base64.DEFAULT);

        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    /**
     * 先保存到本地再广播到图库
     *
     * @param context
     * @param bitmap
     * @return
     */
    public static void saveImageToGallery(Context context, Bitmap bitmap) {
        // 首先保存图片
        File appDir = new File(Environment.getExternalStorageDirectory(), "UCOIN");
        if (!appDir.exists())
            appDir.mkdir();
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 其次把文件插入到系统图库
        try {
            //            MediaStore.Images.Media.insertImage(context.getContentResolver(),
            //                    file.getAbsolutePath(), fileName, null);
            savePhotoToMedia(context, file, fileName);
            Toast.makeText(context, context.getResources().getString(R.string.forget_save_pic_success), Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(context, context.getResources().getString(R.string.forget_save_pic_failed), Toast.LENGTH_SHORT).show();
        }

        //         最后通知图库更新
        //        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + android.R.attr.path)));
        Log.e("cjh>>>","存储路径：" + file.getAbsolutePath());
    }

    private static void savePhotoToMedia(Context context, File file, String fileName) throws FileNotFoundException {
        String uriString = MediaStore.Images.Media.insertImage(context.getContentResolver(),
                file.getAbsolutePath(), fileName, null);

        File file1 = new File(getRealPathFromURI(Uri.parse(uriString), context));
        updatePhotoMedia(file1, context);
    }

    //更新图库
    private static void updatePhotoMedia(File file, Context context) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(file));
        context.sendBroadcast(intent);
    }

    //得到绝对地址
    private static String getRealPathFromURI(Uri contentUri, Context context) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String fileStr = cursor.getString(column_index);
        cursor.close();
        return fileStr;
    }

/*    public void gallery(Activity context, int requestCode) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            //权限还没有授予，需要在这里写申请权限的代码
            ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
        } else {
            //权限已经被授予，在这里直接写要执行的相应方法即可

        }
    }*/

    /**
     * Bitmap类型转换成地址
     *
     * @param context
     * @param mBitmap
     * @return
     */
    public static String saveBitmap(Context context, Bitmap mBitmap) {
        String savePath;
        File filePic;
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            savePath = SD_PATH;
        } else {
            savePath = context.getApplicationContext().getFilesDir()
                    .getAbsolutePath()
                    + IN_PATH;
        }
        try {
            filePic = new File(savePath + "ff" + ".jpg");
            if (!filePic.exists()) {
                filePic.getParentFile().mkdirs();
                filePic.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(filePic);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }

        return filePic.getAbsolutePath();
    }
}
