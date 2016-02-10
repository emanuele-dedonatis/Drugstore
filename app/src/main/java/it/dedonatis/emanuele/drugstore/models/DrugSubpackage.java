package it.dedonatis.emanuele.drugstore.models;

import java.util.Date;

import it.dedonatis.emanuele.drugstore.utils.DateUtils;

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

    public int setDosesLeft(int dosesLeft) {
        mDosesLeft = dosesLeft;
        return mDosesLeft;
    }

    public int removeDosesLeft(int dosesToRemove) {
        mDosesLeft -= dosesToRemove;
        return mDosesLeft;
    }

    public Date getExpirationDate() {
        return mExpirationDate;
    }

    public Date setExpirationDate(String dbExpDate) {
        mExpirationDate = DateUtils.fromDbStringToDate(dbExpDate);
        return mExpirationDate;
    }
}
