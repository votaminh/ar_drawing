package com.msc.ar_drawing.utils.i_ringtone;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Downloader extends AsyncTask<String, String, String> {

    private static final String TAG = "SetTone";
    private Activity _context;
    private CustomClick customClick;

    public Downloader(Activity _context, CustomClick customClick) {
        this._context = _context;
        this.customClick = customClick;
    }

    public interface CustomClick{
        void onDone();
    }

    @Override
    protected String doInBackground(String... strArr) {

        for (int position = 0; position < CONST.rawArray.length; position++) {

            String path = StorageConfiguration.getDownloadDirectory().getAbsolutePath();
            String filename = "temp";

            String name = CONST.rawName[position];

            if (name != null && name.contains(".mp3")) {
                filename = name;
            } else {
                filename = name + ".mp3";
            }

            File mFile = new File(path + "/" + filename);

            if (!mFile.exists()) {
                byte[] buffer = null;
                InputStream fIn = _context.getResources().openRawResource(CONST.rawArray[position]);
                int size = 0;

                try {
                    size = fIn.available();
                    buffer = new byte[size];
                    fIn.read(buffer);
                    fIn.close();
                } catch (final IOException e) {
                    return CONST.FAIL_TASK;
                }

                boolean exists = new File(path).exists();
                if (!exists) {
                    new File(path).mkdirs();
                }

                if (!mFile.exists()) {
                    FileOutputStream save;
                    try {
                        save = new FileOutputStream(mFile.getAbsolutePath());
                        save.write(buffer);
                        save.flush();
                        save.close();
                        refreshStorage(_context, mFile.getAbsolutePath());
                    } catch (final FileNotFoundException e) {
                        Log.e(TAG, "FileNotFoundException: " + e.getMessage());
                        return CONST.FAIL_TASK;
                    } catch (final IOException e) {
                        Log.e(TAG, "IOException: " + e.getMessage());
                        return CONST.FAIL_TASK;
                    }
                }
            }
        }

        return CONST.SUCCESS_TASK;
    }


    public void refreshStorage(Context mContext, String filePath) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
                    Uri.parse("file://" + Environment.getExternalStorageDirectory())));
        } else {
            MediaScannerConnection.scanFile(mContext, new String[]{filePath}, null, new MediaScannerConnection.OnScanCompletedListener() {
                /*
                 *   (non-Javadoc)
                 * @see android.media.MediaScannerConnection.OnScanCompletedListener#onScanCompleted(java.lang.String, android.net.Uri)
                 */
                public void onScanCompleted(String path, Uri uri) {
                    Log.i("ExternalStorage", "Scanned " + path + ":");
                    Log.i("ExternalStorage", "-> uri=" + uri);
                }
            });

        }
    }


    @Override
    protected void onPostExecute(String mTask) {
        if (mTask.equals(CONST.SUCCESS_TASK)) {
            Toast.makeText(_context, "Success", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(_context, "Failure", Toast.LENGTH_SHORT).show();
        }
        customClick.onDone();
    }


}
