package it.dedonatis.emanuele.drugstore.models;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class Drug {
    private String name;
    private List<Package> packages;

    public Drug(String name) {
        this.name = name;
        packages = new ArrayList<Package>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Package> getPackages() {
        return packages;
    }

    public List<Package> setPackages(List<Package> packages) {
        this.packages = packages;
        return this.packages;
    }

    public List<Package> addPackage(Package pack) {
        packages.add(pack);
        return packages;
    }

    @Override
    public String toString() {
        return name + " " + packages.size() + " packages";
    }
}
