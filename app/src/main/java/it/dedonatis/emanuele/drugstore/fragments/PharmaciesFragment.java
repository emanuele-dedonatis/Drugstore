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


import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import it.dedonatis.emanuele.drugstore.R;
import it.dedonatis.emanuele.drugstore.models.Pharmacies;
import it.dedonatis.emanuele.drugstore.rest.PharmacyRestService;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;

public class PharmaciesFragment extends Fragment implements OnMapReadyCallback {

    final static String LOG_TAG = PharmaciesFragment.class.getSimpleName();
    final static String API_BASE_URL = "http://opendatasalutedata.cloudapp.net";
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
        //setupPharmaciesView();
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

    private void setupPharmaciesView() {

         Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        PharmacyRestService restService = retrofit.create(PharmacyRestService.class);

        String descrizionecomune = "CHIETI";
        restService.getPharmacies("descrizionecomune eq '" + descrizionecomune + "'", "json").enqueue(
                new Callback<Pharmacies>() {

                    @Override
                    public void onResponse(Response<Pharmacies> response,
                                           Retrofit retrofit) {
                        if (response.isSuccess()) {
                            Pharmacies pharmacies = response.body();
                            Log.v(LOG_TAG, pharmacies.size() + "");
                            for (int i = 0; i < pharmacies.size(); i++)
                                Log.v(LOG_TAG, i + ": " + pharmacies.get(i).toString());

                        }else {
                            Log.e(LOG_TAG, response.message());
                        }

                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Log.v(LOG_TAG, "FAIL: " + t.toString());
                    }
                });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_CONTACTS},
                    MY_PERMISSIONS_REQUEST_LOCATION);
        }
        mMap.setMyLocationEnabled(true);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(43.1, -87.9), 10));
    }
}
