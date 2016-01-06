package it.dedonatis.emanuele.drugstore.utils;

import android.graphics.Color;

/**
 * Created by mumu on 06/01/16.
 */
public class ColorUtils {

    public static int getDarkerColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.8f;
        return Color.HSVToColor(hsv);
    }
}
