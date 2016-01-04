package it.dedonatis.emanuele.drugstore.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Pharmacies {

    @Expose
    @SerializedName("d")
    private List<Pharmacy> pharmacies = new ArrayList<Pharmacy>();

    public List<Pharmacy> getPharmacies() {
        return pharmacies;
    }

    public int size() {
        return pharmacies.size();
    }

    public Pharmacy get(int location) {
        return pharmacies.get(location);
    }
}
