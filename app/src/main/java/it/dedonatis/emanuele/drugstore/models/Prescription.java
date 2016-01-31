package it.dedonatis.emanuele.drugstore.models;

import android.net.Uri;

import it.dedonatis.emanuele.drugstore.data.DrugContract;
import it.dedonatis.emanuele.drugstore.utils.ColorUtils;

public class Prescription {

    public static final int EVERY_DAY = 24;
    public static final int EVERY_WEEK = 168;
    public static final int EVERY_MONTH = 672;

    private long prescription_id;
    private long drug_id;
    private String drug_name;
    private String drug_api;
    private long package_id;
    private String packageDescription;
    private int how_much;
    private int every_in_hour;
    private int hour;
    private int until;
    private int drugColor;
    private Uri imageUri;

    public Prescription(long prescription_id, long drug_id, String drug_name, String drug_api, long package_id, String packageDescription, int how_much, int every_in_hour, int hour, int until, Uri imageUri) {
        this.prescription_id = prescription_id;
        this.drug_id = drug_id;
        this.drug_name = drug_name;
        this.packageDescription = packageDescription;
        this.drug_api = drug_api;
        this.package_id = package_id;
        this.how_much = how_much;
        this.every_in_hour = every_in_hour;
        this.hour = hour;
        this.until = until;
        this.drugColor = ColorUtils.getDrugColor(drug_name, drug_api);
        this.imageUri = imageUri;
    }

    public long getPrescription_id() {
        return prescription_id;
    }

    public void setPrescription_id(long prescription_id) {
        this.prescription_id = prescription_id;
    }

    public long getDrug_id() {
        return drug_id;
    }

    public void setDrug_id(long drug_id) {
        this.drug_id = drug_id;
    }

    public String getDrug_name() {
        return drug_name;
    }

    public void setDrug_name(String drug_name) {
        this.drug_name = drug_name;
    }

    public String getDrug_api() {
        return drug_api;
    }

    public void setDrug_api(String drug_api) {
        this.drug_api = drug_api;
    }

    public long getPackage_id() {
        return package_id;
    }

    public void setPackage_id(long package_id) {
        this.package_id = package_id;
    }

    public int getHow_much() {
        return how_much;
    }

    public void setHow_much(int how_much) {
        this.how_much = how_much;
    }

    public int getEveryInHour() {
        return every_in_hour;
    }

    public void setEveryInHour(int every) {
        this.every_in_hour = every;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getUntil() {
        return until;
    }

    public void setUntil(int until) {
        this.until = until;
    }

    public int getDrugColor() {
        return drugColor;
    }

    public void setDrugColor(int drugColor) {
        this.drugColor = drugColor;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }

    public String getPackageDescription() {
        return packageDescription;
    }

    public void setPackageDescription(String packageDescription) {
        this.packageDescription = packageDescription;
    }
}
