package it.dedonatis.emanuele.drugstore.models;

public class DrugPackage {
    private long drugID;
    private String description;
    private int units;
    private int units_left;
    private int expiration_date;

    public DrugPackage(long drugID, String description, int units, int units_left, int expiration_date) {
        this.drugID = drugID;
        this.description = description;
        this.units = units;
        this.units_left = units_left;
        this.expiration_date = expiration_date;
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

    public int getExpiration_date() {
        return expiration_date;
    }

    @Override
    public String toString() {
        return drugID + " - " + description + " - " + units + "/" + units_left + " - " + expiration_date;
    }
}
