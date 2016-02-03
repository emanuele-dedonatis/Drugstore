package it.dedonatis.emanuele.drugstore.models;

import android.net.Uri;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import it.dedonatis.emanuele.drugstore.utils.DateUtils;

public class DrugPackage {
    private long mId;
    private long mDrugId;
    private String mDescription;
    private int mDoses;
    private Uri mImageUri;
    private int mDrugColor;
    private List<DrugSubpackage> mSubpackages = new ArrayList<DrugSubpackage>();

    public DrugPackage(long id, long drugID, String description, int doses, String imageUri, int drugColor, List<DrugSubpackage> subpackages) {
        this.mId = id;
        this.mDrugId = drugID;
        this.mDescription = description;
        this.mDoses = doses;
        if(imageUri != null)
            this.mImageUri = Uri.parse(imageUri);
        this.mDrugColor = drugColor;
        if(subpackages!=null)
            this.mSubpackages = subpackages;
    }

    public long getId() {
        return mId;
    }

    public long getDrugID() {
        return mDrugId;
    }

    public String getDescription() {
        return mDescription;
    }

    public int getDoses() {
        return mDoses;
    }
    public int setDosesLeft(int dosesLeft) {
        mDoses = dosesLeft;
        return mDoses;
    }

    public int removeDosesLeft(int dosesToRemove) {
        mDoses -= dosesToRemove;
        return mDoses;
    }
    public Uri getImageUri() {
        return mImageUri;
    }

    public int getDrugColor() {
        return mDrugColor;
    }

    public List<DrugSubpackage> getSubpackages() {
        return mSubpackages;
    }
}
