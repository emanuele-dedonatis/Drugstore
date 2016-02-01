package it.dedonatis.emanuele.drugstore.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import com.amulyakhare.textdrawable.TextDrawable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImageUtils {

    public static Uri createImageFile(Context context) throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(null);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        return Uri.parse("file:" + image.getAbsolutePath());
    }

    public static Bitmap scaleDown(Bitmap realImage, float maxImageSize,
                                   boolean filter) {
        float ratio = Math.min(
                (float) maxImageSize / realImage.getWidth(),
                (float) maxImageSize / realImage.getHeight());
        int width = Math.round((float) ratio * realImage.getWidth());
        int height = Math.round((float) ratio * realImage.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
                height, filter);
        return newBitmap;
    }

    public static void saveToInternalSorage(Bitmap bitmapImage, Uri uri) throws IOException {
        File mypath = new File(uri.getPath());
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            fos.close();
        }
    }

    public static Bitmap toGrayscale(Bitmap bmpOriginal){
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(bmpOriginal);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }

    public static Bitmap toBinary(Bitmap bmpOriginal) {
        int width, height, threshold;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();
        threshold = 127;
        Bitmap bmpBinary = Bitmap.createBitmap(bmpOriginal);

        for(int x = 0; x < width; ++x) {
            for(int y = 0; y < height; ++y) {
                // get one pixel color
                int pixel = bmpOriginal.getPixel(x, y);
                int gray = (int)(Color.red(pixel) * 0.3 + Color.green(pixel) * 0.59 + Color.blue(pixel) * 0.11);
                //get binary value
                if(gray < threshold){
                    bmpBinary.setPixel(x, y, 0xFF000000);
                } else{
                    bmpBinary.setPixel(x, y, 0xFFFFFFFF);
                }
            }
        }
        return bmpBinary;
    }

    public static Drawable generateRoundLetter(String title, int color) {
        String letter = "";
        if(title != null) {
            letter = String.valueOf(title.charAt(0)).toUpperCase();
        }
        return TextDrawable.builder().buildRound(letter, color);
    }

}
