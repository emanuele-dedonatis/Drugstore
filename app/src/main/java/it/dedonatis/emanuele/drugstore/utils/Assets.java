package it.dedonatis.emanuele.drugstore.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by mumu on 21-Jan-16.
 */
public class Assets {

    private final static String LOG_TAG = Assets.class.getSimpleName();

    public static boolean copyToInternalStorage(Context context, String dir, String name){
        File directory = new File(context.getExternalFilesDir(null) + "/" + dir);
        if(!directory.exists()){
            directory.mkdir();
        }
        File dest = new File(directory, name);
        if (directory.exists() && !(dest).exists()) {
            try {
                AssetManager assetManager = context.getAssets();
                InputStream in = assetManager.open(dir + "/" + name);
                OutputStream out = new FileOutputStream(dest);

                // Transfer bytes from in to out
                byte[] buf = new byte[5120];
                int len = in.read(buf);
                while (len > 0) {
                    out.write(buf, 0, len);
                    len = in.read(buf);
                }
                in.close();
                out.close();
            } catch (IOException e) {
                return false;
            }
        }
        return  true;
    }

}
