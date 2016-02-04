package it.dedonatis.emanuele.drugstore.utils;

import android.graphics.Color;

import com.amulyakhare.textdrawable.util.ColorGenerator;

/**
 * Created by mumu on 06/01/16.
 */
public class ColorUtils {

    private static ColorGenerator generator = ColorGenerator.MATERIAL;

    public static int getDarkerColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.8f;
        return Color.HSVToColor(hsv);
    }

    public static int getDrugColor(String drugName, String drugApi) {
        if(drugApi != null)
            return generator.getColor(drugApi);
        else
            return 0;
    }


}
