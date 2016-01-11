package it.dedonatis.emanuele.drugstore.models;

import android.graphics.Bitmap;
import android.net.Uri;

import java.text.SimpleDateFormat;
import java.util.Date;

import it.dedonatis.emanuele.drugstore.utils.DateUtils;

public class DrugPackage {
    private long packageID;
    private long drugID;
    private String description;
    private int units;
    private boolean isPercentage;
    private int expiration_date;
    private int drugColor;
    private Uri imageUri;

    public DrugPackage(long packageID, long drugID, String description, int units, boolean isPercentage, int expiration_date, Uri imageUri, int drugColor) {
        this.packageID = packageID;
        this.drugID = drugID;
        this.description = description;
        this.units = units;
        this.isPercentage = isPercentage;
        this.expiration_date = expiration_date;
        this.imageUri = imageUri;
        this.drugColor = drugColor;
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

    public Uri getImageUri() {
        return imageUri;
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

    public int getDrugColor() {
        return drugColor;
    }
    @Override
    public String toString() {
        String unitString = units + "";
        if(isPercentage)
            unitString += " %";

        return drugID + " - " + description + " - " + unitString +  " exp." + expiration_date + " img " + imageUri.toString();

    }
}
