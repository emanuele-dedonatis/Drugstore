package it.dedonatis.emanuele.drugstore.models;

import java.text.SimpleDateFormat;
import java.util.Date;

import it.dedonatis.emanuele.drugstore.R;
import it.dedonatis.emanuele.drugstore.utils.DateUtils;

public class DrugPackage {
    private long packageID;
    private long drugID;
    private String description;
    private int units;
    private int units_left;
    private int expiration_date;

    public DrugPackage(long packageID, long drugID, String description, int units, int units_left, int expiration_date) {
        this.packageID = packageID;
        this.drugID = drugID;
        this.description = description;
        this.units = units;
        this.units_left = units_left;
        this.expiration_date = expiration_date;
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

    public int getUnits_left() {
        return units_left;
    }

    public int decrementUnits_left() {

        units_left--;
        return units_left;
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
        return drugID + " - " + description + " - " + units + "/" + units_left + " - " + expiration_date;
    }
}
