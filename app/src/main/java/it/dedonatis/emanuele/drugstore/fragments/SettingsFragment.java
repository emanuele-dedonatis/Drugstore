package it.dedonatis.emanuele.drugstore.fragments;


import android.os.Bundle;
import android.preference.PreferenceFragment;

import it.dedonatis.emanuele.drugstore.R;

public class SettingsFragment extends PreferenceFragment {


    public SettingsFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }


}
