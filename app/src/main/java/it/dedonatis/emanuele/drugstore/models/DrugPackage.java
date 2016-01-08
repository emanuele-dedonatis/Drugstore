package it.dedonatis.emanuele.drugstore.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import java.text.SimpleDateFormat;
import java.util.Date;

import it.dedonatis.emanuele.drugstore.R;
import it.dedonatis.emanuele.drugstore.utils.DateUtils;

public class DrugPackage {
    private long packageID;
    private long drugID;
    private String description;
    private int units;
    private boolean isPercentage;
    private int expiration_date;
    private byte[] image;

    public DrugPackage(long packageID, long drugID, String description, int units, boolean isPercentage, int expiration_date, byte[] image) {
        this.packageID = packageID;
        this.drugID = drugID;
        this.description = description;
        this.units = units;
        this.isPercentage = isPercentage;
        this.expiration_date = expiration_date;
        this.image = image;
    }

    public long getPackageID() {
        return packageID;
    }

    public long getDrugID() {
        return drugID;
    }

    public String getDescription() {
        return description;
    }

    public int getUnits() {
        return units;
    }

    public boolean isPercentage() {
        return isPercentage;
    }

    public Bitmap getImageBitmap() {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    public int getExpiration_date() {
        return expiration_date;
    }

    public Date getParsedExpriartion_date() {
        return DateUtils.parseDate(expiration_date + "");
    }

    public String getStringExpriartion_date() {
        SimpleDateFormat simpleDate =  new SimpleDateFormat("dd/MM/yyyy");
        return "EXP" + " " + simpleDate.format(getParsedExpriartion_date());
    }
    @Override
    public String toString() {
        String unitString = units + "";
        if(isPercentage)
            unitString += " %";

        return drugID + " - " + description + " - " + unitString +  " - " + expiration_date;

    }
}
