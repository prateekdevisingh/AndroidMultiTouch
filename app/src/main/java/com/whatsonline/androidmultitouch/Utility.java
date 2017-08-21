package com.whatsonline.androidmultitouch;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Prateek on 21/08/17.
 */

public class Utility {


    public static File getOutputMediaFile(Context context)

    {
        try {
            File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), context.getResources().getString(R.string.app_name));

            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    return null;
                }
            }

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            File mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");

            return mediaFile;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("sdfsf", e.getMessage());
            return null;
        }
    }
}
