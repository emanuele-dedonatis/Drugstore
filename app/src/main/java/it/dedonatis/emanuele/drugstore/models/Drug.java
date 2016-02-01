package it.dedonatis.emanuele.drugstore.models;

import java.util.List;

import it.dedonatis.emanuele.drugstore.utils.ColorUtils;

public class Drug {
    private long mId;
    private String mName;
    private String mApi;
    private int mColor;

    public Drug(long id, String name, String api) {
        this.mId = id;
        this.mName = name;
        this.mApi = api;
        this.mColor = ColorUtils.getDrugColor(name, api);
    }

    public long getId() {
        return mId;
    }
    public String getName() {
        return mName;
    }

    public String getApi() {
        return mApi;
    }

    public int getColor() {
        return mColor;
    }
}
