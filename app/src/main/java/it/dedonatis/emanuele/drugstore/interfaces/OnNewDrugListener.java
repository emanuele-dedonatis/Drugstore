package it.dedonatis.emanuele.drugstore.interfaces;

public interface OnNewDrugListener {
        public void addDrug(String description, int units, int isPercentage, int exp_date, byte[] image);
    }