package it.dedonatis.emanuele.drugstore.models;

import java.text.SimpleDateFormat;

public class Package {
    private int units;
    private int left;
    private SimpleDateFormat expirationDate;

    public Package(int units, int left, SimpleDateFormat expirationDate) {
        this.units = units;
        this.left = left;
        this.expirationDate = expirationDate;
    }

    public int getUnits() {
        return units;
    }

    public void setUnits(int units) {
        this.units = units;
    }

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public SimpleDateFormat getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(SimpleDateFormat expirationDate) {
        this.expirationDate = expirationDate;
    }

    @Override
    public String toString() {
        return left + "/" + units + " (" + expirationDate.toString() + ")";
    }
}
