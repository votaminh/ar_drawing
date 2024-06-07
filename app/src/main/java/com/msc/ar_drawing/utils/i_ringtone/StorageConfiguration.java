package com.msc.ar_drawing.utils.i_ringtone;

import android.os.Build;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;

public class StorageConfiguration {

    private static final String TAG = "StorageConfiguration";
    private static final String APP_FOLDER = "I Ringtone";
    private static final String SHARED_DIR = ".shared";
    private static final String TEMP_DIR = ".temp";
    private static final String SAVE_DIR = "Downloaded";


    private static File createFile(File file) {
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    private static File getBaseDirectory() {
        File file = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            ContextWrapper wrapper = new ContextWrapper(AppCore.getInstance());
//            file = new File(wrapper.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), APP_FOLDER);
            file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_RINGTONES), APP_FOLDER);
        } else {
            file = new File(Environment.getExternalStorageDirectory(), APP_FOLDER);
        }
        return createFile(file);
    }

    public static File getSharedDirectory() {
        File file = new File(getBaseDirectory(), SHARED_DIR);
        return createFile(file);
    }

    public static File getTempDirectory() {
        File file = new File(getBaseDirectory(), TEMP_DIR);
        return createFile(file);
    }

    public static File getDownloadDirectory() {
        File file = new File(getBaseDirectory(), SAVE_DIR);
        return createFile(file);
    }

    public static String getName(String str) {
        long millis = Calendar.getInstance().getTimeInMillis();
        if (str != null && !str.isEmpty()) {
            String name = str.replace(" ", "_").trim().toLowerCase();
            if (str.endsWith(".mp3") || str.endsWith(".MP3")) {
                return "AUD_" + name;
            }
            return "AUD_" + name + ".mp3";
        }
        return "AUD_" + millis + ".mp3";
    }

    public static boolean copyFile(String fromFile, String fileName, String outputPath) {
        InputStream in = null;
        OutputStream out = null;
        try {
            File dir = new File(outputPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }


            if (fileName != null) {
                in = new FileInputStream(fromFile + fileName);
                out = new FileOutputStream(outputPath + fileName);
            } else {
                in = new FileInputStream(fromFile);
                out = new FileOutputStream(outputPath);
            }


            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            out.flush();
            out.close();
            out = null;

            return true;
        } catch (Exception e) {
            if (e instanceof FileNotFoundException) {
                Log.e(TAG, "File Not Found: " + e.getMessage());
            } else {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
            return false;
        }

    }

}
