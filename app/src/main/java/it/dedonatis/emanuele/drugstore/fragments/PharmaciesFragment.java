package it.dedonatis.emanuele.drugstore.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;

import it.dedonatis.emanuele.drugstore.AsyncTask.PharmacyJsonTask;
import it.dedonatis.emanuele.drugstore.R;

public class PharmaciesFragment extends Fragment implements OnMapReadyCallback {

    final static String LOG_TAG = PharmaciesFragment.class.getSimpleName();
    final static String API_BASE_URL = "https://maps.googleapis.com";
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 0;

    GoogleMap mMap;
    MapView mMapView;

    public PharmaciesFragment() {
    }

    public static PharmaciesFragment newInstance() {
        PharmaciesFragment fragment = new PharmaciesFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        searchPharmacies();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_pharmacies, container, false);

        mMapView = (MapView) fragmentView.findViewById(R.id.mapview);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);

        return fragmentView;
    }

    @Override
    public void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    private void searchPharmacies() {
        PharmacyJsonTask pharmTask = new PharmacyJsonTask();
        pharmTask.execute("45.4803", "9.2237");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_CONTACTS},
                    MY_PERMISSIONS_REQUEST_LOCATION);
        }
        mMap.setMyLocationEnabled(true);
    }
}
