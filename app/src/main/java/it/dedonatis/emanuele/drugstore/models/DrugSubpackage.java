package it.dedonatis.emanuele.drugstore.models;

import java.util.Date;

public class DrugSubpackage {
    private long mId;
    private long mDrugId;
    private long mPackId;
    private int mDosesLeft;
    private Date mExpirationDate;

    public DrugSubpackage(long id, long drugID, long packId, int dosesLeft, Date expirationDate) {
        this.mId = id;
        this.mDrugId = drugID;
        this.mPackId = packId;
        this.mDosesLeft = dosesLeft;
        this.mExpirationDate = expirationDate;
    }

    public long getId() {
        return mId;
    }

    public long getDrugID() {
        return mDrugId;
    }

    public long getPackageId() {
        return mPackId;
    }

    public int getDosesLeft() {
        return mDosesLeft;
    }

    public Date getExpirationDate() {
        return mExpirationDate;
    }
}
